# Master Interview Preparation

## Java and Spring Boot
### Basic: Layering ka practical use?
**Answer:** Controllers HTTP/STOMP boundary handle karte hain (`AuthController`, `ChatController`, `ChatWebSocketController`), implementations business/transaction logic (`ChatServiceImpl`, `PasswordResetServiceImpl`), repositories JPA access. Isse transport aur persistence concerns separate rehte hain.

**Follow-ups**
- **Why interface + impl?** Contracts (`ChatService`) caller ko implementation se decouple karte hain; small services me boilerplate trade-off hai.
- **What if logic controller me ho?** Transaction/reuse/testing scattered hoga.
- **Evidence?** `controller`, `service`, `impl`, `repository` packages and constructor injection.

### Intermediate: `@Transactional` boundaries kahan hain?
**Answer:** User registration, refresh/reset tokens, feedback upsert, chat CRUD transactional hain. Streaming long-lived Reactor flow ko ek DB transaction me hold nahi kiya; `TransactionTemplate` short begin/partial/final writes use karta hai.

**Follow-ups**
- **Long transaction kyun avoid?** Ollama stream ke duration tak DB connection/locks hold honge.
- **Rollback external call ko undo karega?** Nahi; filesystem/email/Ollama transaction resource nahi hain.
- **Gap?** `DocumentServiceImpl.uploadDocument` transactional nahi and compensation absent.

### Advanced: Global exception strategy limitation?
**Answer:** `RuntimeException` broadly 400 map hota hai. Validation exceptions ka dedicated handler nahi dikh raha, aur infrastructure/programming runtime errors bhi 400 ban sakte hain. Domain exceptions with explicit status/problem details better honge.

## REST APIs
### Basic: Current API families?
**Answer:** Controller mappings `/auth`, `/chat`, `/search`, `/api/documents`, `/api/v1/admin/feedback` hain. Context path `/api/v1` add hone ke baad last two effective paths respectively `/api/v1/api/documents` aur `/api/v1/api/v1/admin/feedback` bante hain. Swagger and actuator routes separately exposed hain.

**Follow-up:** Doubled path production concern kyun hai?  
**Expected answer:** Controller constants already `/api...` prefix carry karte hain while servlet context bhi `/api/v1` hai. Frontend document call current doubled route ke saath align hai, but contract confusing/drift-prone hai; controller mappings ko context-relative consistent banana better hoga.

### Intermediate: Message pagination ka fix?
**Answer:** `45cef8c` full fetch avoid karta hai. Controller optional `page,size` validate karta hai; `MessageRepository.findPageByChatSessionId(..., PageRequest)` DB query me paging/order apply karta hai.

**Follow-up:** Response `Page` metadata deta hai?  
**Expected answer:** Nahi, service `.getContent()` return karta hai; client total pages/count nahi paata.

### Advanced: Endpoint ownership security?
**Answer:** Session/message operations current user resolve karke `findByIdAndUser_Id` use karte hain. Feedback message→session→user ID check karta hai. Document chat `existsByIdAndUploadedBy_Username` verifies ownership.

## Validation and Errors
### Basic: Bean Validation examples?
**Answer:** Login/register/reset/title/chat/refresh DTOs `@NotBlank`; reset password `@Size(min=6)`; email `@Email`; feedback type `@NotNull`.

### What if malformed JSON/validation fails?
**Answer:** Spring MVC rejects before controller. Repository has no dedicated `MethodArgumentNotValidException` mapping, so exact payload/status framework defaults par depend karta hai; custom project error shape guaranteed claim nahi karna.

### Why DTO instead of Entity?
**Answer:** Input contracts narrow rakhne aur persistence fields hide karne ke liye. Lekin `ChatSession` directly return hota hai, so project mixed approach use karta hai.

## PostgreSQL, JPA and Flyway
### Basic: Schema evolution?
**Answer:** V1 users/sessions/messages; V2 model name; V3 refresh tokens; V4 email; V5 reset tokens; V6 pinned; V7/V8 feedback; V9 docs; V10 chunks; V11 JSONB embeddings; V12 unique non-null file hash.

### Intermediate: Important constraints?
**Answer:** username/email/hash uniqueness; token hash uniqueness; feedback unique `(message_id,user_id)` + enum CHECK; one embedding per chunk; FK cascades on token/feedback/chunk paths where migrations specify them.

### Advanced: V12 special kyun?
**Answer:** Existing PDFs ka SHA-256 DB columns se reconstruct nahi ho sakta. Migration column nullable add karke unique constraint banata hai, NULL rows milne par explicit exception deta hai, then NOT NULL enforce karta hai—fake backfill invent nahi karta.

