"""
Application configuration loaded from environment variables.
Uses pydantic-settings for type-safe configuration.
"""

from typing import List
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    """Application settings loaded from environment or .env file."""

    # Gemini API
    gemini_api_key: str = ""
    gemini_model: str = "gemini-flash-lite-latest"
    gemini_max_tokens: int = 1024

    # Server
    host: str = "0.0.0.0"
    port: int = 8000
    log_level: str = "info"

    # CORS
    cors_origins: List[str] = ["http://localhost:3000", "http://localhost:8080"]

    # Backend
    backend_url: str = "http://localhost:8080"

    model_config = {
        "env_file": ".env",
        "env_file_encoding": "utf-8",
        "case_sensitive": False,
    }


settings = Settings()
