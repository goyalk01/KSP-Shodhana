"""
Pydantic schemas for the query understanding endpoint.
These define the contract between Spring Boot and FastAPI for intent parsing.
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
    type: Literal["person", "location", "crime_type", "date_range", "fir_number", "status", "weapon"]
    value: str
    confidence: float = Field(ge=0.0, le=1.0)


class QueryFilters(BaseModel):
    """Structured filters extracted from the natural language query."""
    crime_type: Optional[str] = Field(default=None, description="Filter by type of crime")
    district: Optional[str] = Field(default=None, description="Filter by district")
    station: Optional[str] = Field(default=None, description="Filter by station")
    status: Optional[str] = Field(default=None, description="Filter by status")
    severity: Optional[str] = Field(default=None, description="Filter by severity")
    date_from: Optional[str] = Field(default=None, description="Filter start date yyyy-MM-dd")
    date_to: Optional[str] = Field(default=None, description="Filter end date yyyy-MM-dd")
    person_name: Optional[str] = Field(default=None, description="Filter by suspect or criminal name")
    fir_number: Optional[str] = Field(default=None, description="Filter by FIR number")


class UnderstandResponse(BaseModel):
    """Output from the /ai/v1/understand endpoint."""
    intent: Literal[
        "search_crimes",
        "find_criminal",
        "show_network",
        "crime_hotspots",
        "timeline",
        "crime_stats",
        "general_question",
    ]
    entities: list[Entity] = Field(default_factory=list, description="Extracted entities")
    filters: QueryFilters = Field(default_factory=QueryFilters, description="Extracted query filters")
    visualizations: list[Literal["network_graph", "heatmap", "timeline", "evidence"]] = Field(
        default_factory=list,
        description="Recommended visualizations"
    )
    summary: str = Field(
        default="Parsed natural language query into structured investigation parameters.",
        description="Brief natural language summary of what was understood",
    )
