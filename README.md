# KSP Shodhana (ಶೋಧನೆ)

> **Ask. Analyze. Act.** — AI-Powered Crime Intelligence & Investigation Workspace for the Karnataka State Police.

[![Next.js](https://img.shields.io/badge/Next.js-16.2-FDFCF8?style=flat-square&logo=next.js&logoColor=2C2C24)](https://nextjs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-5D7052?style=flat-square&logo=springboot&logoColor=F3F4F1)](https://spring.io/projects/spring-boot)
[![FastAPI](https://img.shields.io/badge/FastAPI-0.115-C18C5D?style=flat-square&logo=fastapi&logoColor=FFFFFF)](https://fastapi.tiangolo.com/)
[![Gemini AI](https://img.shields.io/badge/Gemini_AI-3.5_Flash-E6DCCD?style=flat-square&logo=google-gemini&logoColor=4A4A40)](https://deepmind.google/technologies/gemini/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0-5D7052?style=flat-square&logo=typescript&logoColor=F3F4F1)](https://www.typescriptlang.org/)
[![Java 17-25](https://img.shields.io/badge/Java-17_--_25-C18C5D?style=flat-square&logo=openjdk&logoColor=FFFFFF)](https://openjdk.org/)
[![Python 3.10+](https://img.shields.io/badge/Python-3.10+-E6DCCD?style=flat-square&logo=python&logoColor=4A4A40)](https://www.python.org/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-v4-5D7052?style=flat-square&logo=tailwindcss&logoColor=FFFFFF)](https://tailwindcss.com/)

---

## 📌 Executive Overview

**KSP Shodhana (ಶೋಧನೆ)** is an AI-powered intelligence workspace engineered specifically for police officers and crime investigators across Karnataka State.

Instead of navigating legacy vaults, fragmented spreadsheet registries, and disconnected FIR logs, investigators query intelligence using natural language in **English or Kannada**. The workspace autonomously parses query intent, extracts structured entities, and renders interactive spatial heatmaps, suspect co-accused network graphs, investigation timelines, and citation-backed evidence panels in real time.

---

## 🌟 Key Features

* 💬 **Multimodal AI Copilot**: Understood via Google Gemini 3.5 Flash with structured intent parsing for complex queries (`understand` & `analyze`).
* 🗺️ **Geographic Crime Density Heatmap**: Interactive Leaflet maps highlighting district-level crime hotspots, frequency clusters, and station radii.
* 🕸️ **Co-Accused Suspect Network Graph**: Interactive 2D physics-directed graph mapping suspect links, gang structures, and FIR associations.
* 📅 **Chronological Investigation Timeline**: Visual step-by-step progress tracking for active investigations and FIR events.
* 📊 **Explainable Evidence Cards**: Every AI deduction is backed by official record keys, confidence scores, and source citations.
* 🎛️ **Interactive Resizable Split-Pane**: Fluid drag-slider allowing officers to dynamically resize the Chat Box and Visualization panels.
* ⚙️ **Dynamic Settings Management**: Custom district, duty station, language, refresh interval, and local fallback toggles.
* 📄 **Official Case Dossier Preview**: One-click generation of print-ready, official KSP-formatted investigation reports.
* 🛡️ **Zero-Downtime Local Fallback Engine**: Built-in regex and keyword heuristic fallback (`English & Kannada`) ensuring reliability if external APIs are unreachable.

---

## 🛠️ Technology Stack

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| **Frontend UI** | Next.js (App Router) | `16.2.10` | Interactive Workspace Web Application |
| **State Management** | Zustand | `5.0.14` | Global workspace state & panel layout control |
| **Backend Core** | Spring Boot | `3.3.0` | Orchestration API, Data Processing & Proxy Gateway |
| **AI Gateway** | FastAPI + Uvicorn | `0.115.0` | Gemini Structured Extraction & Intent Router |
| **AI Model** | Google Gemini | `3.5 Flash` | Intent understanding & analytical entity extraction |
| **Database Layer** | Zoho Catalyst Data Store | - | Production cloud-ready persistence layer |
| **Offline Data Engine** | `LocalDataStore` | JSON-backed | High-speed local seed data fallback store |
| **Maps & Graphs** | Leaflet & React Force Graph 2D | `1.9.4` / `1.29` | Spatial maps & 2D physics suspect graph rendering |
| **Design Tokens** | Tailwind CSS | `v4.0` | Modern, responsive organic palette & layout system |

---

## 🏗️ System Architecture & Data Flow

KSP Shodhana employs a **decoupled 3-tier microservice architecture** with a Backend-For-Frontend (BFF) pattern, dynamic proxy routing, structured AI intent parsing, and dual-mode resilient persistence.

### 🏛️ High-Level System Architecture

```mermaid
flowchart TB
    subgraph ClientLayer ["1. Presentation Layer (Port 3000)"]
        UI["Next.js 16 Workspace UI"]
        Store["Zustand State Store"]
        SplitPane["Interactive Resizable Split-Pane"]
        
        subgraph Panels ["Interactive Visualization Panels"]
            Heatmap["🗺️ Leaflet Heatmap"]
            NetworkGraph["🕸️ 2D Physics Force Graph"]
            Timeline["📅 Investigation Timeline"]
            Evidence["📊 Explainable Evidence"]
        end
    end

    subgraph BFFProxy ["BFF / Reverse Proxy Router"]
        NextProxy["Next.js /api/proxy/* Route Handlers"]
    end

    subgraph BackendCore ["2. Core Orchestrator Layer (Port 8080)"]
        SpringBoot["Spring Boot 3.3 Application"]
        AiController["AiController & SettingsController"]
        AiGateway["AiGatewayService"]
        CrimeService["Crime & Criminal Services"]
        LocalFallback["Heuristic Offline Engine (English + Kannada)"]
    end

    subgraph AIGateway ["3. AI Intelligence Layer (Port 8000)"]
        FastAPI["FastAPI Uvicorn Service"]
        UnderstandRouter["/ai/v1/understand"]
        AnalyzeRouter["/ai/v1/analyze"]
        PydanticSchemas["Pydantic Structured Output Models"]
    end

    subgraph ExternalAI ["4. External Foundation Models"]
        Gemini["Google Gemini 3.5 Flash API"]
    end

    subgraph StorageLayer ["5. Data Persistence Layer"]
        CatalystDS[("Zoho Catalyst Data Store")]
        LocalStore[("LocalDataStore (JSON Fallback Store)")]
    end

    %% Data Flow Connections
    UI -->|User Query / Interaction| Store
    Store -->|HTTP JSON Request| NextProxy
    NextProxy -->|Proxy Header & Route| SpringBoot
    
    SpringBoot --> AiController
    AiController --> AiGateway
    
    AiGateway -->|POST Intent Extraction| FastAPI
    FastAPI --> UnderstandRouter
    UnderstandRouter --> PydanticSchemas
    PydanticSchemas -->|Structured Prompt| Gemini
    Gemini -->|JSON Output| FastAPI
    FastAPI -->|Extracted Filters & Intent| AiGateway
    
    AiGateway -.->|On API Failure / Offline| LocalFallback
    
    AiGateway --> CrimeService
    CrimeService --> CatalystDS
    CrimeService -.->|Fallback Query| LocalStore
    
    SpringBoot -->|WorkspacePayload Response| NextProxy
    NextProxy -->|Update Visual State| Store
    Store --> Heatmap
    Store --> NetworkGraph
    Store --> Timeline
    Store --> Evidence
```

### 🔄 End-to-End Query Processing Lifecycle

```mermaid
sequenceDiagram
    autonumber
    actor Officer as 👮 Investigator
    participant UI as 💻 Next.js Workspace (:3000)
    participant Proxy as 🔄 BFF API Proxy
    participant BE as ⚙️ Spring Boot Core (:8080)
    participant AI as 🧠 FastAPI Service (:8000)
    participant GEM as ✦ Gemini 3.5 Flash
    participant DB as 🗄️ LocalDataStore / Catalyst

    Officer->>UI: Enter natural language query ("Show crime hotspots in Bengaluru")
    UI->>Proxy: POST /api/proxy/api/v1/ai/query
    Proxy->>BE: Forward request to POST /api/v1/ai/query
    
    BE->>AI: POST /ai/v1/understand { text, district, station }
    AI->>GEM: Generate Content (Structured Prompt + Strict JSON Schema)
    
    alt Gemini Connection Success
        GEM-->>AI: Return Structured JSON { intent: "CRIME_HOTSPOT", filters: { district: "Bengaluru Urban" } }
        AI-->>BE: 200 OK { success: true, data: { intent, filters, visualizations: ["heatmap", "evidence"] } }
    else Gemini Error / Key Missing
        AI-->>BE: 500 Error / Timeout
        BE->>BE: Trigger Local Fallback Engine (Regex Heuristics for English & Kannada)
    end

    BE->>DB: Query records using extracted entity filters (District, Severity, Status)
    DB-->>BE: Return matching Crime, Criminal, and Link entities

    BE->>AI: POST /ai/v1/analyze { records, query }
    AI->>GEM: Generate Evidence Citations & Confidence Indexes
    GEM-->>AI: Return Evidence Array with source FIR IDs and confidence scores
    AI-->>BE: 200 OK Evidence Payload

    BE->>BE: Assemble WorkspacePayload (Active Panels + Heatmap Points + Evidence Cards)
    BE-->>Proxy: Return WorkspacePayload JSON
    Proxy-->>UI: Forward Response Payload
    UI->>UI: Update Zustand Store -> Render Heatmap, Evidence, & Chat Response
    UI-->>Officer: Interactive Workspace displays spatial crime density & citations
```

### 🌊 Detailed Data Pipeline Phases

| Phase | Component | Responsibilities & Data Operations |
|---|---|---|
| **Phase 1: Ingestion & BFF Routing** | Next.js App Router | Receives natural language input from `ChatInput`, updates Zustand workspace state, and proxies requests via `/api/proxy/*` to remove CORS issues and keep backend credentials secure. |
| **Phase 2: Intent Parsing & Entity Extraction** | FastAPI + Gemini 3.5 Flash | Dispatches structured extraction prompts via Uvicorn. Validates output using Pydantic schemas (intent, action type, district/station parameters, severity, and requested visualization types). |
| **Phase 3: Resilient Fallback Router** | Spring Boot `AiGatewayService` | Monitors FastAPI response health. If Gemini API is unreachable or rate-limited, triggers a dual-language (English & Kannada) regex heuristic fallback parser, ensuring zero downtime. |
| **Phase 4: Database Query & Aggregation** | `CrimeService` & `LocalDataStore` | Executes filtered SQL/DataStore queries using the extracted entities. Retrieves FIR records, suspect co-accused links, and chronological investigation events. |
| **Phase 5: Analytical Evidence Generation** | FastAPI `/ai/v1/analyze` | Generates explainable evidence cards backed by official FIR citations, severity badges, and confidence percentages. |
| **Phase 6: Multi-Panel Client Rendering** | Leaflet + Force Graph + Zustand | Populates Leaflet maps (`heatmap`), 2D physics suspect force graphs (`network_graph`), vertical timelines (`timeline`), and resizable split-pane containers (`ChatPanel` vs `VisualizationGrid`). |

---

## 📂 Repository Structure

```
KSP-Shodhana/
├── frontend/                 # Next.js 16 Web Application
│   ├── src/
│   │   ├── app/              # App router pages, globals.css, proxy routes
│   │   ├── features/         # Chat, Heatmap, Network Graph, Timeline, Evidence
│   │   ├── lib/              # Utility functions & API clients
│   │   └── stores/           # Zustand workspace state management
│   └── package.json
├── backend/                  # Spring Boot Core Orchestrator
│   ├── src/main/java/        # REST Controllers, DTOs, Services, Data Stores
│   ├── src/main/resources/   # Application YAML & Baseline Seed Data JSONs
│   └── pom.xml
├── ai-service/               # FastAPI Python AI Service Gateway
│   ├── app/
│   │   ├── main.py           # FastAPI entry point & CORS configuration
│   │   ├── routers/          # /understand, /analyze, /settings routes
│   │   ├── schemas/          # Pydantic structured output models
│   │   └── services/         # Gemini API client & fallback handlers
│   ├── requirements.txt
│   └── .env.example
├── seed-data/                # Raw JSON datasets for baseline deployment
└── docs/                     # Architecture & system design documentation
```

---

## 🗄️ Database Schema & Data Models

The system models intelligence across 7 core entities:

| Model | Key Fields | Description |
|---|---|---|
| **Crime** | `rowId`, `firNumber`, `crimeType`, `severity`, `district`, `station`, `latitude`, `longitude` | Individual FIR incident records and spatial markers |
| **Criminal** | `rowId`, `name`, `alias`, `age`, `riskLevel`, `gangAffiliation`, `status` | Suspect dossiers and risk metrics |
| **CrimeCriminalLink** | `crimeId`, `criminalId`, `role` | Links suspect roles (*Accused*, *Suspect*, *Witness*) to FIRs |
| **CriminalNetwork** | `sourceCriminalId`, `targetCriminalId`, `relationshipType`, `strength` | Co-accused relationships and gang ties |
| **Investigation** | `investigationId`, `title`, `leadOfficer`, `status` | Active case dossier containers |
| **TimelineEvent** | `eventId`, `investigationId`, `timestamp`, `title`, `description` | Chronological event logs |
| **AuditLog** | `logId`, `timestamp`, `officerId`, `action` | Immutable security audit trails |

---

## 🚀 Local Installation & Quickstart

### Prerequisites
* **Node.js**: `v20.0+`
* **Java SDK**: `JDK 17` to `JDK 25`
* **Python**: `3.10+`
* **Maven**: `3.8+`

---

### Step 1: Launch FastAPI AI Service (Port 8000)
```bash
cd ai-service

# Create virtual environment
python -m venv .venv

# Activate virtual environment
# Windows:
.venv\Scripts\activate
# Linux/macOS:
source .venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Start Uvicorn server
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

---

### Step 2: Launch Spring Boot Backend (Port 8080)
```bash
cd backend

# Compile and start Spring Boot service
mvn spring-boot:run
```

---

### Step 3: Launch Next.js Workspace Frontend (Port 3000)
```bash
cd frontend

# Install Node dependencies
npm install

# Start Next.js development server
npm run dev
```

Open **`http://localhost:3000`** in your web browser to access the workspace.

---

## ⚙️ Environment Variables

### AI Gateway (`ai-service/.env`)
```env
GEMINI_API_KEY=your_gemini_api_key_here
GEMINI_MODEL=gemini-3.5-flash-lite
PORT=8000
CORS_ORIGINS=["http://localhost:3000","http://localhost:8080"]
```

---

## ⚡ Key API Endpoints

### AI Gateway Endpoints (`http://localhost:8000`)
* `POST /ai/v1/understand`: Parses natural language text into structured intent & filters.
* `POST /ai/v1/analyze`: Extracts explainable evidence citations and confidence metrics.
* `GET/POST /ai/v1/settings`: Fetches and updates in-memory model settings.

### Core Backend Endpoints (`http://localhost:8080`)
* `POST /api/v1/ai/query`: Primary endpoint processing investigator queries and compiling visualizations.
* `GET /api/v1/crimes`: Filterable listing of FIR records.
* `GET /api/v1/criminals`: Searchable listing of criminal dossiers.
* `GET /api/v1/network/{criminalId}`: Returns 2D force graph payload for suspect relationships.
* `GET /api/v1/timeline/{investigationId}`: Returns chronological case event timeline.
* `GET /api/v1/reports/{reportId}/preview`: Generates KSP-formatted HTML case dossier report.

---

## 🛡️ Offline Heuristic Fallback & Reliability

To ensure uninterrupted operation during network outages or API rate limit caps:
* When `GEMINI_API_KEY` is not supplied or Gemini API returns an error, Uvicorn and Spring Boot automatically failover to local offline heuristic processors (`AiGatewayService.java`).
* The local fallback uses regex keyword analysis in both **English and Kannada** (e.g., `hotspot`/`ನಕ್ಷೆ`, `network`/`ಜಾಲ`, `timeline`/`ತನಿಖೆ`).
* Structured responses are compiled instantly using local seed data (`seed-data/`), guaranteeing 100% demo reliability.

---

## 📜 License & Accreditation

Developed for **Karnataka State Police**. Created for KSP Hackathon 2026.
