# feature/pinned-chats — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Add owner-scoped pin/unpin persistence and pinned UI grouping.

# Branch Purpose
Add owner-scoped pin/unpin persistence and pinned UI grouping.

# Base Branch / Branch Lineage
Commit `d0c21df`; PR #32.

# Git Commit History
- `d0c21df` — feat: implement pin and unpin chat sessions

# Files Changed
- `backend/src/main/java/com/chatbot/controller/ChatController.java` — d0c21df:M
- `backend/src/main/java/com/chatbot/dto/UpdateSessionPinnedRequest.java` — d0c21df:A
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java` — d0c21df:M
- `backend/src/main/java/com/chatbot/model/ChatSession.java` — d0c21df:M
- `backend/src/main/java/com/chatbot/service/ChatService.java` — d0c21df:M
- `backend/src/main/resources/db/migration/V6__add_is_pinned_to_chat_sessions.sql` — d0c21df:A
- `frontend/src/ChatApp.jsx` — d0c21df:M
- `frontend/src/chatApi.js` — d0c21df:M

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `ChatController.updateSessionPinned`
- **Function name / class:** `ChatController.updateSessionPinned`
- **Input:** sessionId, valid request
- **Output / side effect:** 200 ChatSession
- **Internal flow:** Delegate boolean pin state to service.
- **Dependencies, DB/API, errors, security:** UpdateSessionPinnedRequest and ChatService.
- **Before → After / impact:** New PATCH endpoint.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 2. `ChatServiceImpl.updateSessionPinned`
- **Function name / class:** `ChatServiceImpl.updateSessionPinned`
- **Input:** sessionId,pinned
- **Output / side effect:** saved owned ChatSession
- **Internal flow:** Resolve current user; owner-scoped find; mutate field; save transactionally.
- **Dependencies, DB/API, errors, security:** ChatSessionRepository.
- **Before → After / impact:** Adds persistence and authorization.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 3. `ChatApp.handleTogglePinned`
- **Function name / class:** `ChatApp.handleTogglePinned`
- **Input:** chat/session
- **Output / side effect:** optimistic/reloaded UI state
- **Internal flow:** Call PATCH wrapper and update session collection; derive pinned/unpinned groups.
- **Dependencies, DB/API, errors, security:** updateChatSessionPinnedApi.
- **Before → After / impact:** Adds UI grouping/control.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`ChatController.updateSessionPinned`** — New PATCH endpoint.
- **`ChatServiceImpl.updateSessionPinned`** — Adds persistence and authorization.
- **`ChatApp.handleTogglePinned`** — Adds UI grouping/control.

## Exact diff-level function and hunk inventory
- `backend/src/main/java/com/chatbot/controller/ChatController.java`: `updateSessionPinned()`
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java`: `updateSessionPinned()`
- `frontend/src/ChatApp.jsx`: `handleTogglePinned()`, `pinnedChats()`, `unpinnedChats()`, changed context `export default function ChatApp() {`
- `frontend/src/chatApi.js`: `updateChatSessionPinnedApi()`, changed context `export async function updateChatSessionTitleApi(token, sessionId, title) {`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `backend/src/main/java/com/chatbot/controller/ChatController.java::updateSessionPinned()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `d0c21df`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::updateSessionPinned()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `d0c21df`.
- `frontend/src/ChatApp.jsx::handleTogglePinned()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `d0c21df`.
- `frontend/src/ChatApp.jsx::pinnedChats()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `d0c21df`.
- `frontend/src/ChatApp.jsx::unpinnedChats()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `d0c21df`.
- `frontend/src/chatApi.js::updateChatSessionPinnedApi()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `d0c21df`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `backend/src/main/java/com/chatbot/controller/ChatController.java` — d0c21df:M
- `backend/src/main/java/com/chatbot/dto/UpdateSessionPinnedRequest.java` — d0c21df:A
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java` — d0c21df:M
- `backend/src/main/java/com/chatbot/model/ChatSession.java` — d0c21df:M
- `backend/src/main/java/com/chatbot/service/ChatService.java` — d0c21df:M
- `backend/src/main/resources/db/migration/V6__add_is_pinned_to_chat_sessions.sql` — d0c21df:A
- `frontend/src/ChatApp.jsx` — d0c21df:M
- `frontend/src/chatApi.js` — d0c21df:M

