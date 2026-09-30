# feature/user-chat-history — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Add persistent session CRUD/history and draft-chat orchestration with client/server message correlation.

# Branch Purpose
Add persistent session CRUD/history and draft-chat orchestration with client/server message correlation.

# Base Branch / Branch Lineage
PR #7 owns `f41fd74`; branch tip `22b0d98` entered main through reconnect PR #9.

# Git Commit History
- `f41fd74` — resolve sidebar multiple chat selection and click handling issues
- `22b0d98` — Refactor ChatApp WebSocket architecture with draft chat support, improved streaming lifecycle, and enhanced connection stability

# Files Changed
- `.DS_Store` — 22b0d98:M
- `backend/.DS_Store` — 22b0d98:M
- `backend/src/.DS_Store` — 22b0d98:M
- `backend/src/main/.DS_Store` — 22b0d98:M
- `backend/src/main/java/.DS_Store` — 22b0d98:M
- `backend/src/main/java/com/.DS_Store` — 22b0d98:M
- `backend/src/main/java/com/chatbot/.DS_Store` — 22b0d98:M
- `backend/src/main/java/com/chatbot/controller/ChatController.java` — f41fd74:M
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java` — f41fd74:M
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java` — f41fd74:M
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java` — f41fd74:M
- `backend/src/main/java/com/chatbot/dto/UpdateSessionTitleRequest.java` — f41fd74:A
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java` — f41fd74:M
- `backend/src/main/java/com/chatbot/model/Message.java` — f41fd74:M
- `backend/src/main/java/com/chatbot/repository/MessageRepository.java` — f41fd74:M
- `backend/src/main/java/com/chatbot/service/ChatService.java` — f41fd74:M
- `frontend/src/App.css` — f41fd74:M, 22b0d98:M
- `frontend/src/ChatApp.jsx` — f41fd74:M, 22b0d98:M
- `frontend/src/chatApi.js` — f41fd74:A
- `frontend/src/components/ChatWindow.jsx` — 22b0d98:M
- `frontend/src/components/InputBox.jsx` — 22b0d98:M
- `frontend/src/websocket.js` — 22b0d98:M

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `ChatServiceImpl.createEmptySession`
- **Function name / class:** `ChatServiceImpl.createEmptySession`
- **Input:** current SecurityContext user
- **Output / side effect:** saved ChatSession
- **Internal flow:** Resolve user; save New chat title/timestamp.
- **Dependencies, DB/API, errors, security:** UserRepository, ChatSessionRepository; @Transactional; POST sessions returns 201.
- **Before → After / impact:** Added explicit persistent session creation.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 2. `ChatServiceImpl.resolveStreamingSessionForUser`
- **Function name / class:** `ChatServiceImpl.resolveStreamingSessionForUser`
- **Input:** username, optional sessionId, first line
- **Output / side effect:** owned ChatSession
- **Internal flow:** In short TransactionTemplate: create titled session if null or owner-scope existing ID.
- **Dependencies, DB/API, errors, security:** User/Session repositories.
- **Before → After / impact:** Streaming moved from UI-only IDs to durable sessions.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 3. `ChatServiceImpl.persistWebsocketTurn`
- **Function name / class:** `ChatServiceImpl.persistWebsocketTurn`
- **Input:** username,type,session/message client IDs,text
- **Output / side effect:** DB side effect
- **Internal flow:** Owner-scope session; EDIT finds target and updates; NEW upserts by assistant client ID; mark complete and refresh title.
- **Dependencies, DB/API, errors, security:** MessageRepository and SessionRepository in TransactionTemplate.
- **Before → After / impact:** Added durable correlation for reconnect/edit.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 4. `ChatApp.loadSessions / selectChat`
- **Function name / class:** `ChatApp.loadSessions / selectChat`
- **Input:** token or session id
- **Output / side effect:** React session/message state
- **Internal flow:** Fetch sessions/history, map DB rows to UI, avoid multiple active selections.
- **Dependencies, DB/API, errors, security:** chatApi wrappers.
- **Before → After / impact:** Fixes sidebar selection and restores history.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 5. `createRealChatAndSend`
- **Function name / class:** `createRealChatAndSend`
- **Input:** draft and text
- **Output / side effect:** server session then stream send
- **Internal flow:** Create server session on first draft message, replace draft ID, continue send.
- **Dependencies, DB/API, errors, security:** createChatSession + handleSend.
- **Before → After / impact:** 22b0d98 changed eager session behavior to draft-first UX.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`ChatServiceImpl.createEmptySession`** — Added explicit persistent session creation.
- **`ChatServiceImpl.resolveStreamingSessionForUser`** — Streaming moved from UI-only IDs to durable sessions.
- **`ChatServiceImpl.persistWebsocketTurn`** — Added durable correlation for reconnect/edit.
- **`ChatApp.loadSessions / selectChat`** — Fixes sidebar selection and restores history.
- **`createRealChatAndSend`** — 22b0d98 changed eager session behavior to draft-first UX.

