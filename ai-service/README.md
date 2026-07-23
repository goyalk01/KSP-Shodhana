# KSP Shodhana — FastAPI AI Gateway Service

The Python FastAPI AI Gateway service handles structured natural language intent extraction, semantic RAG vector search, and pre-inference PII data anonymization for **KSP-Shodhana**.

---

## Technical Specifications

* **Framework**: FastAPI + Uvicorn (`0.115.0`)
* **Python Version**: `3.10+`
* **Port**: `8000`
* **AI Model**: Google Gemini (`gemini-flash-lite-latest`)
* **PII Redaction**: `pii_anonymizer.py` pre-inference masking of Aadhaar, phone numbers, and license plates
* **Vector Store**: `vector_store.py` RAG cosine similarity vector search across FIR reports
* **Schema Validation**: Pydantic v2 structured output contracts

---

## Endpoint Specification

* `POST /ai/v1/understand`: Parses natural language text into structured intent, extracted entities, and visualization recommendations.
* `POST /ai/v1/analyze`: Generates analytical insights and explainable evidence cards backed by FIR citations.
* `POST /ai/v1/search/vector`: RAG semantic vector search endpoint across crime documents.
* `GET/POST /ai/v1/settings`: Fetches and updates in-memory model settings.
* `GET /ai/v1/health`: Service health check.

---

## Quickstart & Launch

```bash
cd ai-service

# Create virtual environment
python -m venv .venv
# Windows: .venv\Scripts\activate | Linux/macOS: source .venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Start Uvicorn server
uvicorn app.main:app --host 0.0.0.0 --port 8000
```
Service starts on `http://localhost:8000`.
