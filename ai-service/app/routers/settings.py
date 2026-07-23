"""
Settings router.
Endpoint: GET /ai/v1/settings and POST /ai/v1/settings
"""

from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from app.config import settings

router = APIRouter()


class SettingsPayload(BaseModel):
    gemini_model: str
    gemini_api_key: str
    default_district: str = "Bengaluru Urban"
    local_fallback_active: bool = False


@router.get("/settings", response_model=SettingsPayload)
async def get_settings() -> SettingsPayload:
    """Get the current AI service configuration settings."""
    return SettingsPayload(
        gemini_model=settings.gemini_model,
        gemini_api_key=settings.gemini_api_key,
        default_district="Bengaluru Urban",
        local_fallback_active=False
    )


@router.post("/settings")
async def update_settings(payload: SettingsPayload):
    """Update AI service configuration in memory."""
    try:
        settings.gemini_model = payload.gemini_model
        settings.gemini_api_key = payload.gemini_api_key
        return {"status": "success", "message": "Settings updated in memory successfully"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to update settings: {str(e)}")
