# KSP Shodhana (ಶೋಧನೆ)

> **Ask. Analyze. Act.** — AI-Powered Crime Intelligence & Investigation Workspace

## Overview

KSP Shodhana is an AI Copilot for investigators at the Karnataka State Police. Instead of manually searching fragmented records, investigators interact with crime intelligence using natural language. The system understands queries, analyzes relationships, visualizes criminal networks, displays crime hotspots, generates timelines, and provides explainable evidence-backed insights.

## Architecture

| Service | Technology | Port | Purpose |
|---------|-----------|------|---------|
| Frontend | Next.js 15 + TypeScript | 3000 | Investigation Workspace UI |
| Backend | Spring Boot 3 | 8080 | REST API + Catalyst Data Store |
| AI Service | FastAPI + Python | 8000 | Gemini-powered query understanding & analysis |

## Quick Start

### Prerequisites
- Node.js 20+
- Java 17 to 25 (Lombok 1.18.46+ is configured for JDK 25 compatibility)
- Python 3.10+
- Maven 3.8+ (for local backend execution)

### Frontend
1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the development server (runs on port 3000):
   ```bash
   npm run dev
   ```

### Backend
1. Navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Run the Spring Boot application (runs on port 8080):
   ```bash
   mvn spring-boot:run
   ```
   *Note: On startup, `LocalDataStore` automatically imports demo seed data from the classpath.*

### AI Service
1. Navigate to the AI service directory:
   ```bash
   cd ai-service
   ```
2. Create your environment file:
   ```bash
   cp .env.example .env
   ```
3. Initialize the Python virtual environment and install dependencies:
   ```bash
   python -m venv .venv
   # Windows:
   .venv\Scripts\pip install -r requirements.txt
   # Linux/macOS:
   .venv/bin/pip install -r requirements.txt
   ```
4. Start the FastAPI server (runs on port 8000):
   ```bash
   # Windows:
   .venv\Scripts\uvicorn app.main:app --port 8000
   # Linux/macOS:
   .venv/bin/uvicorn app.main:app --port 8000
   ```

## Project Structure

```
ksp-shodhana/
├── frontend/       # Next.js 15 App
├── backend/        # Spring Boot API
├── ai-service/     # FastAPI AI Service
├── docs/           # Architecture documentation
├── seed-data/      # Demo seed data
└── README.md
```

## Tech Stack

- **Frontend:** Next.js 15, TypeScript, Tailwind CSS, shadcn/ui, React Force Graph, React Leaflet, Recharts
- **Backend:** Spring Boot 3, Catalyst Java SDK
- **AI:** Google Gemini API, FastAPI, Pydantic
- **Database:** Zoho Catalyst Data Store
- **Deployment:** Zoho Catalyst AppSail

## Team

Karnataka State Police — Hackathon 2026

## License

Proprietary — Karnataka State Police
