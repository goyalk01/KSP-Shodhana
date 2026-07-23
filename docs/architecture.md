# ️ KSP-Shodhana  Enterprise System Architecture & Security Specification

**KSP-Shodhana** (Karnataka State Police Shodhana) is an enterprise-grade, high-performance, and vault-secured Crime Intelligence & Network Analysis Platform engineered for law enforcement officers and intelligence analysts across Karnataka.

---

## 1. End-to-End System Topology

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER (Next.js 14)                          │
│  - React 18, TailwindCSS, Lucide Icons, Leaflet Maps, D3 Network Force Graph     │
│  - Real-Time SSE Token Streaming Receiver & Multi-Language Web Speech Engine     │
│  - Dynamic Forensic Steganographic Watermark Overlay (Officer ID, Badge #, IP)   │
└────────────────────────────────────────┬─────────────────────────────────────────┘
                                         │  HTTPS / REST / SSE Stream
                                         ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                   SPRING BOOT CORE GATEWAY & SERVICES (Java 17)                  │
│  - Spring Security with JWT Token Authentication & Role-Based Access Control     │
│  - AiGatewayService (WebClient + 20s Timeout + Local Heuristic Fallbacks)        │
│  - GraphService (Neo4j Multi-Hop BFS Path Analytics Engine)                      │
│  - AuditLedgerService (WORM Cryptographic SHA-256 Hash-Chained Ledger)           │
│  - AnomalyDetector (Anti-Exfiltration Rate Limiter & Session Lock Engine)        │
└───────────────────┬────────────────────┬────────────────────┬────────────────────┘
                    │                    │                    │
          PostgreSQL / PostGIS         Neo4j Graph        FastAPI REST
                    │                    │                    │
                    ▼                    ▼                    ▼
┌───────────────────────┐  ┌───────────────────────┐  ┌──────────────────────────┐
│ PERSISTENCE LAYER     │  │ GRAPH TOPOLOGY        │  │ FASTAPI AI ENGINE (Py)   │
│ - PostgreSQL 15+      │  │ - Neo4j / Graph DB    │  │ - PII Anonymizer Masker  │
│ - PostGIS (Point 4326)│  │ - Multi-Hop Network   │  │ - RAG Vector Store       │
│ - Row-Level Security  │  │ - Suspect Co-Accused  │  │ - Gemini Client Wrapper  │
└───────────────────────┘  └───────────────────────┘  └────────────┬─────────────┘
                                                                   │
                                                                   ▼
                                                       ┌───────────────────────────┐
                                                       │ GOOGLE GEMINI CLOUD       │
                                                       │ (gemini-flash-lite-latest)│
                                                       └───────────────────────────┘
```

---

## ️ 2. Core Service Architectures

### A. FastAPI AI Gateway & Zero-Leakage Pipeline (`ai-service/`)
* **Pre-Inference PII Redaction** ([`pii_anonymizer.py`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/ai-service/app/services/pii_anonymizer.py)):
  * Redacts names, Aadhaar/SSN numbers, phone numbers, and vehicle license plates before prompts leave service boundaries.
  * Replaces PII with tokens (`<AADHAAR_1>`, `<PHONE_1>`, `<VEHICLE_1>`) and performs local on-premise de-tokenization when receiving response.
* **Semantic RAG Vector Store** ([`vector_store.py`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/ai-service/app/services/vector_store.py)):
  * Cosine similarity vector search engine serving semantic context from FIR documents and officer case notes via `/ai/v1/search/vector`.
* **Gemini Client Engine** ([`gemini_client.py`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/ai-service/app/services/gemini_client.py)):
  * Configured for structured Pydantic output using `gemini-flash-lite-latest` with fallback retries and model initialization handling.

---

### B. Spring Boot Gateway & Security Services (`backend/`)
* **Server-Sent Events (SSE) Token Streaming** ([`AiController.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/controller/AiController.java#L44-L65)):
  * Serves `/api/v1/ai/stream` endpoint delivering token-by-token response streams for live typing animations in Next.js chat.
* **Neo4j Multi-Hop Graph Traversal** ([`GraphService.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/service/GraphService.java)):
  * Executes Breadth-First Search (BFS) graph path analytics between suspect nodes up to $N$ hops to uncover hidden syndicate relationships.
* **Spring Security & JWT RBAC** ([`JwtTokenProvider.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/security/JwtTokenProvider.java) & [`SecurityConfig.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/config/SecurityConfig.java)):
  * Enforces role-based permissions (`ROLE_OFFICER`, `ROLE_INSPECTOR`, `ROLE_SUPERINTENDENT`).

---

### C. Enterprise Persistence & Security Layer
* **PostgreSQL + PostGIS RLS Migrations**:
  * [`V1__init_schema.sql`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/resources/db/migration/V1__init_schema.sql): Creates spatial PostGIS geometry indexing (`geom GEOMETRY(Point, 4326)`).
  * [`V2__row_level_security.sql`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/resources/db/migration/V2__row_level_security.sql): Enforces Row-Level Security (RLS) policies (`station_jurisdiction_policy` & `criminal_clearance_policy`).
* **Cryptographic WORM Audit Ledger** ([`AuditLedgerService.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/security/AuditLedgerService.java)):
  * Structure audit logs as a SHA-256 hash-chained ledger:
    $$Hash_N = \text{SHA-256}(Hash_{N-1} + \text{Timestamp} + \text{OfficerID} + \text{Action})$$
  * Includes automated chain integrity validation (`verifyLedgerIntegrity()`).
* **Bulk Export Anomaly Detector** ([`AnomalyDetector.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/security/AnomalyDetector.java)):
  * Anti-exfiltration rate limiter locking officer sessions if >20 criminal profile requests occur within 5 minutes.

---

### D. Presentation Layer & UX (`frontend/`)
* **Dynamic Forensics Watermarking** ([`WatermarkOverlay.tsx`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/frontend/src/components/security/WatermarkOverlay.tsx)):
  * Steganographic canvas overlay embedding Officer Badge #, Timestamp, and Client IP address across high-sensitivity investigation views.
* **Authentic Multi-Language Speech Engine** ([`translator.ts`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/frontend/src/lib/translator.ts)):
  * Translates investigation summaries into authentic Devanagari Hindi (`hi-IN`) and Kannada script (`kn-IN`) before feeding to Web Speech TTS.

---

## Summary of Security Domain Implementation

| Domain | Standard Baseline | **KSP-Shodhana Vault Grade** |
| --- | --- | --- |
| **LLM Privacy** | Raw Prompt API | **Pre-Inference PII Redaction & Local Detokenization** |
| **Database Access** | Simple Web Filters | **PostgreSQL Row-Level Security (RLS) + ABAC** |
| **Exfiltration Protection** | Static Download | **Dynamic Forensic Watermarking + Anomaly Session Lock** |
| **Audit Logging** | File Appender Log | **WORM SHA-256 Hash-Chained Cryptographic Ledger** |
| **Networking** | Standard REST | **mTLS Ready Microservice Mesh + SSE Token Streaming** |
