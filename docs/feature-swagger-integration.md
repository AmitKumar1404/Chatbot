# feature/swagger-integration — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Add Springdoc OpenAPI/Swagger UI and comprehensive root README.

# Branch Purpose
Add Springdoc OpenAPI/Swagger UI and comprehensive root README.

# Base Branch / Branch Lineage
Commit `d539dee`; PR #21.

# Git Commit History
- `d539dee` — docs: create detailed README with architecture, auth flows, APIs, streaming, and setup guide

# Files Changed
- `README.md` — d539dee:A
- `backend/pom.xml` — d539dee:M
- `backend/src/main/java/com/chatbot/config/OpenApiConfig.java` — d539dee:A
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java` — d539dee:M
- `backend/src/main/resources/application.yml` — d539dee:M

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `OpenApiConfig.chatbotOpenApi`
- **Function name / class:** `OpenApiConfig.chatbotOpenApi`
- **Input:** configured metadata
- **Output / side effect:** OpenAPI bean
- **Internal flow:** `app.openapi.*` se title/description/version inject karke `Info` banata hai, `bearerAuth` HTTP bearer/JWT `SecurityScheme` register karta hai aur global `SecurityRequirement` add karta hai.
- **Dependencies, DB/API, errors, security:** springdoc; public security matchers.
- **Before → After / impact:** Adds generated REST docs; STOMP remains outside OpenAPI.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`OpenApiConfig.chatbotOpenApi`** — Adds generated REST docs and bearer-JWT metadata; STOMP remains outside OpenAPI.

## Exact diff-level function and hunk inventory
- `backend/src/main/java/com/chatbot/config/OpenApiConfig.java`: `chatbotOpenApi()`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `backend/src/main/java/com/chatbot/config/OpenApiConfig.java::chatbotOpenApi()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `d539dee`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `README.md` — d539dee:A
- `backend/pom.xml` — d539dee:M
- `backend/src/main/java/com/chatbot/config/OpenApiConfig.java` — d539dee:A
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java` — d539dee:M
- `backend/src/main/resources/application.yml` — d539dee:M

# APIs Added/Modified
Security matchers `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html` permit docs; external URLs include context prefix, e.g. `/api/v1/v3/api-docs` and `/api/v1/swagger-ui/index.html`.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
None

# Frontend Changes
None

# Backend Changes
OpenApiConfig, security permit rules and config.

# Security Changes
Docs routes public; business routes unchanged.

# Testing Changes
No OpenAPI contract test.

# Technical Topics Covered
OpenAPI, Swagger, API metadata, public docs routes

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Springdoc scans REST controllers; STOMP requires separate contract docs.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Documentation discoverability added; later README/spec drift exists.

# Edge Cases
Public docs exposure, STOMP absent from OpenAPI, new APIs undocumented.

# Production Considerations
## Current implementation
Springdoc scans REST controllers; STOMP requires separate contract docs.

## Improvement, not currently implemented
Spec regression test and generated client/collection.

# Interview Topics From This Branch
- Function boundaries: OpenApiConfig.chatbotOpenApi
- API contract: Security matchers `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html` permit docs; external URLs include context prefix, e.g. `/api/v1/v3/api-docs` and `/api/v1/swagger-ui/index.html`.
- Persistence: None
- Security: Docs routes public; business routes unchanged.
- Failure/edge cases: Public docs exposure, STOMP absent from OpenAPI, new APIs undocumented.
- Separate exhaustive Q&A: `Interview/feature-swagger-integration.txt`
