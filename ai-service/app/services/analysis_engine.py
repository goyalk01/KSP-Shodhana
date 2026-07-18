"""
Analysis engine.
Uses Gemini to analyze crime data and produce evidence-backed insights.
"""

import logging
from typing import Any, Optional
from app.schemas.analysis import AnalyzeResponse
from app.services.gemini_client import get_gemini_client
from app.prompts.system_prompt import get_system_prompt
from app.prompts.analysis_prompt import build_analysis_prompt

logger = logging.getLogger(__name__)


class AnalysisEngine:
    """Analyzes crime/criminal data and produces insights using Gemini."""

    async def analyze(
        self,
        data: dict[str, Any],
        original_query: str,
        context: Optional[str] = None,
    ) -> AnalyzeResponse:
        """
        Analyze fetched crime data and produce:
        - Natural language summary
        - Analytical insights
        - Evidence items with confidence scores
        - Suggested follow-up questions

        Args:
            data: Crime/criminal records fetched from Catalyst Data Store.
            original_query: The investigator's original question.
            context: Additional context for the analysis.

        Returns:
            Structured AnalyzeResponse with insights and evidence.
        """
        logger.info("Analyzing data for query: %s", original_query)

        prompt = build_analysis_prompt(data, original_query, context)
        system = get_system_prompt()

        result = await get_gemini_client().generate_structured(
            prompt=prompt,
            system_instruction=system,
            response_schema=AnalyzeResponse,
            temperature=0.3,  # Slightly higher for creative analysis
        )

        logger.info(
            "Analysis complete: %d insights, %d evidence items, confidence=%.2f",
            len(result.insights),
            len(result.evidence),
            result.confidence,
        )
        return result