# APIs Added/Modified
Controller mapping PATCH `/chat/sessions/{sessionId}/pin`, effective `/api/v1/chat/sessions/{sessionId}/pin`, `UpdateSessionPinnedRequest`; success 200.

### Endpoint lifecycle: pin/unpin
- Request: path `sessionId` plus JSON `{ "pinned": boolean }`; primitive boolean missing ho to Java default `false` ban sakta hai because field par `@NotNull` possible nahi and no wrapper validation exists.
- Flow: frontend `updateSessionPinned()` wrapper → `ChatController.updateSessionPinned()` → `ChatService.updateSessionPinned()` → current-user-scoped session lookup → entity flag update/save → `ChatSession` response → sidebar list reorder/state update.
- Success: 200. Unknown/not-owned session service exception current handler se 400 ho sakta hai; dedicated 404 mapping evidence nahi.
- Authentication: JWT-protected chat route; owner-scoped query cross-user mutation prevent karta hai. (Technical identifier names remain exact; runtime response entity is directly exposed rather than dedicated response DTO.)

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
- `V6__add_is_pinned_to_chat_sessions.sql` idempotent-style `ADD COLUMN IF NOT EXISTS is_pinned BOOLEAN NOT NULL DEFAULT FALSE` use karta hai. Existing sessions backfill/default false paate hain, so null handling application me required nahi.
- `ChatSession.isPinned` mapping and `updateSessionPinned()` owner-scoped session ko mutate karke save karte hain. No new index is added; current list sorting client-side/persistence query behavior par depend karta hai.
- Concurrent PATCH requests last-commit-wins hain because optimistic `@Version`/explicit lock evidence nahi hai. Boolean DB constraint (`NOT NULL`) invalid null state prevent karta hai, stale UI ordering nahi.

# Frontend Changes
`handleTogglePinned`; `pinnedChats`/`unpinnedChats` derived groups; `chatApi` wrapper. Draft chats pin action se excluded hain. `PinOff` import present hai but rendered menu `Pin` icon plus text use karta hai, so `PinOff` unused hai.

# Backend Changes
Controller→transactional service→owner-scoped repository→entity.

# Security Changes
Owner-scoped session lookup prevents cross-user update.

# Testing Changes
No pin tests.

# Technical Topics Covered
PATCH API, JPA field, Flyway default, optimistic UI grouping

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Additive default migration preserves existing rows. Backend list query created-at order retain karti hai; pin-first ordering client-side partition hai, SQL `ORDER BY is_pinned` nahi.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Feature addition.

# Edge Cases
Concurrent toggles, deleted session, stale list ordering.

# Production Considerations
## Current implementation
Additive default migration preserves existing rows.

## Improvement, not currently implemented
Version column/ETag, deterministic DB ordering and tests.

# Interview Topics From This Branch
- Function boundaries: ChatController.updateSessionPinned, ChatServiceImpl.updateSessionPinned, ChatApp.handleTogglePinned
- API contract: Controller mapping PATCH `/chat/sessions/{sessionId}/pin`, effective `/api/v1/chat/sessions/{sessionId}/pin`, `UpdateSessionPinnedRequest`; success 200.
- Persistence: V6 adds `is_pinned BOOLEAN NOT NULL DEFAULT FALSE`.
- Security: Owner-scoped session lookup prevents cross-user update.
- Failure/edge cases: Concurrent toggles, deleted session, stale list ordering.
- Separate exhaustive Q&A: `Interview/feature-pinned-chats.txt`