## Exact diff-level function and hunk inventory
- `backend/src/main/java/com/chatbot/controller/ChatController.java`: `createSession()`, `deleteSession()`, `updateSessionTitle()`
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java`: `sendJsonToUser()`
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java`: `getSessionId()`, `setSessionId()`, `getUserMessageId()`, `setUserMessageId()`
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java`: `getChatSessionId()`, `setChatSessionId()`
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java`: `createEmptySession()`, `deleteSession()`, `updateSessionTitle()`, `resolveStreamingSessionForUser()`, `persistWebsocketTurn()`, `findEditTarget()`, `maybeRefreshSessionTitle()`
- `frontend/src/ChatApp.jsx`: `ChatApp()`, `loadSessions()`, `mapped()`, `selectChat()`, `snap()`, `createNewChat()`, `deleteChat()`, `next()`, `renameChat()`, `currentChat()`, `createRealChatAndSend()`, `withoutDraft()`, changed context `function buildPriorDtos(messages) {`
- `frontend/src/chatApi.js`: `fetchChatSessions()`, `createChatSession()`, `fetchSessionMessages()`, `deleteChatSessionApi()`, `updateChatSessionTitleApi()`
- `frontend/src/components/ChatWindow.jsx`: changed context `const UserBubble = memo(function UserBubble({`
- `frontend/src/components/InputBox.jsx`: changed context `export default function InputBox({ onSend, onStop, isStreaming, disabled }) {`
- `frontend/src/websocket.js`: changed context `export function connectWebSocket({ accessToken, onMessage, onConnect, onError })`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `backend/src/main/java/com/chatbot/controller/ChatController.java::createSession()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f41fd74`.
- `backend/src/main/java/com/chatbot/controller/ChatController.java::deleteSession()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f41fd74`.
- `backend/src/main/java/com/chatbot/controller/ChatController.java::updateSessionTitle()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f41fd74`.
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java::sendJsonToUser()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f41fd74`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::getSessionId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `f41fd74`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::getUserMessageId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `f41fd74`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::setSessionId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `f41fd74`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::setUserMessageId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `f41fd74`.
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::getChatSessionId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `f41fd74`.
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::setChatSessionId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `f41fd74`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::createEmptySession()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f41fd74`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::deleteSession()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f41fd74`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::findEditTarget()` — Identifier/context se owner-scoped ya current resource/state resolve karta hai; missing case caller ko propagate hota hai. Git evidence: `f41fd74`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::maybeRefreshSessionTitle()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f41fd74`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::persistWebsocketTurn()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `f41fd74`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::resolveStreamingSessionForUser()` — Identifier/context se owner-scoped ya current resource/state resolve karta hai; missing case caller ko propagate hota hai. Git evidence: `f41fd74`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::updateSessionTitle()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f41fd74`.
- `frontend/src/ChatApp.jsx::ChatApp()` — Top-level entry/component function hai jo child state, handlers aur rendering/application bootstrap compose karta hai. Git evidence: `f41fd74`.
- `frontend/src/ChatApp.jsx::createNewChat()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f41fd74, 22b0d98`.
- `frontend/src/ChatApp.jsx::createRealChatAndSend()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `22b0d98`.
- `frontend/src/ChatApp.jsx::currentChat()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `22b0d98`.
- `frontend/src/ChatApp.jsx::deleteChat()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f41fd74`.
- `frontend/src/ChatApp.jsx::loadSessions()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f41fd74`.
- `frontend/src/ChatApp.jsx::mapped()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f41fd74`.
- `frontend/src/ChatApp.jsx::next()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f41fd74`.
- `frontend/src/ChatApp.jsx::renameChat()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f41fd74`.
- `frontend/src/ChatApp.jsx::selectChat()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f41fd74`.
- `frontend/src/ChatApp.jsx::snap()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f41fd74`.
- `frontend/src/ChatApp.jsx::withoutDraft()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `22b0d98`.
- `frontend/src/chatApi.js::createChatSession()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f41fd74`.
- `frontend/src/chatApi.js::deleteChatSessionApi()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f41fd74`.
- `frontend/src/chatApi.js::fetchChatSessions()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f41fd74`.
- `frontend/src/chatApi.js::fetchSessionMessages()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f41fd74`.
- `frontend/src/chatApi.js::updateChatSessionTitleApi()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f41fd74`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `.DS_Store` — 22b0d98:M
- `backend/.DS_Store` — 22b0d98:M
- `backend/src/.DS_Store` — 22b0d98:M
- `backend/src/main/.DS_Store` — 22b0d98:M
- `backend/src/main/java/.DS_Store` — 22b0d98:M
- `backend/src/main/java/com/.DS_Store` — 22b0d98:M
- `backend/src/main/java/com/chatbot/.DS_Store` — 22b0d98:M
- `backend/src/main/java/com/chatbot/controller/ChatController.java` — f41fd74:M
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java` — f41fd74:M
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java` — f41fd74:M
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java` — f41fd74:M
- `backend/src/main/java/com/chatbot/dto/UpdateSessionTitleRequest.java` — f41fd74:A
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java` — f41fd74:M
- `backend/src/main/java/com/chatbot/model/Message.java` — f41fd74:M
- `backend/src/main/java/com/chatbot/repository/MessageRepository.java` — f41fd74:M
- `backend/src/main/java/com/chatbot/service/ChatService.java` — f41fd74:M
- `frontend/src/App.css` — f41fd74:M, 22b0d98:M
- `frontend/src/ChatApp.jsx` — f41fd74:M, 22b0d98:M
- `frontend/src/chatApi.js` — f41fd74:A
- `frontend/src/components/ChatWindow.jsx` — 22b0d98:M
- `frontend/src/components/InputBox.jsx` — 22b0d98:M
- `frontend/src/websocket.js` — 22b0d98:M

