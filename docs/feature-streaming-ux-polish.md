# feature/streaming-ux-polish — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Stabilize reconnect rendering, cross-tab ownership and streaming Markdown UX.

# Branch Purpose
Stabilize reconnect rendering, cross-tab ownership and streaming Markdown UX.

# Base Branch / Branch Lineage
Commit `f3fb373`; same tip as alias `feature/redis-active-stream-registry`; inherited by Redis PR #16.

# Git Commit History
- `f3fb373` — fix: stabilize websocket recovery, reconnect handling, and cross-tab stream isolation

# Files Changed
- `backend/src/main/java/com/chatbot/ChatbotApplication.java` — f3fb373:M
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java` — f3fb373:M
- `backend/src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java` — f3fb373:M
- `frontend/src/App.css` — f3fb373:M
- `frontend/src/ChatApp.jsx` — f3fb373:M
- `frontend/src/components/ChatWindow.jsx` — f3fb373:M
- `frontend/src/components/StreamingMessageRenderer.jsx` — f3fb373:A
- `frontend/src/websocket.js` — f3fb373:M

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `ChatApp stream ownership helpers`
- **Function name / class:** `ChatApp stream ownership helpers`
- **Input:** clientStreamId, assistant id, tab username/session
- **Output / side effect:** sessionStorage ownership record
- **Internal flow:** Save/clear tab-owned stream metadata; reject other-tab incomplete stream updates; reconcile on reconnect.
- **Dependencies, DB/API, errors, security:** sessionStorage and refs.
- **Before → After / impact:** Adds cross-tab isolation on top of reconnect branch.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 2. `StreamingMessageRenderer`
- **Function name / class:** `StreamingMessageRenderer`
- **Input:** partial Markdown text
- **Output / side effect:** incremental rendered output
- **Internal flow:** Parse/animate accumulated stream fragments for display.
- **Dependencies, DB/API, errors, security:** React; current main does not import it, so treat as branch implementation/dead current path.
- **Before → After / impact:** Added in branch but later live renderer uses ReactMarkdown.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`ChatApp stream ownership helpers`** — Adds cross-tab isolation on top of reconnect branch.
- **`StreamingMessageRenderer`** — Added in branch but later live renderer uses ReactMarkdown.

## Exact diff-level function and hunk inventory
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java`: `webSocketHeartbeatTaskScheduler()`
- `backend/src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java`: `onSessionConnect()`, `cleanupStaleDisconnectedStreams()`
- `frontend/src/ChatApp.jsx`: `setOwnedStream()`, `clearOwnedStream()`, `isActiveStreamOwnedByThisTab()`, `allowIncompleteAssistant()`, `cleanupSocketOnPageExit()`, changed context `function getInitialIsDesktopViewport() {`, changed context `function buildPriorDtos(messages) {`, changed context `function mapDbMessageToUi(m) {`, changed context `function mergeDbMessagesWithActiveStream(localMessages, dbRows, assistantId) {`, changed context `export default function ChatApp() {`
- `frontend/src/components/ChatWindow.jsx`: changed context `const AssistantBubble = memo(function AssistantBubble({`
- `frontend/src/components/StreamingMessageRenderer.jsx`: `animate()`, `html()`
- `frontend/src/websocket.js`: changed context `export function connectWebSocket({ accessToken, onMessage, onConnect, onError })`, changed context `export function disconnectWebSocket() {`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java::webSocketHeartbeatTaskScheduler()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f3fb373`.
- `backend/src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java::cleanupStaleDisconnectedStreams()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f3fb373`.
- `backend/src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java::onSessionConnect()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f3fb373`.
- `frontend/src/ChatApp.jsx::allowIncompleteAssistant()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f3fb373`.
- `frontend/src/ChatApp.jsx::cleanupSocketOnPageExit()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f3fb373`.
- `frontend/src/ChatApp.jsx::clearOwnedStream()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f3fb373`.
- `frontend/src/ChatApp.jsx::isActiveStreamOwnedByThisTab()` — Boolean predicate/guard hai jo branch invariant ya eligibility check karta hai. Git evidence: `f3fb373`.
- `frontend/src/ChatApp.jsx::setOwnedStream()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `f3fb373`.
- `frontend/src/components/StreamingMessageRenderer.jsx::animate()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f3fb373`.
- `frontend/src/components/StreamingMessageRenderer.jsx::html()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f3fb373`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `backend/src/main/java/com/chatbot/ChatbotApplication.java` — f3fb373:M
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java` — f3fb373:M
- `backend/src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java` — f3fb373:M
- `frontend/src/App.css` — f3fb373:M
- `frontend/src/ChatApp.jsx` — f3fb373:M
- `frontend/src/components/ChatWindow.jsx` — f3fb373:M
- `frontend/src/components/StreamingMessageRenderer.jsx` — f3fb373:A
- `frontend/src/websocket.js` — f3fb373:M

# APIs Added/Modified
No new API.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
None

# Frontend Changes
ChatApp ownership/recovery, websocket stale client guards, StreamingMessageRenderer.

# Backend Changes
WebSocket heartbeat and disconnect cleanup changes.

# Security Changes
Per-tab ownership reduces cross-tab mix-up; JWT contract unchanged.

# Testing Changes
No tests.

# Technical Topics Covered
Cross-tab sessionStorage, stale-event guards, Markdown streaming, heartbeat scheduling

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Separate tab ownership from username-wide backend stream key.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Reconnect/cross-tab stream isolation was explicit fix target.

# Edge Cases
Same user in two tabs, stale done event, page unload, renderer partial markdown.

# Production Considerations
## Current implementation
Separate tab ownership from username-wide backend stream key.

## Improvement, not currently implemented
Automated multi-tab E2E, broker-backed replay and remove dead renderer if unused.

# Interview Topics From This Branch
- Function boundaries: ChatApp stream ownership helpers, StreamingMessageRenderer
- API contract: No new API.
- Persistence: None
- Security: Per-tab ownership reduces cross-tab mix-up; JWT contract unchanged.
- Failure/edge cases: Same user in two tabs, stale done event, page unload, renderer partial markdown.
- Separate exhaustive Q&A: `Interview/feature-streaming-ux-polish.txt`