### What if two identical PDFs upload simultaneously?
**Answer:** Service pre-check race kar sakta hai; V12 unique hash final protection hai. One request constraint failure face karega. Better dedicated conflict mapping plus temp-file cleanup.

### Expert: JSONB vector scalability?
**Answer:** `findByChunk_Document_Id` all candidate embeddings loads; cosine/BM25 Java me O(number of chunks × dimensions) score hote hain. Large corpus ke liye pgvector ANN/index or vector DB, metadata filters and offline re-index pipeline better.

## JWT and Security
### Basic: REST auth flow?
**Answer:** Login uses `AuthenticationManager`; BCrypt-backed `UserDetailsService`; `JwtUtil` token issue/validate; `JwtFilter` bearer parse karke stateless `SecurityContext` set karta hai.

### Intermediate: WebSocket auth different kaise?
**Answer:** HTTP handshake path permit-all hai, but STOMP CONNECT native `Authorization` header `StompJwtChannelInterceptor` validate karta hai and authenticated principal session par set karta hai. `/app/*` SEND needs principal.

### Advanced: Refresh rotation?
**Answer:** Raw UUID client ko, SHA-256 hash DB me. Refresh request active hash reads, expiry check, old revoke, new access + refresh persist—all transaction me.

**Follow-ups**
- **Hash kyun?** DB leak me reusable raw bearer token expose na ho.
- **Salt kyun nahi?** High-entropy random token dictionary-resistant hai; deterministic lookup required. HMAC/pepper further hardening ho sakta hai.
- **Concurrent replay?** Explicit lock/atomic update absent; race possible.
- **Register bhi refresh token deta hai?** Current `UserServiceImpl.register` access token/username return karta hai; refresh token login path par create hota hai.
- **Frontend rotation use karta hai?** Nahi. `AuthContext` access token store karta hai aur `/auth/refresh` call nahi karta, so backend capability UI lifecycle me integrated nahi.

### Password reset security?
**Answer:** Forgot response account existence hide karta hai; active reset tokens revoke; raw one-time token email; hash DB; expiry; password BCrypt; reset token revoke and active refresh tokens revoke.

### Why not localStorage?
**Answer:** Multi-user tab bug. `sessionStorage` tab-specific. XSS protection nahi milti; HttpOnly cookies alternate architecture hain.

## Rate Limiting
### Basic: Kahan applied?
**Answer:** `IpRateLimitingFilter` login POST and non-OPTIONS `/chat` REST paths par Bucket4j buckets use karta hai, 429 `ErrorResponse` return karta hai.

### Advanced: Production risks?
**Answer:** Buckets per-process maps me hain, IP entries eviction nahi, `X-Forwarded-For` blindly first value trust hota hai, STOMP sends covered nahi. Redis-backed limiter/trusted proxy policy needed.

## WebSocket/STOMP and Streaming
### Basic: Destinations?
**Answer:** Handshake `/api/v1/ws-chat`; publish `/app/chat` and `/app/chat/stop`; subscribe `/user/queue/messages`.

### Intermediate: Correlation IDs kyun?
**Answer:** `clientStreamId` stale stream filter, `messageId` assistant bubble route, `userMessageId`/`editTargetMessageId` DB/UI correlation. Downstream chunk/done/error echo IDs.

### Advanced: `replaceAndStart` race fix?
**Answer:** Same principal key par `ConcurrentHashMap.compute` old `Disposable` cancel + new subscription registration same critical section me karta hai. `streamId` current-owner checks late chunks/cleanup ko reject karte hain.

### What if user presses STOP?
**Answer:** Registry metadata/local disposable remove and dispose; controller matching `clientStreamId` DONE sends. Reactor `doFinally` partial accumulator persist kar sakta hai when registry indicates user stop.

### What if socket disconnects but Ollama runs?
**Answer:** Transient disconnect path partial DB state recoverable rakhta hai; active metadata/polling UI reconcile karte hain. Cleanup listener disconnect markers use karta hai. Exact cross-instance stream execution still local limitation hai.

### Why STOMP simple broker limitation?
**Answer:** In-process simple broker multi-instance subscription routing share nahi karta. Redis state presence usko distributed broker nahi banati.

## Redis
### Basic: Redis me kya stored?
**Answer:** Active stream JSON with TTL, disconnected users sorted set, and a structural rate-limit counter helper. Active service falls back to local maps.

### What if Redis write succeeds but local process dies?
**Answer:** Metadata TTL tak visible ho sakta hai but owner process stream gone. Owner instance ID stored hai, but current code complete lease/takeover protocol implement nahi karta.

### Why fallback?
**Answer:** Single-instance availability maintain. Trade-off is degraded cross-instance consistency.

