# feature/chat-search — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Introduce user-scoped title/message search, UI, DB-level message paging and collapsed sidebar search affordance.

# Branch Purpose
Introduce user-scoped title/message search, UI, DB-level message paging and collapsed sidebar search affordance.

# Base Branch / Branch Lineage
Four PR stages #12/#13/#14/#17.

# Git Commit History
- `9dd63af` — Search api
- `61d3916` — Search UI added on UI
- `45cef8c` — Fix chat message pagination to enforce DB-level paging using PageRequest and prevent full dataset fetch in API responses
- `6e79b84` — Add search icon in collapsed desktop sidebar

# Files Changed
- `backend/Chatbot.postman_collection.json` — 9dd63af:M
- `backend/src/main/java/com/chatbot/constant/AppConstants.java` — 9dd63af:M
- `backend/src/main/java/com/chatbot/controller/ChatController.java` — 45cef8c:M
- `backend/src/main/java/com/chatbot/controller/SearchController.java` — 9dd63af:A
- `backend/src/main/java/com/chatbot/dto/SearchResultDto.java` — 9dd63af:A
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java` — 45cef8c:M
- `backend/src/main/java/com/chatbot/impl/SearchServiceImpl.java` — 9dd63af:A
- `backend/src/main/java/com/chatbot/repository/ChatSessionRepository.java` — 9dd63af:M
- `backend/src/main/java/com/chatbot/repository/MessageRepository.java` — 9dd63af:M, 45cef8c:M
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java` — 9dd63af:M
- `backend/src/main/java/com/chatbot/service/ChatService.java` — 45cef8c:M
- `backend/src/main/java/com/chatbot/service/SearchService.java` — 9dd63af:A
- `frontend/src/App.css` — 61d3916:M, 45cef8c:M, 6e79b84:M
- `frontend/src/ChatApp.jsx` — 61d3916:M, 45cef8c:M, 6e79b84:M
- `frontend/src/chatApi.js` — 9dd63af:M
- `frontend/src/components/SearchBar.jsx` — 61d3916:A, 45cef8c:M

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `SearchServiceImpl.search`
- **Function name / class:** `SearchServiceImpl.search`
- **Input:** query
- **Output / side effect:** List<SearchResultDto>
- **Internal flow:** Return empty for blank; resolve user; query title and message matches; normalize previews; merge ordered results.
- **Dependencies, DB/API, errors, security:** UserRepository, ChatSessionRepository, MessageRepository; read-only transaction.
- **Before → After / impact:** New user-scoped search use case.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 2. `MessageRepository.searchByUserAndContent`
- **Function name / class:** `MessageRepository.searchByUserAndContent`
- **Input:** userId, keyword
- **Output / side effect:** matching Message list
- **Internal flow:** JPQL filters session owner and case-insensitive user/AI text; timestamp descending.
- **Dependencies, DB/API, errors, security:** PostgreSQL/JPA; `%keyword%` can scan.
- **Before → After / impact:** New repository query.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 3. `MessageRepository.findPageByChatSessionId`
- **Function name / class:** `MessageRepository.findPageByChatSessionId`
- **Input:** sessionId, Pageable
- **Output / side effect:** Page<Message>
- **Internal flow:** JPQL + Pageable pushes limit/offset/order to DB.
- **Dependencies, DB/API, errors, security:** Used by ChatService.getMessages(page,size).
- **Before → After / impact:** Before all messages fetched; after DB-level paging.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 4. `SearchBar submit/select flow`
- **Function name / class:** `SearchBar submit/select flow`
- **Input:** query and callbacks
- **Output / side effect:** search results/navigation side effects
- **Internal flow:** Trim/debounce/submit query, render result list, call parent to load target session.
- **Dependencies, DB/API, errors, security:** chatApi.searchChats.
- **Before → After / impact:** UI added after API, later collapsed affordance.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`SearchServiceImpl.search`** — New user-scoped search use case.
- **`MessageRepository.searchByUserAndContent`** — New repository query.
- **`MessageRepository.findPageByChatSessionId`** — Before all messages fetched; after DB-level paging.
- **`SearchBar submit/select flow`** — UI added after API, later collapsed affordance.

