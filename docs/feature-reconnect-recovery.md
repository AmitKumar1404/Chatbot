# feature/reconnect-recovery — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Recover interrupted streams after backend/network disconnect using active metadata, partial DB persistence and stale-connection guards.

# Branch Purpose
Recover interrupted streams after backend/network disconnect using active metadata, partial DB persistence and stale-connection guards.

# Base Branch / Branch Lineage
Depends on user-chat-history `22b0d98`; reconnect-owned `339d9d7`, `492d39c`; PR #9/#10.

# Git Commit History
- `339d9d7` — fix: improve websocket reconnect recovery and streaming stability during backend restart
- `492d39c` — fix: improve websocket stream interruption, reconnect recovery, and partial response persistence

# Files Changed
- `backend/.DS_Store` — 492d39c:M
- `backend/.java-version` — 492d39c:A
- `backend/pom.xml` — 492d39c:M
- `backend/src/main/java/com/chatbot/config/CorsConfig.java` — 339d9d7:M, 492d39c:M
- `backend/src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/constant/StreamConstants.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/controller/ChatController.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/dto/ActiveStreamStatusDto.java` — 339d9d7:A
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/impl/OllamaStreamingServiceImpl.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/model/Message.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/repository/MessageRepository.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java` — 492d39c:M
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/service/ChatService.java` — 339d9d7:M
- `frontend/src/App.css` — 339d9d7:M
- `frontend/src/ChatApp.jsx` — 339d9d7:M, 492d39c:M
- `frontend/src/chatApi.js` — 339d9d7:M, 492d39c:M
- `frontend/src/hooks/useNetworkStatus.js` — 339d9d7:A
- `frontend/src/hooks/useSidebarState.js` — 492d39c:A
- `frontend/src/websocket.js` — 339d9d7:M, 492d39c:M

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `ChatController.activeStream`
- **Function name / class:** `ChatController.activeStream`
- **Input:** Authentication
- **Output / side effect:** 200 ActiveStreamStatusDto or 204/401
- **Internal flow:** Validate principal; read registry status; return body only while active.
- **Dependencies, DB/API, errors, security:** ActiveStreamRegistry; protected REST.
- **Before → After / impact:** New recovery probe.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 2. `ChatServiceImpl.beginStreamingTurn`
- **Function name / class:** `ChatServiceImpl.beginStreamingTurn`
- **Input:** user/type/session/content/client IDs
- **Output / side effect:** placeholder DB side effect
- **Internal flow:** Create or reset target message with generationComplete=false before external stream.
- **Dependencies, DB/API, errors, security:** TransactionTemplate, owner-scoped repositories.
- **Before → After / impact:** Before final-only persistence; after crash leaves recoverable partial row.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 3. `ChatServiceImpl.updatePartialAiResponse`
- **Function name / class:** `ChatServiceImpl.updatePartialAiResponse`
- **Input:** user/session/assistant ID/partial text
- **Output / side effect:** DB side effect
- **Internal flow:** Owner-check; update incomplete row only.
- **Dependencies, DB/API, errors, security:** MessageRepository; short transaction.
- **Before → After / impact:** Added throttled partial persistence.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 4. `ChatApp.recoverInterruptedStream`
- **Function name / class:** `ChatApp.recoverInterruptedStream`
- **Input:** owned stream/session state
- **Output / side effect:** reconciled UI/restarted stream
- **Internal flow:** Abort old requests; fetch active metadata and DB messages; poll while active; ignore stale ownership/connection.
- **Dependencies, DB/API, errors, security:** fetchActiveStream/fetchSessionMessages, AbortController, refs.
- **Before → After / impact:** Changed disconnect from terminal failure to recoverable state machine.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 5. `websocket.connectWebSocket`
- **Function name / class:** `websocket.connectWebSocket`
- **Input:** access token and callbacks
- **Output / side effect:** connectionId
- **Internal flow:** Deactivate old client; create STOMP client; set CONNECT header; subscribe user queue; guard callbacks by current connection ID.
- **Dependencies, DB/API, errors, security:** @stomp/stompjs.
- **Before → After / impact:** Added reconnect caps and stale-client isolation.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`ChatController.activeStream`** — New recovery probe.
- **`ChatServiceImpl.beginStreamingTurn`** — Before final-only persistence; after crash leaves recoverable partial row.
- **`ChatServiceImpl.updatePartialAiResponse`** — Added throttled partial persistence.
- **`ChatApp.recoverInterruptedStream`** — Changed disconnect from terminal failure to recoverable state machine.
- **`websocket.connectWebSocket`** — Added reconnect caps and stale-client isolation.

