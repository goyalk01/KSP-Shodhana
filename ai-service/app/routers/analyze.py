"""
Analysis router.
Endpoint: POST /ai/v1/analyze

Takes fetched data + original query and returns insights with evidence.
"""

from fastapi import APIRouter, HTTPException
from app.schemas.analysis import AnalyzeRequest, AnalyzeResponse
from app.services.analysis_engine import AnalysisEngine

router = APIRouter()
analysis_engine = AnalysisEngine()


@router.post("/analyze", response_model=AnalyzeResponse)
async def analyze_data(request: AnalyzeRequest) -> AnalyzeResponse:
    """
    Analyze crime/criminal data and produce evidence-backed insights.

    This endpoint is called by Spring Boot after data has been fetched
    from Catalyst Data Store. Gemini analyzes the data and produces:
    - Summary (natural language)
    - Insights (analytical observations)
    - Evidence (factual claims with sources and confidence scores)
    - Suggested follow-up questions
    """
    try:
        result = await analysis_engine.analyze(
            data=request.data,
            original_query=request.original_query,
            context=request.context,
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Analysis failed: {str(e)}")
