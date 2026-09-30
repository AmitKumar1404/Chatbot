# feature/feedback-system — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Implement per-message helpful/not-helpful feedback, reasons, optimistic UI, history restoration and analytics.

# Branch Purpose
Implement per-message helpful/not-helpful feedback, reasons, optimistic UI, history restoration and analytics.

# Base Branch / Branch Lineage
Nine commits merged PR #33; tip-only `4185ea6` adds stricter validation/reason restoration/Postman updates.

# Git Commit History
- `9d27224` — feat: add message feedback persistence layer
- `70a7783` — feat: implement message feedback backend API
- `86ef17c` — feat: add message feedback UI
- `5feee3b` — feat: restore message feedback from chat history
- `abf7f70` — fix(feedback): prevent duplicate feedback submissions
- `ce64dc1` — feat: optimistically update feedback selection with rollback
- `7b5742c` — feat: add feedback analytics API
- `93e5452` — feat(feedback): add backend support for feedback reasons
- `1c3e913` — feat(feedback): add reason selection modal for not helpful feedback
- `4185ea6` — feat(feedback): validate feedback reasons and restore them from chat history & update collection with latest backend APIs

# Files Changed
- `backend/Chatbot.postman_collection.json` — 4185ea6:M
- `backend/src/main/java/com/chatbot/controller/ChatController.java` — 5feee3b:M
- `backend/src/main/java/com/chatbot/controller/FeedbackAnalyticsController.java` — 7b5742c:A
- `backend/src/main/java/com/chatbot/controller/MessageFeedbackController.java` — 70a7783:A
- `backend/src/main/java/com/chatbot/dto/ChatMessageResponse.java` — 5feee3b:A, 4185ea6:M
- `backend/src/main/java/com/chatbot/dto/FeedbackStatsResponse.java` — 7b5742c:A
- `backend/src/main/java/com/chatbot/dto/MessageFeedbackRequest.java` — 70a7783:A, 93e5452:M
- `backend/src/main/java/com/chatbot/dto/MessageFeedbackResponse.java` — 70a7783:A
- `backend/src/main/java/com/chatbot/exception/GlobalExceptionHandler.java` — 4185ea6:M
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java` — 5feee3b:M, 4185ea6:M
- `backend/src/main/java/com/chatbot/impl/FeedbackAnalyticsServiceImpl.java` — 7b5742c:A
- `backend/src/main/java/com/chatbot/impl/MessageFeedbackServiceImpl.java` — 70a7783:A, 93e5452:M, 4185ea6:M
- `backend/src/main/java/com/chatbot/model/FeedbackReason.java` — 93e5452:A
- `backend/src/main/java/com/chatbot/model/MessageFeedback.java` — 9d27224:A, 93e5452:M
- `backend/src/main/java/com/chatbot/model/MessageFeedbackType.java` — 9d27224:A
- `backend/src/main/java/com/chatbot/repository/MessageFeedbackRepository.java` — 9d27224:A, 5feee3b:M, 7b5742c:M
- `backend/src/main/java/com/chatbot/service/ChatService.java` — 5feee3b:M
- `backend/src/main/java/com/chatbot/service/FeedbackAnalyticsService.java` — 7b5742c:A
- `backend/src/main/java/com/chatbot/service/MessageFeedbackService.java` — 70a7783:A
- `backend/src/main/resources/db/migration/V7__create_message_feedbacks_table.sql` — 9d27224:A
- `backend/src/main/resources/db/migration/V8__add_feedback_reason_to_message_feedbacks.sql` — 93e5452:A
- `frontend/package-lock.json` — 5feee3b:M
- `frontend/package.json` — 5feee3b:M
- `frontend/src/ChatApp.jsx` — 5feee3b:M, 4185ea6:M
- `frontend/src/chatApi.js` — 86ef17c:M, 1c3e913:M
- `frontend/src/components/ChatWindow.jsx` — 86ef17c:M, 5feee3b:M, abf7f70:M, ce64dc1:M, 1c3e913:M, 4185ea6:M
- `frontend/src/components/FeedbackButtons.jsx` — 86ef17c:A, 5feee3b:M
- `frontend/src/components/FeedbackReasonModal.jsx` — 1c3e913:A

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `MessageFeedbackServiceImpl.upsertFeedback`
- **Function name / class:** `MessageFeedbackServiceImpl.upsertFeedback`
- **Input:** messageId, MessageFeedbackRequest
- **Output / side effect:** MessageFeedbackResponse
- **Internal flow:** Resolve user/message; verify owner and generationComplete; null reason for HELPFUL; find existing or build; save.
- **Dependencies, DB/API, errors, security:** Message/User/Feedback repositories; @Transactional; DB unique pair.
- **Before → After / impact:** Evolved from type-only upsert to reason-aware rules; strict invalid combinations are tip-only.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 2. `ChatServiceImpl.withFeedback`
- **Function name / class:** `ChatServiceImpl.withFeedback`
- **Input:** messages,userId
- **Output / side effect:** List<ChatMessageResponse>
- **Internal flow:** Batch message IDs; query feedback once; map entity fields plus feedback to DTO.
- **Dependencies, DB/API, errors, security:** MessageFeedbackRepository.
- **Before → After / impact:** History response evolved from entities/messages to feedback-enriched DTOs.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 3. `FeedbackAnalyticsServiceImpl.getStats`
- **Function name / class:** `FeedbackAnalyticsServiceImpl.getStats`
- **Input:** none
- **Output / side effect:** helpful/notHelpful/total DTO
- **Internal flow:** Count repository rows by enum and sum.
- **Dependencies, DB/API, errors, security:** MessageFeedbackRepository; read-only transaction.
- **Before → After / impact:** Adds aggregate API, but no admin role enforcement.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 4. `ChatWindow.submitFeedback`
- **Function name / class:** `ChatWindow.submitFeedback`
- **Input:** message/type/reason
- **Output / side effect:** optimistic state then API/rollback
- **Internal flow:** Capture previous selection; mark pending; call API; restore previous state on error.
- **Dependencies, DB/API, errors, security:** updateMessageFeedbackApi and modal state.
- **Before → After / impact:** Duplicate guard and rollback added in successive commits.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 5. `GlobalExceptionHandler.handleUnreadableMessage`
- **Function name / class:** `GlobalExceptionHandler.handleUnreadableMessage`
- **Input:** HttpMessageNotReadableException
- **Output / side effect:** 400 ErrorResponse
- **Internal flow:** Inspect enum parse cause; emit controlled invalid feedback reason/type message.
- **Dependencies, DB/API, errors, security:** Tip-only 4185ea6.
- **Before → After / impact:** Adds stricter malformed enum handling not present on main.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`MessageFeedbackServiceImpl.upsertFeedback`** — Evolved from type-only upsert to reason-aware rules; strict invalid combinations are tip-only.
- **`ChatServiceImpl.withFeedback`** — History response evolved from entities/messages to feedback-enriched DTOs.
- **`FeedbackAnalyticsServiceImpl.getStats`** — Adds aggregate API, but no admin role enforcement.
- **`ChatWindow.submitFeedback`** — Duplicate guard and rollback added in successive commits.
- **`GlobalExceptionHandler.handleUnreadableMessage`** — Adds stricter malformed enum handling not present on main.

## Exact diff-level function and hunk inventory
- `backend/src/main/java/com/chatbot/controller/ChatController.java`: `messages()`
- `backend/src/main/java/com/chatbot/controller/FeedbackAnalyticsController.java`: `stats()`
- `backend/src/main/java/com/chatbot/controller/MessageFeedbackController.java`: `upsertFeedback()`
- `backend/src/main/java/com/chatbot/exception/GlobalExceptionHandler.java`: `handleUnreadableMessage()`, `isInvalidFeedbackReason()`
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java`: `getMessages()`, `withFeedback()`
- `backend/src/main/java/com/chatbot/impl/FeedbackAnalyticsServiceImpl.java`: `getStats()`
- `backend/src/main/java/com/chatbot/impl/MessageFeedbackServiceImpl.java`: `upsertFeedback()`, `resolveCurrentUser()`
- `frontend/src/ChatApp.jsx`: changed context `function mapDbMessageToUi(m, options = {}) {`
- `frontend/src/chatApi.js`: `updateMessageFeedbackApi()`, changed context `export async function updateChatSessionPinnedApi(token, sessionId, pinned) {`
- `frontend/src/components/ChatWindow.jsx`: `handleFeedback()`, `historyFeedbackByMessageId()`, `getSelectedFeedback()`, `submitFeedback()`, `closeFeedbackReasonModal()`, `handleFeedbackReasonSubmit()`, `historyFeedbackReasonByMessageId()`, changed context `const AssistantBubble = memo(function AssistantBubble({`, changed context `export default function ChatWindow({`
- `frontend/src/components/FeedbackButtons.jsx`: `FeedbackButtons()`
- `frontend/src/components/FeedbackReasonModal.jsx`: `FeedbackReasonModal()`, `onKeyDown()`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `backend/src/main/java/com/chatbot/controller/ChatController.java::messages()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `5feee3b`.
- `backend/src/main/java/com/chatbot/controller/FeedbackAnalyticsController.java::stats()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `7b5742c`.
- `backend/src/main/java/com/chatbot/controller/MessageFeedbackController.java::upsertFeedback()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `70a7783`.
- `backend/src/main/java/com/chatbot/exception/GlobalExceptionHandler.java::handleUnreadableMessage()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `4185ea6`.
- `backend/src/main/java/com/chatbot/exception/GlobalExceptionHandler.java::isInvalidFeedbackReason()` — Boolean predicate/guard hai jo branch invariant ya eligibility check karta hai. Git evidence: `4185ea6`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::getMessages()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `5feee3b`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::withFeedback()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `5feee3b`.
- `backend/src/main/java/com/chatbot/impl/FeedbackAnalyticsServiceImpl.java::getStats()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `7b5742c`.
- `backend/src/main/java/com/chatbot/impl/MessageFeedbackServiceImpl.java::resolveCurrentUser()` — Identifier/context se owner-scoped ya current resource/state resolve karta hai; missing case caller ko propagate hota hai. Git evidence: `70a7783`.
- `backend/src/main/java/com/chatbot/impl/MessageFeedbackServiceImpl.java::upsertFeedback()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `70a7783`.
- `frontend/src/chatApi.js::updateMessageFeedbackApi()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `86ef17c, 1c3e913`.
- `frontend/src/components/ChatWindow.jsx::closeFeedbackReasonModal()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `1c3e913`.
- `frontend/src/components/ChatWindow.jsx::getSelectedFeedback()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `1c3e913`.
- `frontend/src/components/ChatWindow.jsx::handleFeedback()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `86ef17c, 1c3e913`.
- `frontend/src/components/ChatWindow.jsx::handleFeedbackReasonSubmit()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `1c3e913`.
- `frontend/src/components/ChatWindow.jsx::historyFeedbackByMessageId()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `5feee3b`.
- `frontend/src/components/ChatWindow.jsx::historyFeedbackReasonByMessageId()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `4185ea6`.
- `frontend/src/components/ChatWindow.jsx::submitFeedback()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `1c3e913`.
- `frontend/src/components/FeedbackButtons.jsx::FeedbackButtons()` — Top-level entry/component function hai jo child state, handlers aur rendering/application bootstrap compose karta hai. Git evidence: `86ef17c`.
- `frontend/src/components/FeedbackReasonModal.jsx::FeedbackReasonModal()` — Top-level entry/component function hai jo child state, handlers aur rendering/application bootstrap compose karta hai. Git evidence: `1c3e913`.
- `frontend/src/components/FeedbackReasonModal.jsx::onKeyDown()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `1c3e913`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `backend/Chatbot.postman_collection.json` — 4185ea6:M
- `backend/src/main/java/com/chatbot/controller/ChatController.java` — 5feee3b:M
- `backend/src/main/java/com/chatbot/controller/FeedbackAnalyticsController.java` — 7b5742c:A
- `backend/src/main/java/com/chatbot/controller/MessageFeedbackController.java` — 70a7783:A
- `backend/src/main/java/com/chatbot/dto/ChatMessageResponse.java` — 5feee3b:A, 4185ea6:M
- `backend/src/main/java/com/chatbot/dto/FeedbackStatsResponse.java` — 7b5742c:A
- `backend/src/main/java/com/chatbot/dto/MessageFeedbackRequest.java` — 70a7783:A, 93e5452:M
- `backend/src/main/java/com/chatbot/dto/MessageFeedbackResponse.java` — 70a7783:A
- `backend/src/main/java/com/chatbot/exception/GlobalExceptionHandler.java` — 4185ea6:M
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java` — 5feee3b:M, 4185ea6:M
- `backend/src/main/java/com/chatbot/impl/FeedbackAnalyticsServiceImpl.java` — 7b5742c:A
- `backend/src/main/java/com/chatbot/impl/MessageFeedbackServiceImpl.java` — 70a7783:A, 93e5452:M, 4185ea6:M
- `backend/src/main/java/com/chatbot/model/FeedbackReason.java` — 93e5452:A
- `backend/src/main/java/com/chatbot/model/MessageFeedback.java` — 9d27224:A, 93e5452:M
- `backend/src/main/java/com/chatbot/model/MessageFeedbackType.java` — 9d27224:A
- `backend/src/main/java/com/chatbot/repository/MessageFeedbackRepository.java` — 9d27224:A, 5feee3b:M, 7b5742c:M
- `backend/src/main/java/com/chatbot/service/ChatService.java` — 5feee3b:M
- `backend/src/main/java/com/chatbot/service/FeedbackAnalyticsService.java` — 7b5742c:A
- `backend/src/main/java/com/chatbot/service/MessageFeedbackService.java` — 70a7783:A
- `backend/src/main/resources/db/migration/V7__create_message_feedbacks_table.sql` — 9d27224:A
- `backend/src/main/resources/db/migration/V8__add_feedback_reason_to_message_feedbacks.sql` — 93e5452:A
- `frontend/package-lock.json` — 5feee3b:M
- `frontend/package.json` — 5feee3b:M
- `frontend/src/ChatApp.jsx` — 5feee3b:M, 4185ea6:M
- `frontend/src/chatApi.js` — 86ef17c:M, 1c3e913:M
- `frontend/src/components/ChatWindow.jsx` — 86ef17c:M, 5feee3b:M, abf7f70:M, ce64dc1:M, 1c3e913:M, 4185ea6:M
- `frontend/src/components/FeedbackButtons.jsx` — 86ef17c:A, 5feee3b:M
- `frontend/src/components/FeedbackReasonModal.jsx` — 1c3e913:A