## Exact diff-level function and hunk inventory
- `backend/src/main/java/com/chatbot/constant/StreamConstants.java`: `isTransientStreamingFailure()`
- `backend/src/main/java/com/chatbot/controller/ChatController.java`: `activeStream()`
- `backend/src/main/java/com/chatbot/dto/ActiveStreamStatusDto.java`: `getClientStreamId()`, `setClientStreamId()`, `getSessionId()`, `setSessionId()`, `getAssistantMessageId()`, `setAssistantMessageId()`
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java`: `beginStreamingTurn()`, `updatePartialAiResponse()`
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java`: `getActiveStreamStatus()`, `isDuplicateClientStream()`, `hasActiveStream()`, `ActiveStreamStatus()`, `ActiveStream()`
- `frontend/src/ChatApp.jsx`: `local()`, `clearRecoveryPoll()`, `syncMessagesFromServer()`, `chat()`, `row()`, `buildStreamResumePayload()`, `userIdx()`, `restartInterruptedGeneration()`, `recoverInterruptedStream()`, `setConnectedState()`, `abortRecoveryRequests()`, `markDisconnected()`, `startReplayDedup()`, changed context `function mapDbMessageToUi(m) {`, changed context `export default function ChatApp() {`
- `frontend/src/chatApi.js`: `fetchActiveStream()`, `fetchSessionMessages()`, changed context `export async function createChatSession(token) {`
- `frontend/src/hooks/useNetworkStatus.js`: `useNetworkStatus()`, `handleOnline()`, `handleOffline()`
- `frontend/src/hooks/useSidebarState.js`: `useSidebarState()`, `onChange()`, `toggleSidebar()`, `openSidebar()`, `closeSidebar()`, `sidebarClassName()`
- `frontend/src/websocket.js`: changed context `export function connectWebSocket({ accessToken, onMessage, onConnect, onError })`, changed context `export function disconnectWebSocket() {`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `backend/src/main/java/com/chatbot/constant/StreamConstants.java::isTransientStreamingFailure()` — Boolean predicate/guard hai jo branch invariant ya eligibility check karta hai. Git evidence: `339d9d7`.
- `backend/src/main/java/com/chatbot/controller/ChatController.java::activeStream()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `339d9d7`.
- `backend/src/main/java/com/chatbot/dto/ActiveStreamStatusDto.java::getAssistantMessageId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `339d9d7`.
- `backend/src/main/java/com/chatbot/dto/ActiveStreamStatusDto.java::getClientStreamId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `339d9d7`.
- `backend/src/main/java/com/chatbot/dto/ActiveStreamStatusDto.java::getSessionId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `339d9d7`.
- `backend/src/main/java/com/chatbot/dto/ActiveStreamStatusDto.java::setAssistantMessageId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `339d9d7`.
- `backend/src/main/java/com/chatbot/dto/ActiveStreamStatusDto.java::setClientStreamId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `339d9d7`.
- `backend/src/main/java/com/chatbot/dto/ActiveStreamStatusDto.java::setSessionId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `339d9d7`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::beginStreamingTurn()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `339d9d7`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::updatePartialAiResponse()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `339d9d7`.
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java::ActiveStream()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `339d9d7`.
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java::ActiveStreamStatus()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `339d9d7`.
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java::getActiveStreamStatus()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `339d9d7`.
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java::hasActiveStream()` — Boolean predicate/guard hai jo branch invariant ya eligibility check karta hai. Git evidence: `339d9d7`.
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java::isDuplicateClientStream()` — Boolean predicate/guard hai jo branch invariant ya eligibility check karta hai. Git evidence: `339d9d7`.
- `frontend/src/ChatApp.jsx::abortRecoveryRequests()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `492d39c`.
- `frontend/src/ChatApp.jsx::buildStreamResumePayload()` — Inputs/configuration ko target DTO/prompt/chunk/bucket object me construct karta hai. Git evidence: `339d9d7`.
- `frontend/src/ChatApp.jsx::chat()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `339d9d7, 492d39c`.
- `frontend/src/ChatApp.jsx::clearRecoveryPoll()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `339d9d7`.
- `frontend/src/ChatApp.jsx::local()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `339d9d7`.
- `frontend/src/ChatApp.jsx::markDisconnected()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `492d39c`.
- `frontend/src/ChatApp.jsx::recoverInterruptedStream()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `339d9d7, 492d39c`.
- `frontend/src/ChatApp.jsx::restartInterruptedGeneration()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `339d9d7`.
- `frontend/src/ChatApp.jsx::row()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `339d9d7`.
- `frontend/src/ChatApp.jsx::setConnectedState()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `492d39c`.
- `frontend/src/ChatApp.jsx::startReplayDedup()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `492d39c`.
- `frontend/src/ChatApp.jsx::syncMessagesFromServer()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `339d9d7`.
- `frontend/src/ChatApp.jsx::userIdx()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `339d9d7`.
- `frontend/src/chatApi.js::fetchActiveStream()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `339d9d7, 492d39c`.
- `frontend/src/chatApi.js::fetchSessionMessages()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `492d39c`.
- `frontend/src/hooks/useNetworkStatus.js::handleOffline()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `339d9d7`.
- `frontend/src/hooks/useNetworkStatus.js::handleOnline()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `339d9d7`.
- `frontend/src/hooks/useNetworkStatus.js::useNetworkStatus()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `339d9d7`.
- `frontend/src/hooks/useSidebarState.js::closeSidebar()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `492d39c`.
- `frontend/src/hooks/useSidebarState.js::onChange()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `492d39c`.
- `frontend/src/hooks/useSidebarState.js::openSidebar()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `492d39c`.
- `frontend/src/hooks/useSidebarState.js::sidebarClassName()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `492d39c`.
- `frontend/src/hooks/useSidebarState.js::toggleSidebar()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `492d39c`.
- `frontend/src/hooks/useSidebarState.js::useSidebarState()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `492d39c`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `backend/.DS_Store` — 492d39c:M
- `backend/.java-version` — 492d39c:A
- `backend/pom.xml` — 492d39c:M
- `backend/src/main/java/com/chatbot/config/CorsConfig.java` — 339d9d7:M, 492d39c:M
- `backend/src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/constant/StreamConstants.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/controller/ChatController.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/dto/ActiveStreamStatusDto.java` — 339d9d7:A
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/impl/OllamaStreamingServiceImpl.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/model/Message.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/repository/MessageRepository.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java` — 492d39c:M
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java` — 339d9d7:M
- `backend/src/main/java/com/chatbot/service/ChatService.java` — 339d9d7:M
- `frontend/src/App.css` — 339d9d7:M
- `frontend/src/ChatApp.jsx` — 339d9d7:M, 492d39c:M
- `frontend/src/chatApi.js` — 339d9d7:M, 492d39c:M
- `frontend/src/hooks/useNetworkStatus.js` — 339d9d7:A
- `frontend/src/hooks/useSidebarState.js` — 492d39c:A
- `frontend/src/websocket.js` — 339d9d7:M, 492d39c:M

