# Cross-branch Evolution and Comparison

## Evolution sequence
1. `45e54e3` created Spring Boot auth/chat/WebSocket/Ollama/JPA base.
2. `feature/stream-stop-fix` established repository layout plus NEW/EDIT/STOP stream contract.
3. `feature/message-copy` and `feature/request-response` exposed editing/copying and controlled input UX.
4. `feature/login-page` added JWT-aware React routing and STOMP CONNECT authentication; later added password reset/email.
5. `feature/user-chat-history` persisted session/message UX and owner-scoped CRUD.
6. `feature/reconnect-recovery` added active stream metadata, partial persistence and reconnect synchronization.
7. Sidebar/search branches added responsive navigation, user-scoped search, DB paging and result highlighting.
8. `feature/streaming-ux-polish` hardened stale stream/cross-tab rendering; `feature/redis-stream-state` externalized metadata with fallback.
9. Swagger/Postman/model branches improved API discoverability and Ollama Markdown behavior.
10. Production hardening added env-driven secrets, Flyway, Actuator, CI and PostgreSQL service; rate-limit branch added Bucket4j.
11. Refresh/password reset branches added token rotation, revocation and hashed one-time reset tokens.
12. Pinned chats and feedback expanded persistent product features.
13. Document chat added PDF indexing and hybrid RAG; voice branch restored explicit chat modes then added browser dictation.

## Branch-by-branch comparison
- `feature/stream-stop-fix`: backend + frontend; STOMP contracts, cancellation/editing; no migration.
- `feature/message-copy`: frontend only; clipboard/edit controls.
- `feature/request-response`: frontend only; controlled input/streaming UX.
- `feature/login-page`: both sides; JWT/STOMP auth, routing, password reset, V4/V5, CI profile.
- `feature/user-chat-history`: both sides; session/message persistence APIs.
- `feature/reconnect-recovery`: both sides; partial persistence and active-stream recovery.
- `feature/responsive-collapsible-sidebar`: frontend CSS/state only.
- `feature/chat-search`: both sides; search API/UI and DB-level paging.
- `feature/search-dialog-collapsed-sidebar`: frontend dialog/highlighting; merged through another branch.
- `fix/multi-user-login`: frontend storage scope fix.
- `feature/streaming-ux-polish`: frontend + WebSocket lifecycle; alias tip `feature/redis-active-stream-registry`.
- `feature/redis-stream-state`: backend infra; Redis metadata + in-memory fallback.
- `chore/postman-collection-sync`: documentation/tooling only.
- `feature/swagger-integration`: backend OpenAPI + README.
- `feature/qwen2.5-model-upgrade`: AI config/parser + Markdown frontend.
- `feature/production-readiness-hardening`: config/migrations/CI/monitoring.
- `fix/reconnect-contract-restore`: rate limiting despite branch name.
- `feature/refresh-token-clean`: backend auth + V3.
- `bugfix/ui-fixes`: CSS clipping fix.
- `feature/pinned-chats`: both sides + V6.
- `feature/feedback-system`: both sides + V7/V8; tip-only stricter validation.
- `feature/document-chat`: backend-heavy RAG + V9–V12 + UI mode; tip-only tests.
- `feature/voice-input`: browser speech UI plus mode restore.
- `main`: integration snapshot at `a62003a`.

## Cross-branch interview questions
### Basic — Search evolution
**Question:** Search ek commit me complete hua tha?

**Answer:** Nahi. `9dd63af` backend API, `61d3916` UI, `45cef8c` DB paging fix, `6e79b84` collapsed icon, aur `51ad3cc` dialog/highlighting laaye. Last commit search branch se bana but multi-user-login PR ke through main me aaya.

**Follow-up:** Isse ownership analysis kya sikhata hai?  
**Expected answer:** Merge title alone reliable nahi; merge-parent unique commits aur file diffs inspect karne chahiye.  
**Why asked:** Git archaeology skill test.

### Intermediate — Stream state evolution
**Question:** Reconnect architecture kaise evolve hui?

**Answer:** Initial in-memory `ActiveStreamRegistry` se active status/partial DB persistence (`339d9d7`) aaya; `492d39c` client reconnect/stale guards harden karta hai; `f3fb373` cross-tab ownership/rendering; `e844265` metadata Redis me mirror karta hai with local fallback.

**Follow-up:** Redis ke baad system fully distributed hai?  
**Expected answer:** Nahi. Metadata shared hai, lekin Reactor `Disposable` process-local hai aur simple STOMP broker bhi local hai. Cross-instance cancellation/delivery ke liye distributed broker/execution ownership chahiye.  
**Why asked:** Technology presence ko scalability guarantee samajhne ki galti catch karna.

### Advanced — Auth evolution
**Question:** Access, refresh aur reset tokens me difference kya hai?

**Answer:** JWT access token signed bearer credential hai; refresh/reset values random UUID raw form me client/email ko milte hain aur SHA-256 hash DB me store hota hai. Refresh use par rotation + revoke; reset use par password BCrypt se update, reset token revoke aur active refresh tokens revoke.

**Follow-up:** Concurrent refresh replay kya karega?  
**Expected answer:** Current read-then-revoke transaction me explicit lock/version nahi; race possible hai. Conditional atomic update ya pessimistic lock plus replay-family revocation stronger hoga.  
**Why asked:** Transaction ko automatic race prevention na samajhna.

### Expert — Document mode regression
**Question:** RAG merge ke baad mode fix alag kyun aaya?

**Answer:** Merged RAG code document selection ko chat behavior se tie karta tha. `75fed10` tip aur main ka distinct patch `920edf5` dono explicit `ChatMode.NORMAL/DOCUMENT` area address karte hain: missing mode NORMAL, DOCUMENT needs owned document ID. Yeh accidental document-context leakage prevent karta hai.

**Follow-up:** Tests kahan hain?  
**Expected answer:** `75fed10` par mode/prompt tests hain, current main me nahi. Is gap ko honestly mention karna chahiye.  
**Why asked:** Branch state vs integrated state awareness.