# APIs Added/Modified
Controller mapping PUT `/chat/messages/{id}/feedback` (effective `/api/v1/chat/messages/{id}/feedback`); analytics mapping `/api/v1/admin/feedback/stats` becomes external `/api/v1/api/v1/admin/feedback/stats`.

### Endpoint lifecycle: feedback upsert
- Request: `messageId` path + `MessageFeedbackRequest`; `feedbackType` has `@NotNull`, optional `feedbackReason` is enum-typed. Branch-tip `4185ea6` adds malformed `feedbackReason` detection in `handleUnreadableMessage()` and a specific 400 message.
- Flow: `chatApi.submitFeedback()`/modal → `MessageFeedbackController.upsertFeedback()` → transactional `MessageFeedbackServiceImpl.upsertFeedback()` → current user + owned, completed message checks → existing row update or insert → `MessageFeedbackResponse` → optimistic UI confirm/rollback.
- Success: 200. Runtime domain failures map to 400 in current global handler; malformed enum tip patch maps 400; authentication is required.

### Endpoint lifecycle: analytics
- GET controller mapping `/api/v1/admin/feedback/stats`, effective double-prefixed `/api/v1/api/v1/admin/feedback/stats` because servlet context is also `/api/v1`.
- `FeedbackAnalyticsController.stats()` calls aggregation repository methods and returns 200 `FeedbackStatsResponse`.
- Name says `admin`, but SecurityConfig role/authority check evidence absent hai; any authenticated principal reaching it can query aggregate stats. This is a production authorization gap, not an implemented admin boundary.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
- `V7__create_message_feedbacks_table.sql` creates BIGSERIAL `message_feedbacks` with `message_id`, `user_id`, `feedback_type VARCHAR(20)`, created/updated timestamps. Both FKs use `ON DELETE CASCADE`; deleting message/user removes feedback.
- `uk_message_feedbacks_message_user UNIQUE(message_id,user_id)` one feedback per user-message enforce karta hai, including concurrent inserts. `ck_message_feedbacks_type` only `HELPFUL`/`NOT_HELPFUL` permits; `idx_message_feedbacks_user_id` user aggregation supports karta hai.
- `V8__add_feedback_reason_to_message_feedbacks.sql` nullable `feedback_reason VARCHAR(30)` add karta hai. DB-level reason enum/check nahi hai; branch-tip request validation/service enum parsing invalid reason reject karti hai.
- Service read-then-insert/update upsert karta hai. Unique constraint final race invariant hai, but simultaneous first insert me one request constraint violation receive kar sakta hai; atomic PostgreSQL `ON CONFLICT` production alternative hai.
- Analytics count queries DB totals return karte hain; no admin role guard and no additional analytics index evidence hai.

