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

## Technical Overview

**KSP Shodhana (ಶೋಧನೆ)** is a full-stack AI-assisted crime intelligence workspace built for police officers and crime investigators across Karnataka State.

Investigators can query crime records and suspect networks using natural language in **English or Kannada**. The system parses query intent, extracts structured entities, and renders interactive spatial heatmaps, suspect co-accused network graphs, investigation timelines, and citation-backed evidence panels.

---

## Dual Deployment Modes (Zero-Setup vs. Full Container Stack)

To ensure ease of technical evaluation and robust production readiness, the system implements a **dual-engine design**:

| Component | Default Zero-Setup Mode (Out of the Box) | Full Docker Stack Mode (`docker-compose.yml`) |
|---|---|---|
| **Data Layer** | Spring Data JPA + H2 In-Memory DB (Auto-seeded by `JpaDataInitializer`) | PostgreSQL 15 + PostGIS (Flyway migrations `V1` & `V2`) |
| **Spatial Queries** | LocationTech JTS Point Bounding-Box + Haversine Radius Filtering | Native PostGIS Spatial Queries (`ST_DWithin` on geometry column) |
| **Graph Engine** | In-Memory Multi-Hop Breadth-First Search (BFS) Traversal | Real Neo4j Cypher Graph Queries (`GraphService` optional `Driver` bean) |
| **Security & Auth** | Spring Security JWT Filter (`JwtAuthenticationFilter`) with `.permitAll()` on demo routes | Spring Security JWT + DB-backed Role Enforcement (`ROLE_SUPERINTENDENT`) |
| **AI Gateway** | FastAPI + Gemini `gemini-flash-lite-latest` + Heuristic Offline Fallback | FastAPI + Gemini API + Vector RAG Store (`vector_store.py`) |

---

## Key Technical Features

* **Multimodal AI Gateway**: FastAPI service integrating Google Gemini (`gemini-flash-lite-latest`) for structured intent understanding (`/ai/v1/understand`) and pattern analysis (`/ai/v1/analyze`).
* **Real-Time Token Streaming (SSE)**: Server-Sent Events endpoint (`/api/v1/ai/stream`) delivering live typing streams to the Next.js chat interface.
* **Spring Data JPA & Spatial Entities**: `Crime` and `Criminal` models annotated with `@Entity`, `@Table`, `@Id`, and LocationTech `Point location` (`geometry(Point, 4326)`).
* **Graceful Neo4j Fallback**: `GraphService.java` dynamically checks if Neo4j is reachable; executes Cypher queries if available, and gracefully defaults to in-memory BFS traversal if not.
* **Cryptographic WORM Audit Ledger**: SHA-256 hash-chained immutable logging (`AuditLedgerService.java`) with active chain verification (`verifyLedgerIntegrity()`).
* **Pre-Inference PII Masking**: Automatic regex masking of sensitive identities, Aadhaar numbers, phone numbers, and license plates (`pii_anonymizer.py`).
* **Semantic Vector Search (RAG)**: TF-IDF & Cosine similarity vector search (`vector_store.py`) over unstructured case notes and FIR documents.
* **Bulk Export Anomaly Detection**: Rate-limiting guard (`AnomalyDetector.java`) that locks accounts if >20 criminal profile requests occur within 5 minutes.
* **Dynamic Forensic Watermarking**: Steganographic overlay (`WatermarkOverlay.tsx`) embedding Officer Badge #, Timestamp, and IP address across sensitive UI panels.

---

## Technology Stack Architecture

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER (Next.js 14)                          │
│  - App Router, TailwindCSS, Zustand, Leaflet Maps, React Force Graph 2D          │
│  - Real-Time SSE Stream Receiver & Devanagari/Kannada Web Speech Engine          │
└────────────────────────────────────────┬─────────────────────────────────────────┘
                                         │  REST / SSE (Port 3000 -> 8080)
                                         ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                     SPRING BOOT CORE BACKEND ENGINE (Port 8080)                  │
│  - Spring Security with JwtAuthenticationFilter & SecurityFilterChain            │
│  - Spring Data JPA + H2 In-Memory / PostgreSQL PostGIS DataSource                │
│  - GraphService (Neo4j Driver with In-Memory BFS Fallback)                       │
│  - AuditLedgerService (WORM Cryptographic SHA-256 Hash Chain)                    │
└───────────────────┬────────────────────┬────────────────────┬────────────────────┘
                    │                    │                    │
          PostgreSQL / PostGIS         Neo4j Graph        FastAPI REST
                    │                    │                    │
                    ▼                    ▼                    ▼
┌───────────────────────┐  ┌───────────────────────┐  ┌──────────────────────────┐
│ POSTGIS CONTAINER     │  │ NEO4J GRAPH DB        │  │ FASTAPI AI SERVICE (8000)│
│ - PostGIS (Point 4326)│  │ - Cypher Multi-Hop    │  │ - PII Anonymizer Masker  │
│ - RLS Migrations V1/V2│  │ - Suspect Graph       │  │ - RAG Vector Store       │
└───────────────────────┘  └───────────────────────┘  └────────────┬─────────────┘
                                                                   │
                                                                   ▼
                                                       ┌───────────────────────────┐
                                                       │ GOOGLE GEMINI CLOUD API   │
                                                       └───────────────────────────┘
```

---

## Quickstart Guide

### Option A: Zero-Setup Local Execution (Default / Demo)

Requires only Java 17+, Python 3.10+, and Node.js 18+.

```bash
# 1. FastAPI AI Service (Terminal 1)
cd ai-service
python -m venv .venv
# Windows: .venv\Scripts\activate | Linux/macOS: source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000

# 2. Spring Boot Core Backend (Terminal 2)
cd backend
mvn spring-boot:run

# 3. Next.js Presentation UI (Terminal 3)
cd frontend
npm install
npm run dev
```
Open **`http://localhost:3000`** in your browser.

---

### Option B: Full Containerized Stack (`docker-compose.yml`)

Runs PostgreSQL/PostGIS, Neo4j, Spring Boot, FastAPI, and Next.js as Docker containers.

```bash
docker-compose up --build
```

---

## Repository Structure

```
├── frontend/             # Next.js 14 App Router, Leaflet, React Force Graph UI
├── backend/              # Spring Boot 3.3, Spring Data JPA, Security & Graph Services
├── ai-service/           # FastAPI, Gemini Client, PII Anonymizer, Vector RAG
├── docs/                 # System Architecture & Technical Specifications
└── docker-compose.yml    # Multi-container orchestration (PostGIS, Neo4j, App Services)
```

---

## License & Accreditation

Developed for **Karnataka State Police**. Built for KSP Hackathon 2026.
