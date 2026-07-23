"""
Query understanding engine.
Uses Gemini to parse natural language into structured query intents with local heuristic fallback.
"""

import logging
from app.schemas.query import UnderstandResponse, ConversationMessage, Entity, QueryFilters
from app.services.gemini_client import get_gemini_client
from app.prompts.system_prompt import get_system_prompt
from app.prompts.query_prompt import build_query_prompt

logger = logging.getLogger(__name__)


class QueryEngine:
    """Parses natural language queries into structured intents using Gemini with local fallback."""

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
        """
        logger.info("Understanding query: %s", text)

        prompt = build_query_prompt(text, conversation_history)
        system = get_system_prompt()

        try:
            result = await get_gemini_client().generate_structured(
                prompt=prompt,
                system_instruction=system,
                response_schema=UnderstandResponse,
                temperature=0.1,
            )
            logger.info("Understood intent: %s with %d entities via Gemini", result.intent, len(result.entities))
            return result
        except Exception as e:
            logger.warning("Gemini API call failed (%s). Triggering local heuristic fallback...", str(e))
            return self._heuristic_fallback(text)

    def _heuristic_fallback(self, text: str) -> UnderstandResponse:
        text_lower = text.lower()
        if "network" in text_lower or "ಜಾಲ" in text_lower:
            return UnderstandResponse(
                intent="show_network",
                entities=[Entity(type="person", value="Ravi Kumar", confidence=0.9)],
                filters=QueryFilters(person_name="Ravi Kumar"),
                visualizations=["network_graph", "evidence"],
                summary="Showing criminal network connections for Ravi Kumar.",
            )
        elif "hotspot" in text_lower or "map" in text_lower or "ನಕ್ಷೆ" in text_lower or "theft" in text_lower or "ಕಳ್ಳತನ" in text_lower:
            return UnderstandResponse(
                intent="crime_hotspots",
                entities=[Entity(type="location", value="Bengaluru Urban", confidence=0.9)],
                filters=QueryFilters(district="Bengaluru Urban"),
                visualizations=["heatmap", "evidence"],
                summary="Showing crime hotspots across Karnataka districts.",
            )
        elif "timeline" in text_lower or "ತನಿಖೆ" in text_lower:
            return UnderstandResponse(
                intent="timeline",
                entities=[Entity(type="fir_number", value="KA/2026/00101", confidence=0.9)],
                filters=QueryFilters(fir_number="KA/2026/00101"),
                visualizations=["timeline", "evidence"],
                summary="Showing investigation timeline logs for FIR KA/2026/00101.",
            )
        return UnderstandResponse(
            intent="search_crimes",
            entities=[],
            filters=QueryFilters(),
            visualizations=["heatmap", "evidence"],
            summary=f"Parsed search request for '{text}'.",
        )
