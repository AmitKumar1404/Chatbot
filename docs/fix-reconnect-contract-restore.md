# fix/reconnect-contract-restore — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Add configurable Bucket4j rate limiting; branch name is misleading because diff is rate-limit work.

# Branch Purpose
Add configurable Bucket4j rate limiting; branch name is misleading because diff is rate-limit work.

# Base Branch / Branch Lineage
Commit `9434f50`; PR #26.

# Git Commit History
- `9434f50` — Add configurable Bucket4j rate limiting to protect login and chat endpoints

# Files Changed
- `README.md` — 9434f50:M
- `backend/Chatbot.postman_collection.json` — 9434f50:M
- `backend/pom.xml` — 9434f50:M
- `backend/src/main/java/com/chatbot/ratelimit/IpRateLimitingFilter.java` — 9434f50:A
- `backend/src/main/java/com/chatbot/ratelimit/RateLimitProperties.java` — 9434f50:A
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java` — 9434f50:M
- `backend/src/main/resources/application-prod.yml` — 9434f50:M

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `IpRateLimitingFilter.doFilterInternal`
- **Function name / class:** `IpRateLimitingFilter.doFilterInternal`
- **Input:** HttpServletRequest/Response/FilterChain
- **Output / side effect:** continues chain or writes 429
- **Internal flow:** Resolve route bucket; consume one token; on exhaustion serialize ErrorResponse.
- **Dependencies, DB/API, errors, security:** Bucket4j, ObjectMapper; filter before JwtFilter.
- **Before → After / impact:** New cross-cutting filter despite misleading branch name.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 2. `resolveClientIp`
- **Function name / class:** `resolveClientIp`
- **Input:** request
- **Output / side effect:** IP String
- **Internal flow:** Take first X-Forwarded-For value if nonblank, else remote address.
- **Dependencies, DB/API, errors, security:** Proxy header trust is not bounded.
- **Before → After / impact:** Introduced per-IP keying and spoofing concern.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 3. `newBucket`
- **Function name / class:** `newBucket`
- **Input:** RateLimitProperties.Rule
- **Output / side effect:** Bucket
- **Internal flow:** Build classic bandwidth with greedy refill.
- **Dependencies, DB/API, errors, security:** Configured capacity/refill/duration.
- **Before → After / impact:** Makes limits config-driven.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`IpRateLimitingFilter.doFilterInternal`** — New cross-cutting filter despite misleading branch name.
- **`resolveClientIp`** — Introduced per-IP keying and spoofing concern.
- **`newBucket`** — Makes limits config-driven.

## Exact diff-level function and hunk inventory
- `backend/src/main/java/com/chatbot/ratelimit/IpRateLimitingFilter.java`: `doFilterInternal()`, `resolveBucket()`, `isLoginRequest()`, `isChatRequest()`, `newBucket()`, `resolveClientIp()`
- `backend/src/main/java/com/chatbot/ratelimit/RateLimitProperties.java`: `getLogin()`, `setLogin()`, `getChat()`, `setChat()`, `getCapacity()`, `setCapacity()`, `getRefillTokens()`, `setRefillTokens()`, `getRefillDurationSeconds()`, `setRefillDurationSeconds()`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `backend/src/main/java/com/chatbot/ratelimit/IpRateLimitingFilter.java::doFilterInternal()` — Cross-cutting transport/security processing karta hai before controller/message handler execution. Git evidence: `9434f50`.
- `backend/src/main/java/com/chatbot/ratelimit/IpRateLimitingFilter.java::isChatRequest()` — Boolean predicate/guard hai jo branch invariant ya eligibility check karta hai. Git evidence: `9434f50`.
- `backend/src/main/java/com/chatbot/ratelimit/IpRateLimitingFilter.java::isLoginRequest()` — Boolean predicate/guard hai jo branch invariant ya eligibility check karta hai. Git evidence: `9434f50`.
- `backend/src/main/java/com/chatbot/ratelimit/IpRateLimitingFilter.java::newBucket()` — Inputs/configuration ko target DTO/prompt/chunk/bucket object me construct karta hai. Git evidence: `9434f50`.
- `backend/src/main/java/com/chatbot/ratelimit/IpRateLimitingFilter.java::resolveBucket()` — Identifier/context se owner-scoped ya current resource/state resolve karta hai; missing case caller ko propagate hota hai. Git evidence: `9434f50`.
- `backend/src/main/java/com/chatbot/ratelimit/IpRateLimitingFilter.java::resolveClientIp()` — Identifier/context se owner-scoped ya current resource/state resolve karta hai; missing case caller ko propagate hota hai. Git evidence: `9434f50`.
- `backend/src/main/java/com/chatbot/ratelimit/RateLimitProperties.java::getCapacity()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `9434f50`.
- `backend/src/main/java/com/chatbot/ratelimit/RateLimitProperties.java::getChat()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `9434f50`.
- `backend/src/main/java/com/chatbot/ratelimit/RateLimitProperties.java::getLogin()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `9434f50`.
- `backend/src/main/java/com/chatbot/ratelimit/RateLimitProperties.java::getRefillDurationSeconds()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `9434f50`.
- `backend/src/main/java/com/chatbot/ratelimit/RateLimitProperties.java::getRefillTokens()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `9434f50`.
- `backend/src/main/java/com/chatbot/ratelimit/RateLimitProperties.java::setCapacity()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `9434f50`.
- `backend/src/main/java/com/chatbot/ratelimit/RateLimitProperties.java::setChat()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `9434f50`.
- `backend/src/main/java/com/chatbot/ratelimit/RateLimitProperties.java::setLogin()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `9434f50`.
- `backend/src/main/java/com/chatbot/ratelimit/RateLimitProperties.java::setRefillDurationSeconds()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `9434f50`.
- `backend/src/main/java/com/chatbot/ratelimit/RateLimitProperties.java::setRefillTokens()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `9434f50`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `README.md` — 9434f50:M
- `backend/Chatbot.postman_collection.json` — 9434f50:M
- `backend/pom.xml` — 9434f50:M
- `backend/src/main/java/com/chatbot/ratelimit/IpRateLimitingFilter.java` — 9434f50:A
- `backend/src/main/java/com/chatbot/ratelimit/RateLimitProperties.java` — 9434f50:A
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java` — 9434f50:M
- `backend/src/main/resources/application-prod.yml` — 9434f50:M

