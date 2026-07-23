# KSP Shodhana (ಶೋಧನೆ)

> **Ask. Analyze. Act.** AI-Powered Crime Intelligence & Investigation Workspace for the Karnataka State Police.

[![Next.js](https://img.shields.io/badge/Next.js-14.2-FDFCF8?style=flat-square&logo=next.js&logoColor=2C2C24)](https://nextjs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-5D7052?style=flat-square&logo=springboot&logoColor=F3F4F1)](https://spring.io/projects/spring-boot)
[![FastAPI](https://img.shields.io/badge/FastAPI-0.115-C18C5D?style=flat-square&logo=fastapi&logoColor=FFFFFF)](https://fastapi.tiangolo.com/)
[![Gemini AI](https://img.shields.io/badge/Gemini_AI-Flash_Lite-E6DCCD?style=flat-square&logo=google-gemini&logoColor=4A4A40)](https://deepmind.google/technologies/gemini/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0-5D7052?style=flat-square&logo=typescript&logoColor=F3F4F1)](https://www.typescriptlang.org/)
[![Java 17-25](https://img.shields.io/badge/Java-17_--_25-C18C5D?style=flat-square&logo=openjdk&logoColor=FFFFFF)](https://openjdk.org/)
[![Python 3.10+](https://img.shields.io/badge/Python-3.10+-E6DCCD?style=flat-square&logo=python&logoColor=4A4A40)](https://www.python.org/)
[![PostgreSQL PostGIS](https://img.shields.io/badge/PostgreSQL-PostGIS-336791?style=flat-square&logo=postgresql&logoColor=FFFFFF)](https://postgis.net/)

---

## Executive Overview

**KSP Shodhana (ಶೋಧನೆ)** is an enterprise-grade AI-powered intelligence workspace engineered specifically for police officers and crime investigators across Karnataka State.

Instead of navigating legacy vaults, fragmented spreadsheet registries, and disconnected FIR logs, investigators query intelligence using natural language in **English or Kannada**. The workspace autonomously parses query intent, extracts structured entities, and renders interactive spatial heatmaps, suspect co-accused network graphs, investigation timelines, and citation-backed evidence panels in real time.

---

## Key Features & Enterprise Capabilities

* **Multimodal AI Copilot**: Powered by Google Gemini `gemini-flash-lite-latest` with structured intent parsing for complex queries (`understand` & `analyze`).
* **Real-Time Token Streaming (SSE)**: Server-Sent Events endpoint (`/api/v1/ai/stream`) enabling live token-by-token typing response streams in the chat interface.
* **Geographic Crime Density Heatmap**: Dynamic Leaflet maps with automatic location centering, district filtering, and incident density overlays.
* **Co-Accused Suspect Network Graph**: Multi-hop physics-directed graph mapping suspect links, gang structures, and FIR associations (`GraphService.java`).
* **Semantic Vector Search (RAG)**: Cosine similarity vector store (`vector_store.py`) enabling semantic search across unstructured FIR documents and case notes.
* **Pre-Inference PII Anonymization**: Automatic redaction of sensitive identities, Aadhaar/SSN numbers, phone numbers, and vehicle license plates (`pii_anonymizer.py`).
* **PostgreSQL + PostGIS Spatial Schema**: Spatial PostGIS geometry indexing (`geom GEOMETRY(Point, 4326)`) and Row-Level Security (`V1` & `V2` migrations).
* **Cryptographic Immutable WORM Audit Ledger**: SHA-256 hash-chained ledger (`Hash_N = SHA-256(Hash_{N-1} + Timestamp + OfficerID + Action)`).
* **Bulk Export Anomaly Detection**: Anti-exfiltration rate limiter locking officer accounts if >20 criminal profile requests occur within 5 minutes (`AnomalyDetector.java`).
* **Dynamic Forensics Watermarking**: Steganographic overlay embedding Officer Badge #, Timestamp, and Client IP address across high-sensitivity screens.
* **Spring Security JWT & RBAC**: Role-based access control enforcing `ROLE_OFFICER`, `ROLE_INSPECTOR`, and `ROLE_SUPERINTENDENT`.
* **Authentic Hindi & Kannada Voice Engine**: True script translation (`translator.ts`) in Web Speech TTS.

---

## Technology Stack

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| **Frontend UI** | Next.js (App Router) | `14.2` | Interactive Workspace Web Application |
| **State Management** | Zustand | `5.0` | Global workspace state & panel layout control |
| **Backend Core** | Spring Boot | `3.3.0` | Orchestration API, Security & Proxy Gateway |
| **AI Gateway** | FastAPI + Uvicorn | `0.115.0` | Gemini Structured Extraction & PII Redaction Router |
| **AI Model** | Google Gemini | `gemini-flash-lite-latest` | Intent understanding & analytical entity extraction |
| **Database Layer** | PostgreSQL + PostGIS / Catalyst | `15+` | Spatial persistence & Row-Level Security layer |
| **Graph Engine** | Neo4j / GraphService | - | Multi-hop suspect network traversal engine |
| **Vector Engine** | RAG VectorStore | Python | Cosine similarity semantic search engine |
| **Maps & Graphs** | Leaflet & React Force Graph 2D | `1.9` / `1.29` | Spatial maps & 2D physics suspect graph rendering |

---

## Architecture & Data Flow

```
Browser (Next.js 14) -> Spring Boot Gateway (Port 8080) -> FastAPI AI Gateway (Port 8000) -> Google Gemini API
                               |                                    |
                    PostgreSQL + PostGIS & Neo4j             RAG VectorStore & PII Masker
```

---

## Microservices Quickstart

### 1. FastAPI AI Service (Port 8000)
```bash
cd ai-service
python -m venv .venv
# Windows: .venv\Scripts\activate | Linux/macOS: source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

### 2. Spring Boot Core Backend (Port 8080)
```bash
cd backend
mvn spring-boot:run
```

### 3. Next.js Frontend Workspace (Port 3000)
```bash
cd frontend
npm install
npm run dev
```

Open **`http://localhost:3000`** in your browser.

---

## Environment Variables (`ai-service/.env`)
```env
GEMINI_API_KEY=your_gemini_api_key_here
GEMINI_MODEL=gemini-flash-lite-latest
PORT=8000
HOST=0.0.0.0
CORS_ORIGINS=["http://localhost:3000","http://localhost:8080"]
BACKEND_URL=http://localhost:8080
```

---

## License & Accreditation

Developed for **Karnataka State Police**. Created for KSP Hackathon 2026.