# APIs Added/Modified
Controller mappings GET/POST `/chat/sessions`, DELETE `/chat/sessions/{id}`, PATCH title and GET messages; effective external paths `/api/v1/chat/...`.

### REST lifecycle and statuses
- GET `/api/v1/chat/sessions` returns 200 list; POST same path returns 201 created empty `ChatSession`.
- DELETE `/api/v1/chat/sessions/{sessionId}` returns 200 empty body after owner-scoped deletion.
- PATCH `/api/v1/chat/sessions/{sessionId}/title` consumes validated `UpdateSessionTitleRequest`, updates owner-scoped entity and returns 200.
- GET `/api/v1/chat/sessions/{sessionId}/messages` returns 200 mapped message history; later search branch adds optional pagination.
- Frontend `chatApi` wrappers feed `ChatApp` state. Every service operation resolves current authenticated username and scopes session lookup; nonexistent and foreign-owned IDs are intentionally indistinguishable at repository/service boundary, though current generic runtime mapping is 400 rather than a dedicated 404.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
Uses users→chat_sessions→messages; client bubble IDs and generation_complete evolved here/reconnect lineage.

# Frontend Changes
ChatApp load/select/create/delete/rename and draft conversion; chatApi wrappers.

# Backend Changes
ChatController/ChatServiceImpl CRUD and streaming persistence methods.

# Security Changes
All session lookup paths scope by current user.

# Testing Changes
No behavior tests.

# Technical Topics Covered
Session CRUD, ownership queries, transactional persistence, draft UI, client IDs

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Delay real session creation until first send; persist stream turns against owner-scoped session.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Sidebar multiple selection/click and draft lifecycle were corrected.

# Edge Cases
Draft abandoned, session deleted during stream, cross-user ID, duplicate assistant ID.

# Production Considerations
## Current implementation
Delay real session creation until first send; persist stream turns against owner-scoped session.

## Improvement, not currently implemented
Cursor pagination, DTO-only responses, optimistic locking and API tests.

# Interview Topics From This Branch
- Function boundaries: ChatServiceImpl.createEmptySession, ChatServiceImpl.resolveStreamingSessionForUser, ChatServiceImpl.persistWebsocketTurn, ChatApp.loadSessions / selectChat, createRealChatAndSend
- API contract: Controller mappings GET/POST `/chat/sessions`, DELETE `/chat/sessions/{id}`, PATCH title and GET messages; effective external paths `/api/v1/chat/...`.
- Persistence: Uses users→chat_sessions→messages; client bubble IDs and generation_complete evolved here/reconnect lineage.
- Security: All session lookup paths scope by current user.
- Failure/edge cases: Draft abandoned, session deleted during stream, cross-user ID, duplicate assistant ID.
- Separate exhaustive Q&A: `Interview/feature-user-chat-history.txt`
