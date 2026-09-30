# feature/redis-stream-state — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Persist active-stream/disconnect metadata in Redis with in-memory fallback.

# Branch Purpose
Persist active-stream/disconnect metadata in Redis with in-memory fallback.

# Base Branch / Branch Lineage
Builds on inherited `f3fb373`; branch-owned `e844265`; PR #16.

# Git Commit History
- `e844265` — Integrate Redis caching and infrastructure support

# Files Changed
- `backend/pom.xml` — e844265:M
- `backend/src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java` — e844265:M
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java` — e844265:M
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java` — e844265:M
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java` — e844265:A
- `backend/src/main/resources/application-prod.yml` — e844265:M
- `backend/src/main/resources/application.yml` — e844265:M

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `RedisInfraStateStore.saveActiveStream`
- **Function name / class:** `RedisInfraStateStore.saveActiveStream`
- **Input:** principal, ActiveStreamState
- **Output / side effect:** Redis/local side effect
- **Internal flow:** Always update local map; attempt JSON value set with TTL; catch Redis runtime failures.
- **Dependencies, DB/API, errors, security:** StringRedisTemplate, ObjectMapper.
- **Before → After / impact:** Registry metadata externalized while retaining fallback.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 2. `RedisInfraStateStore.getActiveStream`
- **Function name / class:** `RedisInfraStateStore.getActiveStream`
- **Input:** principal
- **Output / side effect:** Optional<ActiveStreamState>
- **Internal flow:** Read Redis and deserialize; if unavailable/missing fall back local.
- **Dependencies, DB/API, errors, security:** Redis key `chatbot:stream:active:`.
- **Before → After / impact:** Enables cross-instance metadata visibility, not cross-instance Disposable control.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 3. `markDisconnected / getExpiredDisconnectedUsers`
- **Function name / class:** `markDisconnected / getExpiredDisconnectedUsers`
- **Input:** principal/time or cutoff
- **Output / side effect:** ZSET/local state or expired users
- **Internal flow:** Write ZSET score and TTL; query bounded expired range; fall back local map.
- **Dependencies, DB/API, errors, security:** Redis sorted set.
- **Before → After / impact:** Disconnect cleanup became shared metadata.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 4. `withRedis / fromRedis`
- **Function name / class:** `withRedis / fromRedis`
- **Input:** operation supplier
- **Output / side effect:** best-effort result
- **Internal flow:** Execute Redis call; suppress runtime outage and warn once.
- **Dependencies, DB/API, errors, security:** AtomicBoolean warning guard.
- **Before → After / impact:** Explicit graceful degradation path.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`RedisInfraStateStore.saveActiveStream`** — Registry metadata externalized while retaining fallback.
- **`RedisInfraStateStore.getActiveStream`** — Enables cross-instance metadata visibility, not cross-instance Disposable control.
- **`markDisconnected / getExpiredDisconnectedUsers`** — Disconnect cleanup became shared metadata.
- **`withRedis / fromRedis`** — Explicit graceful degradation path.

## Exact diff-level function and hunk inventory
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java`: `isCurrentStream()`, `markDisconnected()`, `clearDisconnected()`, `expiredDisconnectedUsers()`
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java`: `getInstanceId()`, `saveActiveStream()`, `getActiveStream()`, `removeActiveStream()`, `removeActiveStreamIfMatches()`, `hasActiveStream()`, `isActiveStreamId()`, `markDisconnected()`, `clearDisconnected()`, `getExpiredDisconnectedUsers()`, `incrementRateLimitCounter()`, `activeStreamKey()`, `toJson()`, `fromJson()`, `withRedis()`, `fromRedis()`, `warnRedisUnavailable()`, `ActiveStreamState()`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java::clearDisconnected()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java::expiredDisconnectedUsers()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java::isCurrentStream()` — Boolean predicate/guard hai jo branch invariant ya eligibility check karta hai. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java::markDisconnected()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::ActiveStreamState()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::activeStreamKey()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::clearDisconnected()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::fromJson()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::fromRedis()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::getActiveStream()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::getExpiredDisconnectedUsers()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::getInstanceId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::hasActiveStream()` — Boolean predicate/guard hai jo branch invariant ya eligibility check karta hai. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::incrementRateLimitCounter()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::isActiveStreamId()` — Boolean predicate/guard hai jo branch invariant ya eligibility check karta hai. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::markDisconnected()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::removeActiveStream()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::removeActiveStreamIfMatches()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::saveActiveStream()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::toJson()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::warnRedisUnavailable()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `e844265`.
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java::withRedis()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `e844265`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `backend/pom.xml` — e844265:M
- `backend/src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java` — e844265:M
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java` — e844265:M
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java` — e844265:M
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java` — e844265:A
- `backend/src/main/resources/application-prod.yml` — e844265:M
- `backend/src/main/resources/application.yml` — e844265:M

# APIs Added/Modified
No new endpoint; effective GET `/api/v1/chat/stream/active` reads new store indirectly.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
No SQL migration.
- Active metadata key: `chatbot:stream:active:{principal}`, JSON `ActiveStreamState`, configured TTL default 1800 seconds and minimum 60 seconds.
- Disconnect markers: shared sorted set `chatbot:stream:disconnected`; score is disconnect epoch milliseconds, lookup is capped at 256 users and key TTL defaults to 3600 seconds.
- `chatbot:ratelimit:{route}:{principal}:{timeBucket}` design appears in `incrementRateLimitCounter()`, but it is a structure-only helper in this branch; later Bucket4j filter does not use it.
- Local `ConcurrentHashMap` fallback survives Redis call failure only within the current JVM and can diverge between instances.

# Frontend Changes
Recovery behavior consumes shared metadata.

# Backend Changes
RedisInfraStateStore and ActiveStreamRegistry integration.

# Security Changes
State keyed by principal; owner instance recorded but not enforced.

# Testing Changes
No Redis integration/failover tests.

# Technical Topics Covered
Redis keys, TTL, sorted sets, graceful degradation, concurrent maps

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Redis is state registry, not response cache; local Disposable remains JVM-owned.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Single-process recovery metadata limitation improved.

# Edge Cases
Redis down, stale TTL, wrong instance receives STOP, split brain.

# Production Considerations
## Current implementation
Redis is state registry, not response cache; local Disposable remains JVM-owned.

## Improvement, not currently implemented
Distributed broker/lease, atomic Lua/CAS, health metrics and failover tests.

# Interview Topics From This Branch
- Function boundaries: RedisInfraStateStore.saveActiveStream, RedisInfraStateStore.getActiveStream, markDisconnected / getExpiredDisconnectedUsers, withRedis / fromRedis
- API contract: No new endpoint; effective GET `/api/v1/chat/stream/active` reads new store indirectly.
- Persistence: No SQL migration; Redis active key and disconnected ZSET.
- Security: State keyed by principal; owner instance recorded but not enforced.
- Failure/edge cases: Redis down, stale TTL, wrong instance receives STOP, split brain.
- Separate exhaustive Q&A: `Interview/feature-redis-stream-state.txt`
