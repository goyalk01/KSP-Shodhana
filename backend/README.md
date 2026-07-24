# KSP Shodhana — Spring Boot Core Backend Gateway

The Spring Boot backend service is the central core orchestrator of the **KSP-Shodhana Intelligence Platform**. It connects Next.js frontend, FastAPI AI Service, Spring Data JPA (H2 / PostgreSQL PostGIS), and Neo4j Graph Database.

---

## Technical Architecture & Design

### Dual Persistence & Data Layer
* **Default Mode (Zero-Setup)**: Runs Spring Data JPA with H2 In-Memory DB (`jdbc:h2:mem:ksp_shodhana`). On application boot, [`JpaDataInitializer.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/config/JpaDataInitializer.java) automatically populates `Crime` and `Criminal` `@Entity` tables.
* **Production Docker Mode**: Connects to PostgreSQL 15 + PostGIS (`ksp-postgis`) with Flyway migrations (`V1__init_schema.sql` creating PostGIS geometry `location geometry(Point, 4326)` and `V2__row_level_security.sql` creating Row-Level Security policies).

### Graph Analytics Engine
* **Optional Neo4j Integration**: [`GraphService.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/service/GraphService.java) injects an optional `Driver` bean (`@Autowired(required = false)`).
* **Graceful Fallback**: If Neo4j is connected, it executes real multi-hop Cypher queries (`MATCH p=(c:Criminal {id: $id})-[r:ASSOCIATED_WITH*1..2]-(target)`). If Neo4j is not connected, it gracefully falls back to an in-memory Breadth-First Search (BFS) graph traversal.

### Spatial Distance Mapping
* **LocationTech JTS Spatial Mapping**: [`Crime.java`](file:///e:/VS%20Code/Combination/Hackathon/KSP%20Shodhana/backend/src/main/java/com/ksp/shodhana/model/Crime.java) maps `org.locationtech.jts.geom.Point location` with `@PrePersist` spatial synchronization.
* **Radius Search**: Executes bounding-box spatial queries (`findWithinBoundingBox`) and Haversine distance calculations in default mode, and native PostGIS `ST_DWithin` queries when deployed against PostgreSQL/PostGIS.

---

## Service Specifications

* **Framework**: Spring Boot 3.3.0 (Java 17 - 25)
* **Port**: `8080`
* **Security**: Spring Security with `JwtAuthenticationFilter` & `SecurityFilterChain`. Main API routes use `.permitAll()` for demo evaluation accessibility.
* **Real-time Streaming**: Server-Sent Events (SSE) via `SseEmitter` (`/api/v1/ai/stream`).
* **WORM Audit Ledger**: `AuditLedgerService.java` SHA-256 hash-chained immutable logging.
* **Anti-Exfiltration**: `AnomalyDetector.java` rate-limiting and account locking.

---

## Endpoint Overview

### AI Intelligence Endpoints
* `POST /api/v1/ai/query`: Primary investigation query processor returning full `WorkspacePayload`.
* `GET /api/v1/ai/stream?query=...`: Real-time SSE token-by-token streaming endpoint.

### Record Management Endpoints
* `GET /api/v1/crimes`: Filterable list of FIR crime records.
* `GET /api/v1/crimes/spatial/radius`: PostGIS spatial radius query endpoint.
* `GET /api/v1/criminals`: Searchable criminal dossier database.
* `GET /api/v1/network/{criminalId}`: Returns 2D physics force graph network data for criminal profile.
* `GET /api/v1/timeline/{investigationId}`: Returns chronological investigation event timeline.
* `GET /api/v1/audit/ledger`: WORM Cryptographic SHA-256 ledger endpoint.
* `GET /api/v1/reports/{reportId}/preview`: Generates printable HTML investigation case dossier.

---

## Running locally

```bash
cd backend
mvn spring-boot:run
```
Service starts on `http://localhost:8080`.
