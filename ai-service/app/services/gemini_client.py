"""
Gemini API client wrapper.
Centralizes all Gemini API calls with error handling, logging, retry logic, and candidate model failovers.
"""

import asyncio
import time
import logging
from typing import Any

from app.config import settings

logger = logging.getLogger(__name__)


class GeminiClient:
    """Wrapper around the Google Gemini API with structured output support and model failovers."""

    def __init__(self) -> None:
        from google import genai

        self.api_key = settings.gemini_api_key
        self.model = settings.gemini_model
        self._client = genai.Client(api_key=self.api_key)
        logger.info("GeminiClient initialized with model: %s", self.model)

    async def generate_structured(
        self,
        prompt: str,
        system_instruction: str,
        response_schema: type,
        temperature: float = 0.2,
        max_retries: int = 1,
    ) -> Any:
        """
        Generate a structured JSON response from Gemini.
        Uses asyncio.to_thread to avoid blocking the event loop.
        Tries fallback models if primary model is unavailable.
        """
        from google.genai import types

        candidate_models = [self.model, "gemini-2.5-flash", "gemini-1.5-flash", "gemini-1.5-pro"]
        # Remove duplicates while preserving order
        unique_models = []
        for m in candidate_models:
            if m and m not in unique_models:
                unique_models.append(m)

        last_error = None

        for target_model in unique_models:
            for attempt in range(max_retries + 1):
                start_time = time.time()
                try:
                    response = await asyncio.wait_for(
                        asyncio.to_thread(
                            self._client.models.generate_content,
                            model=target_model,
                            contents=prompt,
                            config=types.GenerateContentConfig(
                                system_instruction=system_instruction,
                                response_mime_type="application/json",
                                response_schema=response_schema,
                                temperature=temperature,
                                max_output_tokens=settings.gemini_max_tokens,
                            ),
                        ),
                        timeout=8.0,
                    )

                    elapsed_ms = (time.time() - start_time) * 1000
                    logger.info(
                        "Gemini call completed in %.0fms | model=%s | attempt=%d",
                        elapsed_ms,
                        target_model,
                        attempt + 1,
                    )

                    parsed = response_schema.model_validate_json(response.text)
                    return parsed

                except Exception as e:
                    elapsed_ms = (time.time() - start_time) * 1000
                    last_error = e
                    logger.warning(
                        "Gemini model '%s' failed after %.0fms (attempt %d/%d): %s",
                        target_model, elapsed_ms, attempt + 1, max_retries + 1, str(e),
                    )
                    if attempt < max_retries:
                        await asyncio.sleep(0.2 * (attempt + 1))

        raise last_error  # type: ignore

    async def generate_text(
        self,
        prompt: str,
        system_instruction: str,
        temperature: float = 0.3,
    ) -> str:
        """Generate plain text from Gemini with model fallback."""
        from google.genai import types

        candidate_models = [self.model, "gemini-2.5-flash", "gemini-1.5-flash", "gemini-1.5-pro"]
        unique_models = [m for idx, m in enumerate(candidate_models) if m and m not in candidate_models[:idx]]

        last_err = None
        for target_model in unique_models:
            try:
                response = await asyncio.wait_for(
                    asyncio.to_thread(
                        self._client.models.generate_content,
                        model=target_model,
                        contents=prompt,
                        config=types.GenerateContentConfig(
                            system_instruction=system_instruction,
                            temperature=temperature,
                            max_output_tokens=settings.gemini_max_tokens,
                        ),
                    ),
                    timeout=8.0,
                )
                return response.text
            except Exception as e:
                last_err = e

        raise last_err  # type: ignore


# Lazy-initialized singleton
_client: GeminiClient | None = None


def get_gemini_client() -> GeminiClient:
    """Get or create the Gemini client singleton."""
    global _client
    if _client is None or _client.api_key != settings.gemini_api_key or _client.model != settings.gemini_model:
        _client = GeminiClient()
    return _client