# APIs Added/Modified
Controller mapping GET `/chat/stream/active`, effective `/api/v1/chat/stream/active`; existing STOMP contract hardened.

### Recovery endpoint lifecycle
- Authenticated GET `/api/v1/chat/stream/active` calls `ActiveStreamRegistry.getActiveStreamStatus(authentication.getName())`.
- Missing/unauthenticated principal returns 401; active metadata returns 200 `ActiveStreamStatusDto(clientStreamId, sessionId, assistantMessageId)`; idle state returns 204.
- Frontend reconnect flow compares correlation IDs, reloads persisted messages when needed and avoids accepting stale socket callbacks. Endpoint reports metadata, not missed chunks; database history is the durable recovery source after completed/persisted generation.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
Message `generationComplete` and partial `aiResponse` support recovery. Stream start par incomplete row banti/reset hoti hai; accumulated AI response approximately 400 ms interval par short transaction me update hota hai. Is branch phase me schema Hibernate `ddl-auto` se evolve hua—dedicated Flyway migration branch-owned nahi hai.

# Frontend Changes
ChatApp recovery state machine, network hook, abortable active/message fetches and websocket connection IDs. `frontend/src/hooks/useSidebarState.js` add hua tha, lekin repository usage search me import/caller evidence nahi mila; responsive sidebar later inline `ChatApp` state se implement hua.

# Backend Changes
ActiveStreamStatusDto, controller endpoint, registry and short transaction writes.

# Security Changes
Active status is current-principal scoped; JWT protects REST/STOMP.

# Testing Changes
No reconnect integration tests.

# Technical Topics Covered
WebSocket lifecycle, partial persistence, recovery polling, AbortController, network status

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Persist partial text periodically and reconcile server active metadata with tab-owned stream IDs.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Backend restart/connection interruption and stale events were verified commit targets.

# Edge Cases
Socket closes mid-chunk, old connection callback, STOP during reconnect, duplicate resume.

# Production Considerations
## Current implementation
Persist partial text periodically and reconcile server active metadata with tab-owned stream IDs.

## Improvement, not currently implemented
Lease-based ownership, integration chaos tests, bounded retry and metrics.

# Interview Topics From This Branch
- Function boundaries: ChatController.activeStream, ChatServiceImpl.beginStreamingTurn, ChatServiceImpl.updatePartialAiResponse, ChatApp.recoverInterruptedStream, websocket.connectWebSocket
- API contract: Controller mapping GET `/chat/stream/active`, effective `/api/v1/chat/stream/active`; existing STOMP contract hardened.
- Persistence: Message `generationComplete` and partial `aiResponse` support recovery.
- Security: Active status is current-principal scoped; JWT protects REST/STOMP.
- Failure/edge cases: Socket closes mid-chunk, old connection callback, STOP during reconnect, duplicate resume.
- Separate exhaustive Q&A: `Interview/feature-reconnect-recovery.txt`
