"""
Pydantic schemas for the query understanding endpoint.
These define the contract between Spring Boot and FastAPI for intent parsing.
Uses separate request/response models to avoid Gemini structured output limitations.
"""

from typing import Literal, Optional
from pydantic import BaseModel, Field


class ConversationMessage(BaseModel):
    """A single message in the conversation history."""
    role: Literal["user", "assistant"]
    content: str


class UnderstandRequest(BaseModel):
    """Input to the /ai/v1/understand endpoint."""
    text: str = Field(
        ..., description="Natural language query from the investigator", max_length=2000
    )
    conversation_history: list[ConversationMessage] = Field(
        default_factory=list,
        description="Previous conversation messages for multi-turn context (max 5)",
    )


class Entity(BaseModel):
    """An extracted entity from the query."""
    type: str = Field(description="Entity type: person, location, crime_type, date_range, fir_number, status, weapon")
    value: str
    confidence: float = Field(ge=0.0, le=1.0)


class QueryFilters(BaseModel):
    """Structured filters extracted from the natural language query."""
    crime_type: Optional[str] = None
    district: Optional[str] = None
    station: Optional[str] = None
    status: Optional[str] = None
    severity: Optional[str] = None
    date_from: Optional[str] = None
    date_to: Optional[str] = None
    person_name: Optional[str] = None
    fir_number: Optional[str] = None


class UnderstandResponse(BaseModel):
    """Output from the /ai/v1/understand endpoint. Used as Gemini structured output schema."""
    intent: str = Field(description="Query intent: search_crimes, find_criminal, show_network, crime_hotspots, timeline, crime_stats, general_question")
    entities: list[Entity] = Field(description="Extracted entities from the query")
    filters: QueryFilters = Field(description="Extracted query filters")
    visualizations: list[str] = Field(description="Recommended visualizations: network_graph, heatmap, timeline, evidence")
    summary: str = Field(description="Brief natural language summary of what was understood")