## Ollama and LLM
### Basic: Sync vs streaming?
**Answer:** REST `/chat` `OllamaClient` sync path; WebSocket `OllamaStreamingServiceImpl` WebClient NDJSON Flux path.

### Intermediate: Whitespace chunks kyun preserve?
**Answer:** Qwen/model stream me newline/spaces Markdown structure carry karte hain. `dd49b14` terminal done-empty frame only suppress karta hai, otherwise whitespace retains.

### What if malformed NDJSON line?
**Answer:** Parser warning log karke that line drop karta hai; stream continue. Repeated malformed output could silently incomplete response create karega—metric/error threshold improve kar sakta hai.

## RAG and Document Chat
### Basic: Upload flow?
**Answer:** Authenticated multipart PDF validation (extension, MIME, 25MB), SHA-256 duplicate check, local disk copy, PDFBox extraction, semantic chunking, heading-aware embedding inputs, Ollama embeddings, DB document/chunk/embedding persistence.

### Intermediate: Chunking algorithm?
**Answer:** 300–1000 bounds, configured ~800 target/100 overlap; headings detect; short paragraphs merge without crossing headings; oversized paragraphs sentence/list/semicolon units me split; overlap original offsets se; metadata computed though only content/index persist.

### Intermediate: Retrieval?
**Answer:** Query embedding + stored cosine, optional BM25; each channel max-normalize; configurable vector/keyword weighted fusion; hybrid score sort, cosine tie-break, chunk index final tie-break; top-K.

### Advanced: Context control?
**Answer:** Exact normalized duplicate remove, max chunks/max chars, selected chunks labeled. Prompt says context-only and exact fallback sentence; however prompt injection immunity guaranteed nahi.

### Why prefixes `search_document:` / `search_query:`?
**Answer:** Embedding implementation task-specific prefixes uses. Repository doesn't contain benchmark proving gain; model-aligned retrieval intent reasonable inference hai.

### What if embedding call partially fails?
**Answer:** Sequential “batch windows” each failure as null record; upload skips missing embedding; at least one required. This can persist incomplete index; response doesn't expose missing count. True batch HTTP nahi.

### What if document belongs to another user?
**Answer:** DOCUMENT mode controller owner-scoped existence check fails and sends error+done before retrieval.

### Why RAG instead of whole PDF prompt?
**Answer:** Current bounded top-K/context design token/context size control aur relevance improve karta hai. Trade-off retrieval miss; no measured quality suite exists.

## React Frontend
### Basic: State orchestration?
**Answer:** `ChatApp.jsx` sessions, draft chats, selected chat, stream IDs, recovery, search, pin/feedback/document mode coordinate karta hai; API wrappers separate; `websocket.js` STOMP lifecycle encapsulate karta hai.

### Intermediate: Optimistic feedback?
**Answer:** UI selection immediately update karta hai, API fail par rollback. Duplicate-click guard exists. DB unique invariant remains backend protection.

### Advanced: Cross-tab isolation?
**Answer:** Auth token/session stream ownership `sessionStorage`; stream metadata includes tab ownership and stale connection/stream IDs ignored. Sidebar collapse preference intentionally `localStorage` because UX preference cross-tab share ho sakta hai.

### Voice input flow?
**Answer:** `InputBox` runtime `SpeechRecognition || webkitSpeechRecognition` detect karta hai, locale sets, interim/final transcript input me writes, stop/error/end lifecycle cleans state. Audio server ko send/store nahi hota.

## Testing and CI
### Basic: Actual coverage?
**Answer:** Current main one context-load test, no frontend suite. GitHub Actions PostgreSQL service ke saath backend package and frontend build run karta hai.

### Advanced: First tests to add?
**Answer:** Refresh replay concurrency, session/document ownership, feedback unique race, stream stop/replacement, Redis fallback, PDF transaction cleanup, hybrid ranking fixtures, STOMP auth contract, React recovery and speech unsupported state.

### Why build passing != feature tested?
**Answer:** Compilation/context startup wiring catch karta hai; behavior/race/contract assertions absent hain.

## Expert System Design
### Scale to multiple instances?
**Answer:** Externalize broker, route per-user stream events, lease stream ownership, use distributed rate limiting, object storage, vector index, durable jobs for indexing, idempotency keys, observability. Current Redis metadata is foundation but not complete distributed architecture.

### Strongest project discussion?
**Answer:** Evolution explain karo: IDs + partial persistence + active status solve reconnect; Redis metadata extends visibility; explicit mode fixes RAG leakage; DB constraints back application checks. Saath me limitations honestly identify karo.

### What would you not claim?
**Answer:** Production-ready distributed streaming, comprehensive tests, role-protected admin analytics, transactional PDF pipeline, vector DB, retries/circuit breaker, or measured RAG quality—repository evidence support nahi karta.
