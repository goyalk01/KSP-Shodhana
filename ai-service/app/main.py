"""
KSP Shodhana  AI Service
FastAPI entry point for the Gemini-powered query understanding and analysis engine.
"""

from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.config import settings
from app.routers import understand, analyze, settings as settings_router


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Application startup and shutdown events."""
    # Startup
    print(f"KSP Shodhana AI Service starting on port {settings.port}")
    print(f"Gemini model: {settings.gemini_model}")
    yield
    # Shutdown
    print("AI Service shutting down")


app = FastAPI(
    title="KSP Shodhana AI Service",
    description="Gemini-powered query understanding and crime analysis engine",
    version="0.1.0",
    lifespan=lifespan,
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Register routers
app.include_router(understand.router, prefix="/ai/v1", tags=["Query Understanding"])
app.include_router(analyze.router, prefix="/ai/v1", tags=["Analysis"])
app.include_router(settings_router.router, prefix="/ai/v1", tags=["Settings"])


@app.get("/", tags=["Health"])
async def root():
    """Root endpoint for service discovery."""
    return {"status": "ok", "service": "KSP Shodhana AI Service", "model": settings.gemini_model}


@app.get("/health", tags=["Health"])
async def health():
    """Health check endpoint for Docker & load balancers."""
    return {"status": "ok", "service": "ksp-shodhana-ai", "model": settings.gemini_model}


@app.get("/ai/v1/health", tags=["Health"])
async def health_check():
    """Health check endpoint."""
    return {"status": "ok", "service": "ksp-shodhana-ai", "model": settings.gemini_model}


@app.post("/ai/v1/search/vector", tags=["Vector Search"])
async def search_vector(payload: dict):
    """RAG Semantic Vector Search endpoint across crime documents."""
    from app.services.vector_store import vector_store
    query = payload.get("query", "")
    top_k = payload.get("top_k", 3)
    results = vector_store.search_semantic(query, top_k)
    return {"query": query, "results": results}