## Exact diff-level function and hunk inventory
- `backend/src/main/java/com/chatbot/controller/ChatController.java`: `messages()`
- `backend/src/main/java/com/chatbot/controller/SearchController.java`: `search()`
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java`: `getMessages()`
- `backend/src/main/java/com/chatbot/impl/SearchServiceImpl.java`: `search()`, `resolveCurrentUser()`, `contentPreview()`
- `frontend/src/ChatApp.jsx`: `handleSearch()`, `handleSearchQueryChange()`, `handleSearchResultClick()`, `handleClearSearch()`, `handleCollapsedSidebarSearchClick()`, changed context `export default function ChatApp() {`
- `frontend/src/chatApi.js`: `searchChatsApi()`, changed context `export async function updateChatSessionTitleApi(token, sessionId, title) {`
- `frontend/src/components/SearchBar.jsx`: `SearchBar()`, `handleSubmit()`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `backend/src/main/java/com/chatbot/controller/ChatController.java::messages()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `45cef8c`.
- `backend/src/main/java/com/chatbot/controller/SearchController.java::search()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `9dd63af`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::getMessages()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `45cef8c`.
- `backend/src/main/java/com/chatbot/impl/SearchServiceImpl.java::contentPreview()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `9dd63af`.
- `backend/src/main/java/com/chatbot/impl/SearchServiceImpl.java::resolveCurrentUser()` — Identifier/context se owner-scoped ya current resource/state resolve karta hai; missing case caller ko propagate hota hai. Git evidence: `9dd63af`.
- `backend/src/main/java/com/chatbot/impl/SearchServiceImpl.java::search()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `9dd63af`.
- `frontend/src/ChatApp.jsx::handleClearSearch()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `45cef8c`.
- `frontend/src/ChatApp.jsx::handleCollapsedSidebarSearchClick()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `6e79b84`.
- `frontend/src/ChatApp.jsx::handleSearch()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `61d3916`.
- `frontend/src/ChatApp.jsx::handleSearchQueryChange()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `61d3916`.
- `frontend/src/ChatApp.jsx::handleSearchResultClick()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `61d3916`.
- `frontend/src/chatApi.js::searchChatsApi()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `9dd63af`.
- `frontend/src/components/SearchBar.jsx::SearchBar()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `61d3916`.
- `frontend/src/components/SearchBar.jsx::handleSubmit()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `61d3916`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `backend/Chatbot.postman_collection.json` — 9dd63af:M
- `backend/src/main/java/com/chatbot/constant/AppConstants.java` — 9dd63af:M
- `backend/src/main/java/com/chatbot/controller/ChatController.java` — 45cef8c:M
- `backend/src/main/java/com/chatbot/controller/SearchController.java` — 9dd63af:A
- `backend/src/main/java/com/chatbot/dto/SearchResultDto.java` — 9dd63af:A
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java` — 45cef8c:M
- `backend/src/main/java/com/chatbot/impl/SearchServiceImpl.java` — 9dd63af:A
- `backend/src/main/java/com/chatbot/repository/ChatSessionRepository.java` — 9dd63af:M
- `backend/src/main/java/com/chatbot/repository/MessageRepository.java` — 9dd63af:M, 45cef8c:M
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java` — 9dd63af:M
- `backend/src/main/java/com/chatbot/service/ChatService.java` — 45cef8c:M
- `backend/src/main/java/com/chatbot/service/SearchService.java` — 9dd63af:A
- `frontend/src/App.css` — 61d3916:M, 45cef8c:M, 6e79b84:M
- `frontend/src/ChatApp.jsx` — 61d3916:M, 45cef8c:M, 6e79b84:M
- `frontend/src/chatApi.js` — 9dd63af:M
- `frontend/src/components/SearchBar.jsx` — 61d3916:A, 45cef8c:M

# APIs Added/Modified
Controller mappings GET `/search?q=` and GET `/chat/sessions/{id}/messages?page=&size=`; effective external paths `/api/v1/search` and `/api/v1/chat/...`.

### Endpoint lifecycle
- GET `/api/v1/search?q=...`: optional query enters `SearchController.search()` → `SearchService.search()` → current authenticated user resolution → title/content repository queries → `List<SearchResultDto>`; blank query returns service-defined empty result rather than a write/error path.
- GET `/api/v1/chat/sessions/{sessionId}/messages?page=&size=`: both pagination params absent delegates full history; both present require `page >= 0`, `size > 0` then repository paging. If only one param is present, current controller condition treats request as unpaged because `page == null || size == null`.
- Success is 200. Explicit pagination guard throws `RuntimeException`, mapped to 400. Owner-scoped session lookup prevents another user's messages from being returned.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
Repository title/content queries; paging applied in DB, no new migration.

# Frontend Changes
SearchBar, result selection, collapsed icon and chat loading.

# Backend Changes
SearchController/Service/DTO/repository methods and paged messages.

# Security Changes
Service resolves current user; query only sees owned sessions/messages.

# Testing Changes
No search/pagination tests.

# Technical Topics Covered
Spring Data JPQL, PageRequest, result DTOs, React search/navigation

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Service merges title/message matches; DB paging prevents in-memory full fetch.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Full dataset fetch fixed by `PageRequest`; collapsed search access added.

# Edge Cases
Blank query, wildcard-like input, large result set, deleted source message.

# Production Considerations
## Current implementation
Service merges title/message matches; DB paging prevents in-memory full fetch.

## Improvement, not currently implemented
Full-text index/search, page metadata, debounce/cancel and tests.

# Interview Topics From This Branch
- Function boundaries: SearchServiceImpl.search, MessageRepository.searchByUserAndContent, MessageRepository.findPageByChatSessionId, SearchBar submit/select flow
- API contract: Controller mappings GET `/search?q=` and GET `/chat/sessions/{id}/messages?page=&size=`; effective external paths `/api/v1/search` and `/api/v1/chat/...`.
- Persistence: Repository title/content queries; paging applied in DB, no new migration.
- Security: Service resolves current user; query only sees owned sessions/messages.
- Failure/edge cases: Blank query, wildcard-like input, large result set, deleted source message.
- Separate exhaustive Q&A: `Interview/feature-chat-search.txt`
