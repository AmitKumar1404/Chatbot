# feature/production-readiness-hardening — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Externalize sensitive config, add Flyway/Actuator/CI/PostgreSQL service and resolve integration conflicts.

# Branch Purpose
Externalize sensitive config, add Flyway/Actuator/CI/PostgreSQL service and resolve integration conflicts.

# Base Branch / Branch Lineage
Merged remote lineage PR #23–#25/#27; local branch tip is stale parallel and not canonical.

# Git Commit History
- `b667f16` — feat(security): move sensitive configuration to environment variables
- `79867c7` — Add Flyway-based database versioning for production deployments
- `ee34e83` — Enable Actuator monitoring endpoints for application health and metadata
- `c291a9f` — merge conflict resolve - UI not open, searchin api not working properly, edit request not give response at same position
- `0879ea8` — Add CI workflow, monitoring endpoints and rate limiting documentation
- `93be5d5` — Pass environment variables to GitHub Actions
- `3a57294` — Add PostgreSQL service for CI tests
- `9ab6087` — Skip Spring Boot context test in CI build
- `050339d` — Add Flyway initial schema migration

# Files Changed
- `.github/workflows/ci.yml` — 0879ea8:A, 93be5d5:M, 3a57294:M, 9ab6087:M, 050339d:M
- `README.md` — 79867c7:M, ee34e83:M, 0879ea8:M
- `backend/.gitignore` — b667f16:M
- `backend/pom.xml` — b667f16:M, 79867c7:M, ee34e83:M
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java` — c291a9f:M
- `backend/src/main/java/com/chatbot/model/ChatSession.java` — 79867c7:M
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java` — ee34e83:M
- `backend/src/main/resources/application-prod.yml` — b667f16:M, 79867c7:M
- `backend/src/main/resources/application.yml` — b667f16:M, ee34e83:M
- `backend/src/main/resources/db/migration/V1__initial_schema.sql` — 050339d:A
- `backend/src/main/resources/db/migration/V2__add_model_name_column.sql` — 79867c7:A, 050339d:M
- `frontend/src/ChatApp.jsx` — c291a9f:M
- `frontend/src/websocket.js` — c291a9f:M

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `SecurityConfig actuator matcher`
- **Function name / class:** `SecurityConfig actuator matcher`
- **Input:** HTTP request
- **Output / side effect:** permit/deny decision
- **Internal flow:** Permit actuator paths while retaining authenticated defaults for business APIs.
- **Dependencies, DB/API, errors, security:** Spring Security.
- **Before → After / impact:** Actuator introduced; health details remain broadly visible.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 2. `ChatServiceImpl.persistWebsocketTurn` edit path
- **Function name / class:** `ChatServiceImpl.persistWebsocketTurn` edit branch
- **Input:** existing EDIT target and regenerated response
- **Output / side effect:** existing message content update
- **Internal flow:** Conflict-resolution commit keeps the message at its existing position by no longer resetting its timestamp and no longer deleting all later session messages.
- **Dependencies, DB/API, errors, security:** Message/session repositories and existing transaction boundary; no new endpoint.
- **Before → After / impact:** Earlier EDIT behavior retimestamped the row and truncated later messages; `c291a9f` comments those operations out, avoiding cascade-like history loss but allowing later conversation rows to remain after an earlier answer changes.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`SecurityConfig actuator matcher`** — Actuator introduced; health details remain broadly visible.
- **`ChatServiceImpl.persistWebsocketTurn` edit path** — Timestamp reset and `deleteByChatSession_IdAndIdGreaterThan` were disabled during conflict resolution.

## Exact diff-level function and hunk inventory
- `frontend/src/ChatApp.jsx`: `setConnectedState()`, `abortRecoveryRequests()`, `markDisconnected()`, `recoverInterruptedStream()`, changed context `function getInitialIsDesktopViewport() {`, changed context `function mapDbRowsToUi(rows, { chatId, ownedStream, username }) {`, changed context `export default function ChatApp() {`
- `frontend/src/websocket.js`: changed context `export function connectWebSocket({ accessToken, onMessage, onConnect, onError })`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `frontend/src/ChatApp.jsx::abortRecoveryRequests()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `c291a9f`.
- `frontend/src/ChatApp.jsx::markDisconnected()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `c291a9f`.
- `frontend/src/ChatApp.jsx::recoverInterruptedStream()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `c291a9f`.
- `frontend/src/ChatApp.jsx::setConnectedState()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `c291a9f`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `.github/workflows/ci.yml` — 0879ea8:A, 93be5d5:M, 3a57294:M, 9ab6087:M, 050339d:M
- `README.md` — 79867c7:M, ee34e83:M, 0879ea8:M
- `backend/.gitignore` — b667f16:M
- `backend/pom.xml` — b667f16:M, 79867c7:M, ee34e83:M
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java` — c291a9f:M
- `backend/src/main/java/com/chatbot/model/ChatSession.java` — 79867c7:M
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java` — ee34e83:M
- `backend/src/main/resources/application-prod.yml` — b667f16:M, 79867c7:M
- `backend/src/main/resources/application.yml` — b667f16:M, ee34e83:M
- `backend/src/main/resources/db/migration/V1__initial_schema.sql` — 050339d:A
- `backend/src/main/resources/db/migration/V2__add_model_name_column.sql` — 79867c7:A, 050339d:M
- `frontend/src/ChatApp.jsx` — c291a9f:M
- `frontend/src/websocket.js` — c291a9f:M

# APIs Added/Modified
Actuator `/health` and `/info`; no business API.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
V1 initial schema; V2 model_name; `ddl-auto=validate`, baseline-on-migrate.

# Frontend Changes
Conflict fix touched ChatApp/websocket.

# Backend Changes
Config, migration, security and CI workflow changes.

# Security Changes
Secrets become env placeholders; actuator paths permitted.

# Testing Changes
CI workflow added, env variables wired and PostgreSQL 16 service introduced. `9ab6087` temporarily ran `mvn clean package -DskipTests`; `050339d` restored `mvn clean package` after adding V1. Only the pre-existing context-load test executed; its later `@ActiveProfiles("local")` change belongs to `feature/login-page`, not this branch.

# Technical Topics Covered
Environment variables, Flyway, Actuator, GitHub Actions, PostgreSQL CI

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Flyway owns schema; CI PostgreSQL validates startup/build.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
UI/search/reconnect conflicts were merged, and edit regeneration stopped retimestamping/truncating later rows. Keeping later rows is itself a conversation-consistency trade-off that needs product-level tests.

# Edge Cases
Missing env, dirty legacy schema, public health details, CI without Redis/Ollama.

# Production Considerations
## Current implementation
Flyway owns schema; CI PostgreSQL validates startup/build.

## Improvement, not currently implemented
Secret manager, migration rehearsal, restricted actuator, richer CI tests.

# Interview Topics From This Branch
- Function boundaries: SecurityConfig actuator matcher, ChatbotApplicationTests.contextLoads
- API contract: Actuator `/health` and `/info`; no business API.
- Persistence: V1 initial schema; V2 model_name; `ddl-auto=validate`, baseline-on-migrate.
- Security: Secrets become env placeholders; actuator paths permitted.
- Failure/edge cases: Missing env, dirty legacy schema, public health details, CI without Redis/Ollama.
- Separate exhaustive Q&A: `Interview/feature-production-readiness-hardening.txt`
