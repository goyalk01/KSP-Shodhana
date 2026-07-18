"""
Query understanding router.
Endpoint: POST /ai/v1/understand

Takes a natural language query and returns structured intent, entities, and filters.
"""

from fastapi import APIRouter, HTTPException
from app.schemas.query import UnderstandRequest, UnderstandResponse
from app.services.query_engine import QueryEngine

router = APIRouter()
query_engine = QueryEngine()


@router.post("/understand", response_model=UnderstandResponse)
async def understand_query(request: UnderstandRequest) -> UnderstandResponse:
    """
    Parse a natural language query into structured intent and parameters.

    This endpoint is called by Spring Boot when an investigator submits a query.
    It uses Gemini to extract:
    - Intent (what the user wants to do)
    - Entities (people, locations, crime types, dates)
    - Filters (structured query parameters)
    - Visualizations (which UI panels to activate)
    """
    try:
        result = await query_engine.understand(
            text=request.text,
            conversation_history=request.conversation_history,
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Query understanding failed: {str(e)}")