# APIs Added/Modified
POST `/api/v1/auth/login` 5/min/IP; effective `/api/v1/chat/**` REST routes 60/min/IP; rejected request gets 429 JSON. STOMP is not covered.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
No migration; in-memory bucket maps.

# Frontend Changes
No direct frontend change.

# Backend Changes
IpRateLimitingFilter and RateLimitProperties.

# Security Changes
Filter runs before JwtFilter; trusts first X-Forwarded-For value.

# Testing Changes
No rate-limit tests.

# Technical Topics Covered
Servlet filters, Bucket4j, token bucket, IP extraction

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Route classification occurs before auth; limits are per JVM.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Abuse protection added, not reconnect behavior.

# Edge Cases
Spoofed forwarded IP, unbounded map, multi-instance bypass, STOMP bypass.

# Production Considerations
## Current implementation
Route classification occurs before auth; limits are per JVM.

## Improvement, not currently implemented
Trusted proxy config, Redis-backed buckets, eviction and tests.

# Interview Topics From This Branch
- Function boundaries: IpRateLimitingFilter.doFilterInternal, resolveClientIp, newBucket
- API contract: POST `/api/v1/auth/login` 5/min/IP; effective `/api/v1/chat/**` REST routes 60/min/IP; rejected request gets 429 JSON. STOMP is not covered.
- Persistence: No migration; in-memory bucket maps.
- Security: Filter runs before JwtFilter; trusts first X-Forwarded-For value.
- Failure/edge cases: Spoofed forwarded IP, unbounded map, multi-instance bypass, STOMP bypass.
- Separate exhaustive Q&A: `Interview/fix-reconnect-contract-restore.txt`
