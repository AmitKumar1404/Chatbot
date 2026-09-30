# feature/refresh-token-clean — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Implement hashed refresh tokens with rotation/revocation and refresh endpoint.

# Branch Purpose
Implement hashed refresh tokens with rotation/revocation and refresh endpoint.

# Base Branch / Branch Lineage
Commit `3e9de42`; PR #28; old `feature/refresh-token` is duplicate stale lineage.

# Git Commit History
- `3e9de42` — Implement refresh token authentication with token rotation

# Files Changed
- `README.md` — 3e9de42:M
- `backend/Chatbot.postman_collection.json` — 3e9de42:M
- `backend/src/main/java/com/chatbot/constant/AppConstants.java` — 3e9de42:M
- `backend/src/main/java/com/chatbot/controller/AuthController.java` — 3e9de42:M
- `backend/src/main/java/com/chatbot/dto/AuthResponse.java` — 3e9de42:M
- `backend/src/main/java/com/chatbot/dto/RefreshTokenRequest.java` — 3e9de42:A
- `backend/src/main/java/com/chatbot/impl/RefreshTokenServiceImpl.java` — 3e9de42:A
- `backend/src/main/java/com/chatbot/model/RefreshToken.java` — 3e9de42:A
- `backend/src/main/java/com/chatbot/repository/RefreshTokenRepository.java` — 3e9de42:A
- `backend/src/main/java/com/chatbot/security/JwtFilter.java` — 3e9de42:M
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java` — 3e9de42:M
- `backend/src/main/java/com/chatbot/service/RefreshTokenService.java` — 3e9de42:A
- `backend/src/main/resources/application.yml` — 3e9de42:M
- `backend/src/main/resources/db/migration/V3__create_refresh_tokens_table.sql` — 3e9de42:A

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `RefreshTokenServiceImpl.createRefreshToken`
- **Function name / class:** `RefreshTokenServiceImpl.createRefreshToken`
- **Input:** username
- **Output / side effect:** raw UUID token
- **Internal flow:** Load user; generate UUID; persist SHA-256 hash with expiry; return raw once.
- **Dependencies, DB/API, errors, security:** User/RefreshToken repositories; transaction.
- **Before → After / impact:** Adds server-stored refresh credential.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 2. `refreshAccessToken`
- **Function name / class:** `refreshAccessToken`
- **Input:** raw refresh token
- **Output / side effect:** AuthResponse with new access+refresh
- **Internal flow:** Hash lookup unrevoked; reject/mark expired; revoke old; issue JWT and rotated refresh in one transaction.
- **Dependencies, DB/API, errors, security:** JwtUtil, repositories; BadCredentialsException→401.
- **Before → After / impact:** Implements rotation; no row lock for concurrent replay.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 3. `hashToken`
- **Function name / class:** `hashToken`
- **Input:** raw token
- **Output / side effect:** 64-char SHA-256 hex
- **Internal flow:** Digest UTF-8 bytes and hex encode; throw if algorithm unavailable.
- **Dependencies, DB/API, errors, security:** MessageDigest.
- **Before → After / impact:** Avoids storing bearer token plaintext.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`RefreshTokenServiceImpl.createRefreshToken`** — Adds server-stored refresh credential.
- **`refreshAccessToken`** — Implements rotation; no row lock for concurrent replay.
- **`hashToken`** — Avoids storing bearer token plaintext.

## Exact diff-level function and hunk inventory
- `backend/src/main/java/com/chatbot/controller/AuthController.java`: `refresh()`
- `backend/src/main/java/com/chatbot/impl/RefreshTokenServiceImpl.java`: `createRefreshToken()`, `refreshAccessToken()`, `persistRefreshToken()`, `hashToken()`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `backend/src/main/java/com/chatbot/controller/AuthController.java::refresh()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `3e9de42`.
- `backend/src/main/java/com/chatbot/impl/RefreshTokenServiceImpl.java::createRefreshToken()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `3e9de42`.
- `backend/src/main/java/com/chatbot/impl/RefreshTokenServiceImpl.java::hashToken()` — Raw refresh token ko SHA-256 hex digest me convert karta hai, taki lookup possible ho but bearer secret plaintext DB me store na ho. Git evidence: `3e9de42`.
- `backend/src/main/java/com/chatbot/impl/RefreshTokenServiceImpl.java::persistRefreshToken()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `3e9de42`.
- `backend/src/main/java/com/chatbot/impl/RefreshTokenServiceImpl.java::refreshAccessToken()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `3e9de42`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `README.md` — 3e9de42:M
- `backend/Chatbot.postman_collection.json` — 3e9de42:M
- `backend/src/main/java/com/chatbot/constant/AppConstants.java` — 3e9de42:M
- `backend/src/main/java/com/chatbot/controller/AuthController.java` — 3e9de42:M
- `backend/src/main/java/com/chatbot/dto/AuthResponse.java` — 3e9de42:M
- `backend/src/main/java/com/chatbot/dto/RefreshTokenRequest.java` — 3e9de42:A
- `backend/src/main/java/com/chatbot/impl/RefreshTokenServiceImpl.java` — 3e9de42:A
- `backend/src/main/java/com/chatbot/model/RefreshToken.java` — 3e9de42:A
- `backend/src/main/java/com/chatbot/repository/RefreshTokenRepository.java` — 3e9de42:A
- `backend/src/main/java/com/chatbot/security/JwtFilter.java` — 3e9de42:M
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java` — 3e9de42:M
- `backend/src/main/java/com/chatbot/service/RefreshTokenService.java` — 3e9de42:A
- `backend/src/main/resources/application.yml` — 3e9de42:M
- `backend/src/main/resources/db/migration/V3__create_refresh_tokens_table.sql` — 3e9de42:A

