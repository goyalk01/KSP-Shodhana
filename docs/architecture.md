# KSP Shodhana — Architecture Overview

See the full architecture design document in the project management artifacts.

## System Architecture

```
Browser → Next.js (BFF) → Spring Boot (API) → FastAPI (AI) → Gemini
                                    ↓
                          Catalyst Data Store
```

## Communication

All inter-service communication uses synchronous HTTP/JSON REST.

## Database

Zoho Catalyst Data Store with 7 tables:
- Crime
- Criminal
- CrimeCriminalLink
- CriminalNetwork
- Investigation
- TimelineEvent
- AuditLog

## Authentication

Zoho Catalyst Embedded Auth with token-based session management.
