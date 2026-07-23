"""
Query understanding router.
Endpoint: POST /ai/v1/understand

Takes a natural language query and returns structured intent, entities, and filters.
"""

from fastapi import APIRouter, HTTPException
from app.schemas.query import UnderstandRequest, UnderstandResponse
from app.services.query_engine import QueryEngine

from app.services.guardrails import guardrail_inspector

router = APIRouter()
query_engine = QueryEngine()


@router.post("/understand", response_model=UnderstandResponse)
async def understand_query(request: UnderstandRequest) -> UnderstandResponse:
    """
    Parse a natural language query into structured intent and parameters.
    """
    try:
        clean_text = guardrail_inspector.sanitize_and_validate(request.text)
        result = await query_engine.understand(
            text=clean_text,
            conversation_history=request.conversation_history,
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Query understanding failed: {str(e)}")