# APIs Added/Modified
Controller mapping POST `/auth/refresh`; effective external path `/api/v1/auth/refresh`; login refresh token return karta hai, register currently nahi.

### Endpoint lifecycle: refresh
- Request: `RefreshTokenRequest.refreshToken` par `@NotBlank`; malformed/blank body framework validation se 400 hota hai.
- Security: route permit-all hai because expired access token user ko refresh karna hota hai; possession and DB hash match authenticate the refresh attempt.
- Flow: `AuthController.refresh()` → `RefreshTokenService.refreshAccessToken()` → SHA-256 hash lookup → revoked/expiry checks → old row revoke → new access JWT + rotated refresh token persist/return.
- Success: 200 `AuthResponse`. Invalid/missing/expired/revoked token service `RuntimeException` path se current global mapping me 400. Concurrent replay strictly serialized nahi hai.
- Frontend caller repository me absent hai, so backend capability end-to-end browser flow nahi bani.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
- `V3__create_refresh_tokens_table.sql` creates `refresh_tokens(id BIGSERIAL PK, user_id BIGINT NOT NULL, token_hash VARCHAR(64) NOT NULL UNIQUE, expires_at TIMESTAMPTZ NOT NULL, revoked BOOLEAN NOT NULL DEFAULT FALSE, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW())`.
- `user_id` references `users(id) ON DELETE CASCADE`; account deletion therefore removes its refresh tokens. Indexes exist on `user_id` and `token_hash` (the unique constraint already provides uniqueness; the explicit hash index is additional).
- `refreshAccessToken()` runs transactionally for lookup/validation/revoke/new-token persistence, but repository query has no pessimistic lock or atomic conditional update. Two simultaneous replays can both observe `revoked=false`; production hardening needs row locking/token-family replay detection or conditional revoke.
- Raw token DB me persist nahi hota: SHA-256 hex is 64 chars and matches `VARCHAR(64)`.

# Frontend Changes
No `/auth/refresh` caller in AuthContext.

# Backend Changes
AuthController/RefreshTokenServiceImpl/entity/repository.

# Security Changes
Raw UUID returned once; only hash stored; old token revoked on rotation.

# Testing Changes
No replay/concurrency tests.

# Technical Topics Covered
Token rotation, SHA-256, transactions, expiry/revocation

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Transaction groups revoke + issue, but no explicit row lock.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Access-token renewal capability added; frontend does not consume it.

# Edge Cases
Concurrent replay race, expired token, DB compromise, token theft.

# Production Considerations
## Current implementation
Transaction groups revoke + issue, but no explicit row lock.

## Improvement, not currently implemented
Atomic conditional revoke/lock, token families, frontend integration and tests.

# Interview Topics From This Branch
- Function boundaries: RefreshTokenServiceImpl.createRefreshToken, refreshAccessToken, hashToken
- API contract: Controller mapping POST `/auth/refresh`; effective external path `/api/v1/auth/refresh`; login refresh token return karta hai, register currently nahi.
- Persistence: V3 refresh_tokens: unique hash, user FK cascade, expiry/revoked, indexes.
- Security: Raw UUID returned once; only hash stored; old token revoked on rotation.
- Failure/edge cases: Concurrent replay race, expired token, DB compromise, token theft.
- Separate exhaustive Q&A: `Interview/feature-refresh-token-clean.txt`
