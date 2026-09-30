# Production Deep Dive

## Current project actually does
- PostgreSQL via JPA; Flyway V1–V12; `ddl-auto=validate`.
- Redis stores active-stream metadata/disconnect markers with TTL and falls back to in-memory state.
- JWT secures REST; STOMP CONNECT validates same bearer token.
- Bucket4j protects REST login and `/chat` paths using per-process, per-IP buckets.
- Ollama calls use WebClient for streaming and RestTemplate for embeddings; stream timeout is 90 seconds.
- Actuator exposes health/info; GitHub Actions builds Maven + Vite with PostgreSQL.
- PDF files are stored on local disk; metadata/chunks/embeddings go to PostgreSQL.

## Production improvements (not implemented)
- Replace simple STOMP broker/local `Disposable` ownership with multi-instance broker and explicit stream-owner routing.
- Use Redis-backed/distributed rate-limit buckets, bounded key lifecycle, and trusted-proxy configuration.
- Add transactional/outbox-like upload workflow, object storage, cleanup compensation and malware/content validation.
- Add pgvector or dedicated vector index; current JSONB vectors are loaded and scored in Java for a selected document.
- Add HTTP client connect/read timeouts, bounded retries/circuit breaking, and dependency-specific metrics.
- Add structured logging without prompt/chunk leakage, tracing and SLO alerts.
- Add controller/service/repository/concurrency/RAG quality and frontend E2E tests.
- Restrict actuator health details and feedback analytics role; current effective `/api/v1/api/v1/admin/feedback/stats` has no admin authorization rule; doubled prefix bhi mapping smell hai.
- Remove per-chunk `System.out.println` logging from `ChatWebSocketController`; streamed content logs me sensitive user/document data leak kar sakta hai.
- Integrate frontend refresh-token lifecycle or remove misleading unused capability; current UI token expiry par `/auth/refresh` call nahi karti.
- Remove or complete dead paths (`StreamingMessageRenderer`, `useSidebarState`, replay-dedup refs) and add CI lint/test gates.
- Review `Message.userMessage`/`aiResponse` 10,000-character DB limits for long streamed answers and map overflow as controlled errors.

## Deep questions
### What if Redis is unavailable?
`RedisInfraStateStore` catches runtime failures, logs a warning once and uses local maps. Single instance can continue, but cross-instance visibility is lost. This is graceful degradation, not equivalent distributed consistency.

### What if Ollama disconnects?
Transient failures propagate through `isTransientStreamingFailure`; controller keeps partial state recoverable and does not emit terminal error/done. Non-transient errors become an error marker/event and terminal persistence. A 90-second Reactor timeout exists.

### What if two chat streams start for one principal?
`ActiveStreamRegistry.replaceAndStart` uses `ConcurrentHashMap.compute`: existing local stream is disposed and new metadata saved. `streamId` checks stop stale callbacks from owning cleanup. Across instances this atomicity is not global.

### What if the same feedback is submitted concurrently?
Service performs find-or-create upsert in a transaction and V7 has unique `(message_id,user_id)`. Without explicit lock, two creates can race; DB constraint protects invariant but one request may surface an integrity exception (currently generic handling may map it poorly).

### What if PDF indexing fails halfway?
File is copied before extraction/embedding/DB completion and `uploadDocument` is not `@Transactional`. Per-chunk saves can partially commit depending transaction behavior. No compensating file/database cleanup is visible. Production design should stage upload, wrap DB unit atomically, and delete/mark failed artifacts.

### What if query vectors have different dimensions?
`SimilarityServiceImpl` throws `IllegalArgumentException`; global runtime mapping yields 400. Operationally this usually indicates model/index mismatch and should be a controlled server/config error with re-index strategy.

### What if BM25 finds no keyword match?
All-zero BM25 disables keyword contribution; hybrid score uses weighted normalized vector score. Pure-vector mode is retained when keyword weight is disabled.

### What if retrieved context exceeds budget?
`ContextBuilderServiceImpl` stops at configured chunk and character limits and exact-normalized deduplication. It breaks when the next chunk would overflow; it does not truncate that chunk.

### Why JSONB embeddings, not pgvector?
Verified current decision: V11 stores `List<Float>` as JSONB and Java computes cosine. Reason is not explicitly documented. Reasonable inference: simpler dependency/setup for initial implementation. Trade-off: all vectors for document are loaded and no ANN/vector index exists.

### Why WebSocket/STOMP, not SSE?
Verified implementation needs bidirectional NEW/EDIT/STOP plus per-user queue and streaming chunks, which STOMP supports. SSE could handle server-to-client chunks but stop/edit still need separate HTTP and connection correlation. This rationale is inferred, not an ADR.

### Why DTOs instead of entities?
DTOs define auth/chat/feedback/stream contracts and avoid exposing every persistence field. Current project still returns `ChatSession` entity in some endpoints, so DTO isolation is partial, not universal.

### Why sessionStorage for token?
`ba1f78a` fixes cross-tab users sharing an origin-wide localStorage token. sessionStorage isolates tabs and survives same-tab reload. It remains JavaScript-readable, so XSS remains relevant; HttpOnly secure cookies would change CSRF/session architecture.

### Is CORS secure?
HTTP CORS is configured separately; WebSocket endpoint uses `setAllowedOriginPatterns("*")`. STOMP CONNECT still requires JWT, but production should restrict origins to reduce cross-origin abuse.