# Frontend Changes
Buttons/modal/history mapping/optimistic state.

# Backend Changes
Entity/repository/service/controllers/DTOs and history mapper.

Repository/API details:
- `MessageFeedbackRepository.existsByMessage_IdAndUser_Id()` define hua but owned implementation me caller evidence nahi mila; upsert `findByMessage_IdAndUser_Id()` use karta hai.
- `MessageFeedbackResponse` stored `feedbackReason` return nahi karta. Main history mapper only `feedbackType` restore karta hai; tip-only `4185ea6` `ChatMessageResponse.feedbackReason` restore karta hai.
- Main through `1c3e913` NOT_HELPFUL reason optional accept karta hai. Tip-only `4185ea6` reason required karta hai and malformed enum ke liye bounded 400 handler add karta hai.

# Security Changes
Message ownership and completion checked; analytics lacks admin RBAC.

# Testing Changes
No main feedback tests.

# Technical Topics Covered
Upsert, DB constraints, enum validation, optimistic rollback, analytics

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
DB unique constraint is final invariant under race.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Duplicate submissions and UI rollback handled; strict reason validation remains tip-only.

# Edge Cases
Concurrent upsert, incomplete message, other user, invalid enum/reason, analytics exposure.

# Production Considerations
## Current implementation
DB unique constraint is final invariant under race.

## Improvement, not currently implemented
Atomic upsert, admin role, validation tests and indexed analytics.

# Interview Topics From This Branch
- Function boundaries: MessageFeedbackServiceImpl.upsertFeedback, ChatServiceImpl.withFeedback, FeedbackAnalyticsServiceImpl.getStats, ChatWindow.submitFeedback, GlobalExceptionHandler.handleUnreadableMessage
- API contract: Controller mapping PUT `/chat/messages/{id}/feedback` (effective `/api/v1/chat/messages/{id}/feedback`); analytics mapping `/api/v1/admin/feedback/stats` becomes external `/api/v1/api/v1/admin/feedback/stats`.
- Persistence: V7 feedback table unique message+user and type CHECK; V8 reason column.
- Security: Message ownership and completion checked; analytics lacks admin RBAC.
- Failure/edge cases: Concurrent upsert, incomplete message, other user, invalid enum/reason, analytics exposure.
- Separate exhaustive Q&A: `Interview/feature-feedback-system.txt`
