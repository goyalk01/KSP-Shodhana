# 🛠️ KSP-Shodhana — System Architecture & Implementation Specification

**KSP-Shodhana** (Karnataka State Police Shodhana) is an AI-assisted Crime Intelligence & Network Analysis Workspace engineered for law enforcement officers and intelligence analysts across Karnataka.

---

## 1. System Topology & Dual Deployment Modes

The system is designed with a **hybrid architecture** that supports both a zero-setup local evaluation mode and a full containerized production deployment.

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER (Next.js 14)                          │
│  - React 18, TailwindCSS, Lucide Icons, Leaflet Maps, D3 Network Force Graph     │
│  - Real-Time SSE Token Streaming Receiver & Multi-Language Web Speech Engine     │
│  - Dynamic Forensic Steganographic Watermark Overlay (Officer ID, Badge #, IP)   │
└────────────────────────────────────────┬─────────────────────────────────────────┘
                                         │  REST / SSE (Port 3000 -> 8080)
                                         ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                   SPRING BOOT CORE GATEWAY & SERVICES (Java 17)                  │
│  - Spring Security with JWT Token Authentication & Role-Based Access Control     │
│  - Spring Data JPA Repositories (CrimeJpaRepository, CriminalJpaRepository)      │
│  - AiGatewayService (WebClient + 20s Timeout + Local Heuristic Fallbacks)        │
│  - GraphService (Neo4j Multi-Hop Cypher Engine + In-Memory BFS Fallback)         │
│  - AuditLedgerService (WORM Cryptographic SHA-256 Hash-Chained Ledger)           │
│  - AnomalyDetector (Anti-Exfiltration Rate Limiter & Session Lock Engine)        │
└───────────────────┬────────────────────┬────────────────────┬────────────────────┘
                    │                    │                    │
          PostgreSQL / PostGIS         Neo4j Graph        FastAPI REST
                    │                    │                    │
                    ▼                    ▼                    ▼
┌───────────────────────┐  ┌───────────────────────┐  ┌──────────────────────────┐
│ PERSISTENCE LAYER     │  │ GRAPH DB CONTAINER    │  │ FASTAPI AI ENGINE (Py)   │
│ - PostgreSQL 15+      │  │ - Neo4j / Graph DB    │  │ - PII Anonymizer Masker  │
│ - PostGIS (Point 4326)│  │ - Multi-Hop Network   │  │ - RAG Vector Store       │
│ - RLS Policies V1/V2  │  │ - Real Cypher Queries │  │ - Gemini Client Wrapper  │
└───────────────────────┘  └───────────────────────┘  └────────────┬─────────────┘
                                                                   │
                                                                   ▼
                                                       ┌───────────────────────────┐
                                                       │ GOOGLE GEMINI CLOUD API   │
                                                       └───────────────────────────┘
```

---

## 2. Technical Implementation Matrix

| Component | Default Zero-Setup Mode (Out of Box) | Docker Container Mode (`docker-compose.yml`) |
|---|---|---|
| **Persistence Engine** | Spring Data JPA with H2 In-Memory DB (`JpaDataInitializer` seeds 16 entities) | PostgreSQL 15 + PostGIS (`V1__init_schema.sql` & `V2__row_level_security.sql`) |
| **Spatial Mapping** | LocationTech JTS `Point` (`geometry(Point, 4326)`) with Bounding-Box + Haversine Radius | Native PostGIS Spatial Queries (`ST_DWithin` on geometry column) |
| **Graph Traversal** | `GraphService` In-Memory Multi-Hop Breadth-First Search (BFS) Traversal | `GraphService` Neo4j `Driver` bean running real Cypher queries |
| **Security & Auth** | `SecurityFilterChain` + `JwtAuthenticationFilter` (`.permitAll()` on demo routes) | `SecurityFilterChain` + JWT Role Enforcement (`ROLE_SUPERINTENDENT`) |
| **AI Processing** | FastAPI + Gemini `gemini-flash-lite-latest` + Local Offline Heuristics | FastAPI + Gemini API + TF-IDF/Cosine RAG Vector Store (`vector_store.py`) |

---

## 3. Core Component Breakdown

### A. Spring Boot Core Orchestrator (`backend/`)
* **Spring Data JPA & Spatial Entities**:
  * [`Crime.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/model/Crime.java): Annotated with `@Entity`, `@Table(name = "crimes")`, `@Id`, `@GeneratedValue`, and LocationTech `Point location` (`geometry(Point, 4326)`).
  * [`Criminal.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/model/Criminal.java): Annotated with `@Entity`, `@Table(name = "criminals")`, `@Id`, `@GeneratedValue`.
  * [`CrimeJpaRepository.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/repository/CrimeJpaRepository.java) & [`CriminalJpaRepository.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/repository/CriminalJpaRepository.java): Extend `JpaRepository<T, Long>` for Spring Data queries.
  * [`JpaDataInitializer.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/config/JpaDataInitializer.java): Automatically seeds JPA tables on startup.
* **Hybrid Graph Engine** ([`GraphService.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/service/GraphService.java)):
  * Injects an optional Neo4j `Driver` bean (`@Autowired(required = false)`).
  * Executes real Cypher multi-hop queries when Neo4j is connected, and gracefully falls back to in-memory BFS traversal when offline.
* **Cryptographic WORM Audit Ledger** ([`AuditLedgerService.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/security/AuditLedgerService.java)):
  * Implements SHA-256 hash chaining (`Hash_N = SHA-256(Hash_{N-1} + Timestamp + OfficerID + Action)`).
  * Exposes read-only verification endpoint `/api/v1/audit/ledger`.
* **Spring Security & RBAC** ([`SecurityConfig.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/config/SecurityConfig.java)):
  * Configures `JwtAuthenticationFilter` and `SecurityFilterChain`. Main demo routes use `.permitAll()` for evaluation convenience.

---

### B. FastAPI AI Service (`ai-service/`)
* **Pre-Inference PII Redaction** ([`pii_anonymizer.py`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/ai-service/app/services/pii_anonymizer.py)):
  * Redacts sensitive identities, Aadhaar numbers, phone numbers, and license plates before prompts leave service boundaries.
* **Semantic RAG Vector Store** ([`vector_store.py`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/ai-service/app/services/vector_store.py)):
  * Cosine similarity vector search over case notes and FIR documents (`/ai/v1/search/vector`).
* **Gemini Integration** ([`gemini_client.py`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/ai-service/app/services/gemini_client.py)):
  * Communicates with Google Gemini API using structured intent and entity extraction prompts.

---

### C. Presentation Layer (`frontend/`)
* **Next.js 14 App Router & Zustand**:
  * Dynamic multi-panel workspace with Zustand global state.
* **Leaflet Spatial Heatmap & React Force Graph**:
  * Real-time rendering of spatial crime clusters and 2D physics suspect networks.
* **Forensic Watermarking & TTS Speech Engine**:
  * Forensic canvas watermarking (`WatermarkOverlay.tsx`) and authentic Devanagari Hindi / Kannada voice translation (`translator.ts`).

---

## 4. Verification & Testing

The codebase includes automated unit test suites for verification:
- **Backend Tests**: `mvn test` executes `SecurityConfigTest`, `GraphServiceTest`, `AuditLedgerServiceTest`, and `JpaRepositoryTest`.
- **FastAPI Tests**: `pytest` executes `test_pii_anonymizer.py`.
- **CI Pipeline**: GitHub Actions (`.github/workflows/ci.yml`) runs automated builds and tests on every push to `dev`.
