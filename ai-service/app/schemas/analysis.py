"""
Pydantic schemas for the analysis endpoint.
These define the contract for AI-generated insights and evidence.
Uses no default values to remain compatible with Gemini structured output.
"""

from typing import Any, Optional
from pydantic import BaseModel, Field


class AnalyzeRequest(BaseModel):
    """Input to the /ai/v1/analyze endpoint."""
    data: Any = Field(
        ..., description="Crime/criminal data fetched from the database"
    )
    original_query: str = Field(
        ..., description="The investigator's original natural language query", max_length=2000
    )
    context: Optional[str] = Field(
        default=None, description="Additional context for the analysis"
    )


class EvidenceItem(BaseModel):
    """A single piece of evidence supporting an AI claim."""
    id: str
    claim: str = Field(description="The factual claim being made")
    sources: list[str] = Field(description="FIR numbers, record IDs, or data points supporting this claim")
    confidence: float = Field(ge=0.0, le=1.0, description="Confidence score 0-1")
    type: str = Field(description="Evidence type: criminal_link, pattern, location, modus_operandi, temporal")


class InsightItem(BaseModel):
    """An analytical insight derived from the data."""
    title: str
    description: str
    severity: str = Field(description="Severity: info, warning, critical")
    related_entities: list[str] = Field(description="Entities related to this insight")


class AnalyzeResponse(BaseModel):
    """Output from the /ai/v1/analyze endpoint. Used as Gemini structured output schema."""
    summary: str = Field(description="Natural language summary of the analysis")
    insights: list[InsightItem] = Field(description="List of analytical insights")
    evidence: list[EvidenceItem] = Field(description="List of supporting evidence")
    confidence: float = Field(ge=0.0, le=1.0, description="Overall confidence in the analysis")
    suggested_followups: list[str] = Field(description="Suggested follow-up questions for the investigator")
