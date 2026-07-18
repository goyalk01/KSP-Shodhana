"""
Query understanding engine.
Uses Gemini to parse natural language into structured query intents.
"""

import logging
from app.schemas.query import UnderstandResponse, ConversationMessage
from app.services.gemini_client import get_gemini_client
from app.prompts.system_prompt import get_system_prompt
from app.prompts.query_prompt import build_query_prompt

logger = logging.getLogger(__name__)


class QueryEngine:
    """Parses natural language queries into structured intents using Gemini."""

    async def understand(
        self,
        text: str,
        conversation_history: list[ConversationMessage],
    ) -> UnderstandResponse:
        """
        Understand a natural language query and extract:
        - Intent (what action to take)
        - Entities (people, places, crime types, dates)
        - Filters (structured query parameters)
        - Visualizations (which UI panels to show)

        Args:
            text: The investigator's query in natural language.
            conversation_history: Previous messages for multi-turn context.

        Returns:
            Structured UnderstandResponse with intent and parameters.
        """
        logger.info("Understanding query: %s", text)

        prompt = build_query_prompt(text, conversation_history)
        system = get_system_prompt()

        result = await get_gemini_client().generate_structured(
            prompt=prompt,
            system_instruction=system,
            response_schema=UnderstandResponse,
            temperature=0.1,  # Low temperature for consistent parsing
        )

        logger.info("Understood intent: %s with %d entities", result.intent, len(result.entities))
        return result
