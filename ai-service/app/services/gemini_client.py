"""
Gemini API client wrapper.
Centralizes all Gemini API calls with error handling, logging, and retry logic.
"""

import asyncio
import time
import logging
from typing import Any

from app.config import settings

logger = logging.getLogger(__name__)


class GeminiClient:
    """Wrapper around the Google Gemini API with structured output support."""

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
        temperature: float = 0.4,
        max_retries: int = 1,
    ) -> Any:
        """
        Generate a structured JSON response from Gemini.
        Uses asyncio.to_thread to avoid blocking the event loop.
        Retries on failure up to max_retries times.
        """
        from google.genai import types

        last_error = None

        for attempt in range(max_retries + 1):
            start_time = time.time()
            try:
                # Wrap API call with an 8-second timeout to prevent hangs
                response = await asyncio.wait_for(
                    asyncio.to_thread(
                        self._client.models.generate_content,
                        model=self.model,
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
                    "Gemini call completed in %.0fms | model=%s | attempt=%d | tokens_in=%s | tokens_out=%s",
                    elapsed_ms,
                    self.model,
                    attempt + 1,
                    getattr(response.usage_metadata, "prompt_token_count", "?"),
                    getattr(response.usage_metadata, "candidates_token_count", "?"),
                )

                # Parse the structured response
                parsed = response_schema.model_validate_json(response.text)
                return parsed

            except Exception as e:
                elapsed_ms = (time.time() - start_time) * 1000
                last_error = e
                if attempt < max_retries:
                    logger.warning(
                        "Gemini call failed after %.0fms (attempt %d/%d): %s. Retrying...",
                        elapsed_ms, attempt + 1, max_retries + 1, str(e),
                    )
                    await asyncio.sleep(0.3 * (attempt + 1))
                else:
                    logger.error(
                        "Gemini call failed after %.0fms (all %d attempts exhausted): %s",
                        elapsed_ms, max_retries + 1, str(e),
                    )

        raise last_error  # type: ignore

    async def generate_text(
        self,
        prompt: str,
        system_instruction: str,
        temperature: float = 0.3,
    ) -> str:
        """
        Generate a plain text response from Gemini.
        Uses asyncio.to_thread to avoid blocking the event loop.
        """
        from google.genai import types

        response = await asyncio.wait_for(
            asyncio.to_thread(
                self._client.models.generate_content,
                model=self.model,
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


# Lazy-initialized singleton
_client: GeminiClient | None = None


def get_gemini_client() -> GeminiClient:
    """Get or create the Gemini client singleton."""
    global _client
    if _client is None or _client.api_key != settings.gemini_api_key or _client.model != settings.gemini_model:
        _client = GeminiClient()
    return _client
