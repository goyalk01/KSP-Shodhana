# KSP Shodhana — Spring Boot Core Backend Gateway

The Spring Boot backend service is the central core orchestrator of the **KSP-Shodhana Intelligence Platform**. It connects Next.js frontend, FastAPI AI Service, PostgreSQL + PostGIS, and Neo4j Graph Database.

---

## Technical Specifications

* **Framework**: Spring Boot 3.3.0 (Java 17 / 25)
* **Port**: `8080`
* **Security**: Spring Security with JWT Tokens & Role-Based Access Control (`ROLE_OFFICER`, `ROLE_INSPECTOR`, `ROLE_SUPERINTENDENT`)
* **Real-time Streaming**: Server-Sent Events (SSE) via `SseEmitter` (`/api/v1/ai/stream`)
* **Persistence**: PostgreSQL 15+ with PostGIS spatial indexing (`geom GEOMETRY(Point, 4326)`) and `LocalDataStore` fallback
* **Graph Engine**: `GraphService.java` providing Neo4j Breadth-First Search (BFS) multi-hop criminal network path analytics
* **WORM Audit Ledger**: `AuditLedgerService.java` SHA-256 hash-chained immutable logging
* **Anti-Exfiltration**: `AnomalyDetector.java` rate-limiting and account locking

---

## Endpoint Specification

### AI Intelligence Endpoints
* `POST /api/v1/ai/query`: Primary investigation query processor returning full `WorkspacePayload`.
* `GET /api/v1/ai/stream?query=...`: Real-time SSE token-by-token streaming endpoint.

### Record Management Endpoints
* `GET /api/v1/crimes`: Filterable list of FIR crime records.
* `GET /api/v1/criminals`: Searchable criminal dossier database.
* `GET /api/v1/network/{criminalId}`: Returns 2D physics force graph network data for criminal profile.
* `GET /api/v1/timeline/{investigationId}`: Returns chronological investigation event timeline.
* `GET /api/v1/reports/{reportId}/preview`: Generates printable HTML investigation case dossier.

---

## Quickstart & Launch

```bash
cd backend
mvn spring-boot:run
```
Service starts on `http://localhost:8080`.
