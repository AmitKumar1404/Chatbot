# feature/login-page — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Add routed login/register UI and STOMP JWT authentication; later profile/logout and forgot/reset password email flow.

# Branch Purpose
Add routed login/register UI and STOMP JWT authentication; later profile/logout and forgot/reset password email flow.

# Base Branch / Branch Lineage
PR #5/#6; merged main into branch; PR #30 adds reset flow and CI profile.

# Git Commit History
- `f015aab` — add authentication system with modern login/register UI
- `f777e27` — move user profile to sidebar with avatar dropdown logout
- `94478a2` — chore: ignore environment files
- `0e6f183` — feat(auth): implement forgot and reset password functionality
- `645a74d` — test: run Spring Boot tests with local profile in CI

# Files Changed
- `.DS_Store` — f015aab:A
- `.github/workflows/ci.yml` — 645a74d:M
- `.gitignore` — 94478a2:M, 0e6f183:M
- `Chatbot.postman_collection.json` — f015aab:D
- `README.md` — 0e6f183:M
- `backend/.DS_Store` — f015aab:A
- `backend/.env.staging.example` — 0e6f183:A
- `backend/.gitignore` — 0e6f183:M
- `backend/Chatbot.postman_collection.json` — 0e6f183:M
- `backend/pom.xml` — 0e6f183:M
- `backend/src/.DS_Store` — f015aab:A
- `backend/src/main/.DS_Store` — f015aab:A
- `backend/src/main/java/.DS_Store` — f015aab:A
- `backend/src/main/java/com/.DS_Store` — f015aab:A
- `backend/src/main/java/com/chatbot/.DS_Store` — f015aab:A
- `backend/src/main/java/com/chatbot/config/StompJwtChannelInterceptor.java` — f015aab:A
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java` — f015aab:M
- `backend/src/main/java/com/chatbot/constant/AppConstants.java` — 0e6f183:M
- `backend/src/main/java/com/chatbot/controller/AuthController.java` — f015aab:M, 0e6f183:M
- `backend/src/main/java/com/chatbot/dto/ForgotPasswordRequest.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/dto/MessageResponse.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/dto/RegisterRequest.java` — 0e6f183:M
- `backend/src/main/java/com/chatbot/dto/ResetPasswordRequest.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/impl/MailServiceImpl.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/impl/PasswordResetServiceImpl.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/impl/UserServiceImpl.java` — 0e6f183:M
- `backend/src/main/java/com/chatbot/model/PasswordResetToken.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/model/User.java` — 0e6f183:M
- `backend/src/main/java/com/chatbot/repository/PasswordResetTokenRepository.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/repository/RefreshTokenRevocationRepository.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/security/JwtFilter.java` — f015aab:M
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java` — f015aab:M, 0e6f183:M
- `backend/src/main/java/com/chatbot/service/MailService.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/service/PasswordResetService.java` — 0e6f183:A
- `backend/src/main/resources/application-local.yml` — 0e6f183:A
- `backend/src/main/resources/application-prod.yml` — 0e6f183:M
- `backend/src/main/resources/application.yml` — 0e6f183:M
- `backend/src/main/resources/db/migration/V4__add_email_to_users.sql` — 0e6f183:A
- `backend/src/main/resources/db/migration/V5__create_password_reset_tokens_table.sql` — 0e6f183:A
- `backend/src/test/java/com/chatbot/ChatbotApplicationTests.java` — 645a74d:M
- `chatbot-backend-prompt.md` — f015aab:D
- `frontend/.gitignore` — 0e6f183:M
- `frontend/package-lock.json` — f015aab:M
- `frontend/package.json` — f015aab:M, 0e6f183:M
- `frontend/src/App.css` — f015aab:M, f777e27:M
- `frontend/src/App.jsx` — f015aab:M, 0e6f183:M
- `frontend/src/ChatApp.jsx` — f015aab:A, f777e27:M
- `frontend/src/apiConfig.js` — f015aab:A, 0e6f183:M
- `frontend/src/context/AuthContext.jsx` — f015aab:A
- `frontend/src/main.jsx` — f015aab:M
- `frontend/src/pages/ForgotPasswordPage.jsx` — 0e6f183:A
- `frontend/src/pages/LoginPage.jsx` — f015aab:A, 0e6f183:M
- `frontend/src/pages/RegisterPage.jsx` — f015aab:A, 0e6f183:M
- `frontend/src/pages/ResetPasswordPage.jsx` — 0e6f183:A
- `frontend/src/websocket.js` — f015aab:M
- `mvnw` — f015aab:D
- `mvnw.cmd` — f015aab:D
- `pom.xml` — f015aab:D
- `src/main/java/com/chatbot/ChatbotApplication.java` — f015aab:D
- `src/main/java/com/chatbot/client/OllamaClient.java` — f015aab:D
- `src/main/java/com/chatbot/config/CorsConfig.java` — f015aab:D
- `src/main/java/com/chatbot/config/PasswordConfig.java` — f015aab:D
- `src/main/java/com/chatbot/config/WebClientConfig.java` — f015aab:D
- `src/main/java/com/chatbot/config/WebSocketConfig.java` — f015aab:D
- `src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java` — f015aab:D
- `src/main/java/com/chatbot/constant/AppConstants.java` — f015aab:D
- `src/main/java/com/chatbot/constant/ResponseCode.java` — f015aab:D
- `src/main/java/com/chatbot/constant/StreamConstants.java` — f015aab:D
- `src/main/java/com/chatbot/controller/AuthController.java` — f015aab:D
- `src/main/java/com/chatbot/controller/ChatController.java` — f015aab:D
- `src/main/java/com/chatbot/controller/ChatWebSocketController.java` — f015aab:D
- `src/main/java/com/chatbot/dto/AuthResponse.java` — f015aab:D
- `src/main/java/com/chatbot/dto/ChatRequest.java` — f015aab:D
- `src/main/java/com/chatbot/dto/ChatResponse.java` — f015aab:D
- `src/main/java/com/chatbot/dto/ChatStompPayload.java` — f015aab:D
- `src/main/java/com/chatbot/dto/ErrorResponse.java` — f015aab:D
- `src/main/java/com/chatbot/dto/LoginRequest.java` — f015aab:D
- `src/main/java/com/chatbot/dto/PriorMessageDto.java` — f015aab:D
- `src/main/java/com/chatbot/dto/RegisterRequest.java` — f015aab:D
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java` — f015aab:D
- `src/main/java/com/chatbot/exception/GlobalExceptionHandler.java` — f015aab:D
- `src/main/java/com/chatbot/impl/ChatServiceImpl.java` — f015aab:D
- `src/main/java/com/chatbot/impl/OllamaStreamingServiceImpl.java` — f015aab:D
- `src/main/java/com/chatbot/impl/UserServiceImpl.java` — f015aab:D
- `src/main/java/com/chatbot/model/ChatSession.java` — f015aab:D
- `src/main/java/com/chatbot/model/Message.java` — f015aab:D
- `src/main/java/com/chatbot/model/User.java` — f015aab:D
- `src/main/java/com/chatbot/repository/ChatSessionRepository.java` — f015aab:D
- `src/main/java/com/chatbot/repository/MessageRepository.java` — f015aab:D
- `src/main/java/com/chatbot/repository/UserRepository.java` — f015aab:D
- `src/main/java/com/chatbot/security/JwtFilter.java` — f015aab:D
- `src/main/java/com/chatbot/security/JwtUtil.java` — f015aab:D
- `src/main/java/com/chatbot/security/SecurityConfig.java` — f015aab:D
- `src/main/java/com/chatbot/service/AIService.java` — f015aab:D
- `src/main/java/com/chatbot/service/ActiveStreamRegistry.java` — f015aab:D
- `src/main/java/com/chatbot/service/ChatPromptComposer.java` — f015aab:D
- `src/main/java/com/chatbot/service/ChatService.java` — f015aab:D
- `src/main/java/com/chatbot/service/OllamaStreamingService.java` — f015aab:D
- `src/main/java/com/chatbot/service/UserService.java` — f015aab:D
- `src/main/resources/application-prod.yml` — f015aab:D
- `src/main/resources/application.properties` — f015aab:D
- `src/main/resources/application.yml` — f015aab:D
- `src/test/java/com/chatbot/ChatbotApplicationTests.java` — f015aab:D

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `StompJwtChannelInterceptor.preSend`
- **Function name / class:** `StompJwtChannelInterceptor.preSend`
- **Input:** Spring Message and channel
- **Output / side effect:** same message with authenticated user or exception
- **Internal flow:** On CONNECT read Bearer native header, validate JWT, load user, set authenticated principal; reject protected SEND without user.
- **Dependencies, DB/API, errors, security:** JwtUtil, UserDetailsService; MessageDeliveryException.
- **Before → After / impact:** Before handshake identity was weaker/custom; after STOMP uses same JWT trust root as REST.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 2. `AuthContext.login`
- **Function name / class:** `AuthContext.login`
- **Input:** username,password
- **Output / side effect:** Promise; stores access token and auth state
- **Internal flow:** POST login, parse error/body, validate token field, write sessionStorage, set username.
- **Dependencies, DB/API, errors, security:** fetch `/auth/login`; browser sessionStorage.
- **Before → After / impact:** Auth moved into provider and protected routes.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 3. `AuthContext bootstrap effect`
- **Function name / class:** `AuthContext bootstrap effect`
- **Input:** stored token
- **Output / side effect:** authenticated or cleared state
- **Internal flow:** Read token, call `/auth/me`, clear invalid token, finish bootstrapping; cancellation guard avoids stale setState.
- **Dependencies, DB/API, errors, security:** sessionStorage, fetch.
- **Before → After / impact:** Introduced persisted same-tab login bootstrap.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 4. `PasswordResetServiceImpl.requestReset`
- **Function name / class:** `PasswordResetServiceImpl.requestReset`
- **Input:** username
- **Output / side effect:** generic MessageResponse
- **Internal flow:** Trim lookup; if user/email exist revoke active tokens, persist hashed token, send mail; always generic response.
- **Dependencies, DB/API, errors, security:** UserRepository, token repo, MailService; @Transactional.
- **Before → After / impact:** Added anti-enumeration forgot flow.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 5. `PasswordResetServiceImpl.resetPassword`
- **Function name / class:** `PasswordResetServiceImpl.resetPassword`
- **Input:** ResetPasswordRequest
- **Output / side effect:** success MessageResponse
- **Internal flow:** Hash lookup active token; reject/revoke expired; BCrypt new password; revoke reset token and all refresh tokens.
- **Dependencies, DB/API, errors, security:** Repositories, PasswordEncoder; transaction.
- **Before → After / impact:** Added one-time reset and session invalidation.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 6. `MailServiceImpl.sendPasswordResetEmail`
- **Function name / class:** `MailServiceImpl.sendPasswordResetEmail`
- **Input:** email, raw token
- **Output / side effect:** void
- **Internal flow:** Build configured frontend URL and message; send through JavaMailSender.
- **Dependencies, DB/API, errors, security:** SMTP external dependency.
- **Before → After / impact:** New email boundary; delivery outcome should be monitored.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`StompJwtChannelInterceptor.preSend`** — Before handshake identity was weaker/custom; after STOMP uses same JWT trust root as REST.
- **`AuthContext.login`** — Auth moved into provider and protected routes.
- **`AuthContext bootstrap effect`** — Introduced persisted same-tab login bootstrap.
- **`PasswordResetServiceImpl.requestReset`** — Added anti-enumeration forgot flow.
- **`PasswordResetServiceImpl.resetPassword`** — Added one-time reset and session invalidation.
- **`MailServiceImpl.sendPasswordResetEmail`** — New email boundary; delivery outcome should be monitored.

## Exact diff-level function and hunk inventory
- `backend/src/main/java/com/chatbot/config/StompJwtChannelInterceptor.java`: `preSend()`, `requiresUser()`, `extractBearer()`
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java`: `configureClientInboundChannel()`
- `backend/src/main/java/com/chatbot/controller/AuthController.java`: `me()`, `forgotPassword()`, `resetPassword()`
- `backend/src/main/java/com/chatbot/impl/MailServiceImpl.java`: `sendPasswordResetEmail()`
- `backend/src/main/java/com/chatbot/impl/PasswordResetServiceImpl.java`: `requestReset()`, `resetPassword()`, `persistResetToken()`, `hashToken()`
- `backend/src/main/java/com/chatbot/security/JwtFilter.java`: `isJwtOptionalHttpPath()`
- `frontend/src/App.jsx`: `App()`
- `frontend/src/ChatApp.jsx`: `ChatApp()`, `setStreamingState()`, `activeChat()`, `finalizeStream()`, `appendAssistantChunk()`, `handleStreamBody()`, `handleSend()`, `snapshot()`, `handleEditSave()`, `chatSnapshot()`, `stopResponse()`, `createNewChat()`, `deleteChat()`, `renameChat()`
- `frontend/src/apiConfig.js`: `wsChatUrl()`
- `frontend/src/context/AuthContext.jsx`: `parseJsonSafe()`, `AuthProvider()`, `login()`, `register()`, `logout()`, `useAuth()`
- `frontend/src/main.jsx`: changed context `createRoot(document.getElementById('root')).render(`
- `frontend/src/pages/ForgotPasswordPage.jsx`: `parseJsonSafe()`, `ForgotPasswordPage()`, `handleSubmit()`
- `frontend/src/pages/LoginPage.jsx`: `LoginPage()`, `handleSubmit()`
- `frontend/src/pages/RegisterPage.jsx`: `RegisterPage()`, `handleSubmit()`, `parseJsonSafe()`
- `frontend/src/pages/ResetPasswordPage.jsx`: `parseJsonSafe()`, `ResetPasswordPage()`, `handleSubmit()`
- `frontend/src/websocket.js`: `connectWebSocket()`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `backend/src/main/java/com/chatbot/config/StompJwtChannelInterceptor.java::extractBearer()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f015aab`.
- `backend/src/main/java/com/chatbot/config/StompJwtChannelInterceptor.java::preSend()` — Cross-cutting transport/security processing karta hai before controller/message handler execution. Git evidence: `f015aab`.
- `backend/src/main/java/com/chatbot/config/StompJwtChannelInterceptor.java::requiresUser()` — Boolean predicate/guard hai jo branch invariant ya eligibility check karta hai. Git evidence: `f015aab`.
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java::configureClientInboundChannel()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f015aab`.
- `backend/src/main/java/com/chatbot/controller/AuthController.java::forgotPassword()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `0e6f183`.
- `backend/src/main/java/com/chatbot/controller/AuthController.java::me()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f015aab`.
- `backend/src/main/java/com/chatbot/controller/AuthController.java::resetPassword()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `0e6f183`.
- `backend/src/main/java/com/chatbot/impl/MailServiceImpl.java::sendPasswordResetEmail()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `0e6f183`.
- `backend/src/main/java/com/chatbot/impl/PasswordResetServiceImpl.java::hashToken()` — Boolean predicate/guard hai jo branch invariant ya eligibility check karta hai. Git evidence: `0e6f183`.
- `backend/src/main/java/com/chatbot/impl/PasswordResetServiceImpl.java::persistResetToken()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `0e6f183`.
- `backend/src/main/java/com/chatbot/impl/PasswordResetServiceImpl.java::requestReset()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `0e6f183`.
- `backend/src/main/java/com/chatbot/impl/PasswordResetServiceImpl.java::resetPassword()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `0e6f183`.
- `backend/src/main/java/com/chatbot/security/JwtFilter.java::isJwtOptionalHttpPath()` — Boolean predicate/guard hai jo branch invariant ya eligibility check karta hai. Git evidence: `f015aab`.
- `frontend/src/App.jsx::App()` — Top-level entry/component function hai jo child state, handlers aur rendering/application bootstrap compose karta hai. Git evidence: `f015aab`.
- `frontend/src/ChatApp.jsx::ChatApp()` — Top-level entry/component function hai jo child state, handlers aur rendering/application bootstrap compose karta hai. Git evidence: `f015aab`.
- `frontend/src/ChatApp.jsx::activeChat()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f015aab`.
- `frontend/src/ChatApp.jsx::appendAssistantChunk()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f015aab`.
- `frontend/src/ChatApp.jsx::chatSnapshot()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f015aab`.
- `frontend/src/ChatApp.jsx::createNewChat()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f015aab`.
- `frontend/src/ChatApp.jsx::deleteChat()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f015aab`.
- `frontend/src/ChatApp.jsx::finalizeStream()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f015aab`.
- `frontend/src/ChatApp.jsx::handleEditSave()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f015aab`.
- `frontend/src/ChatApp.jsx::handleSend()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f015aab`.
- `frontend/src/ChatApp.jsx::handleStreamBody()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f015aab`.
- `frontend/src/ChatApp.jsx::renameChat()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f015aab`.
- `frontend/src/ChatApp.jsx::setStreamingState()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `f015aab`.
- `frontend/src/ChatApp.jsx::snapshot()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f015aab`.
- `frontend/src/ChatApp.jsx::stopResponse()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f015aab`.
- `frontend/src/apiConfig.js::wsChatUrl()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f015aab`.
- `frontend/src/context/AuthContext.jsx::AuthProvider()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f015aab`.
- `frontend/src/context/AuthContext.jsx::login()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f015aab`.
- `frontend/src/context/AuthContext.jsx::logout()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f015aab`.
- `frontend/src/context/AuthContext.jsx::parseJsonSafe()` — Response payload ko defensive parse karta hai aur malformed/empty response ko controlled path deta hai. Git evidence: `f015aab`.
- `frontend/src/context/AuthContext.jsx::register()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f015aab`.
- `frontend/src/context/AuthContext.jsx::useAuth()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `f015aab`.
- `frontend/src/pages/ForgotPasswordPage.jsx::ForgotPasswordPage()` — Top-level entry/component function hai jo child state, handlers aur rendering/application bootstrap compose karta hai. Git evidence: `0e6f183`.
- `frontend/src/pages/ForgotPasswordPage.jsx::handleSubmit()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `0e6f183`.
- `frontend/src/pages/ForgotPasswordPage.jsx::parseJsonSafe()` — Response payload ko defensive parse karta hai aur malformed/empty response ko controlled path deta hai. Git evidence: `0e6f183`.
- `frontend/src/pages/LoginPage.jsx::LoginPage()` — Top-level entry/component function hai jo child state, handlers aur rendering/application bootstrap compose karta hai. Git evidence: `f015aab`.
- `frontend/src/pages/LoginPage.jsx::handleSubmit()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f015aab`.
- `frontend/src/pages/RegisterPage.jsx::RegisterPage()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f015aab`.
- `frontend/src/pages/RegisterPage.jsx::handleSubmit()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f015aab`.
- `frontend/src/pages/RegisterPage.jsx::parseJsonSafe()` — Response payload ko defensive parse karta hai aur malformed/empty response ko controlled path deta hai. Git evidence: `0e6f183`.
- `frontend/src/pages/ResetPasswordPage.jsx::ResetPasswordPage()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `0e6f183`.
- `frontend/src/pages/ResetPasswordPage.jsx::handleSubmit()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `0e6f183`.
- `frontend/src/pages/ResetPasswordPage.jsx::parseJsonSafe()` — Response payload ko defensive parse karta hai aur malformed/empty response ko controlled path deta hai. Git evidence: `0e6f183`.
- `frontend/src/websocket.js::connectWebSocket()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `f015aab`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `.DS_Store` — f015aab:A
- `.github/workflows/ci.yml` — 645a74d:M
- `.gitignore` — 94478a2:M, 0e6f183:M
- `Chatbot.postman_collection.json` — f015aab:D
- `README.md` — 0e6f183:M
- `backend/.DS_Store` — f015aab:A
- `backend/.env.staging.example` — 0e6f183:A
- `backend/.gitignore` — 0e6f183:M
- `backend/Chatbot.postman_collection.json` — 0e6f183:M
- `backend/pom.xml` — 0e6f183:M
- `backend/src/.DS_Store` — f015aab:A
- `backend/src/main/.DS_Store` — f015aab:A
- `backend/src/main/java/.DS_Store` — f015aab:A
- `backend/src/main/java/com/.DS_Store` — f015aab:A
- `backend/src/main/java/com/chatbot/.DS_Store` — f015aab:A
- `backend/src/main/java/com/chatbot/config/StompJwtChannelInterceptor.java` — f015aab:A
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java` — f015aab:M
- `backend/src/main/java/com/chatbot/constant/AppConstants.java` — 0e6f183:M
- `backend/src/main/java/com/chatbot/controller/AuthController.java` — f015aab:M, 0e6f183:M
- `backend/src/main/java/com/chatbot/dto/ForgotPasswordRequest.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/dto/MessageResponse.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/dto/RegisterRequest.java` — 0e6f183:M
- `backend/src/main/java/com/chatbot/dto/ResetPasswordRequest.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/impl/MailServiceImpl.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/impl/PasswordResetServiceImpl.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/impl/UserServiceImpl.java` — 0e6f183:M
- `backend/src/main/java/com/chatbot/model/PasswordResetToken.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/model/User.java` — 0e6f183:M
- `backend/src/main/java/com/chatbot/repository/PasswordResetTokenRepository.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/repository/RefreshTokenRevocationRepository.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/security/JwtFilter.java` — f015aab:M
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java` — f015aab:M, 0e6f183:M
- `backend/src/main/java/com/chatbot/service/MailService.java` — 0e6f183:A
- `backend/src/main/java/com/chatbot/service/PasswordResetService.java` — 0e6f183:A
- `backend/src/main/resources/application-local.yml` — 0e6f183:A
- `backend/src/main/resources/application-prod.yml` — 0e6f183:M
- `backend/src/main/resources/application.yml` — 0e6f183:M
- `backend/src/main/resources/db/migration/V4__add_email_to_users.sql` — 0e6f183:A
- `backend/src/main/resources/db/migration/V5__create_password_reset_tokens_table.sql` — 0e6f183:A
- `backend/src/test/java/com/chatbot/ChatbotApplicationTests.java` — 645a74d:M
- `chatbot-backend-prompt.md` — f015aab:D
- `frontend/.gitignore` — 0e6f183:M
- `frontend/package-lock.json` — f015aab:M
- `frontend/package.json` — f015aab:M, 0e6f183:M
- `frontend/src/App.css` — f015aab:M, f777e27:M
- `frontend/src/App.jsx` — f015aab:M, 0e6f183:M
- `frontend/src/ChatApp.jsx` — f015aab:A, f777e27:M
- `frontend/src/apiConfig.js` — f015aab:A, 0e6f183:M
- `frontend/src/context/AuthContext.jsx` — f015aab:A
- `frontend/src/main.jsx` — f015aab:M
- `frontend/src/pages/ForgotPasswordPage.jsx` — 0e6f183:A
- `frontend/src/pages/LoginPage.jsx` — f015aab:A, 0e6f183:M
- `frontend/src/pages/RegisterPage.jsx` — f015aab:A, 0e6f183:M
- `frontend/src/pages/ResetPasswordPage.jsx` — 0e6f183:A
- `frontend/src/websocket.js` — f015aab:M
- `mvnw` — f015aab:D
- `mvnw.cmd` — f015aab:D
- `pom.xml` — f015aab:D
- `src/main/java/com/chatbot/ChatbotApplication.java` — f015aab:D
- `src/main/java/com/chatbot/client/OllamaClient.java` — f015aab:D
- `src/main/java/com/chatbot/config/CorsConfig.java` — f015aab:D
- `src/main/java/com/chatbot/config/PasswordConfig.java` — f015aab:D
- `src/main/java/com/chatbot/config/WebClientConfig.java` — f015aab:D
- `src/main/java/com/chatbot/config/WebSocketConfig.java` — f015aab:D
- `src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java` — f015aab:D
- `src/main/java/com/chatbot/constant/AppConstants.java` — f015aab:D
- `src/main/java/com/chatbot/constant/ResponseCode.java` — f015aab:D
- `src/main/java/com/chatbot/constant/StreamConstants.java` — f015aab:D
- `src/main/java/com/chatbot/controller/AuthController.java` — f015aab:D
- `src/main/java/com/chatbot/controller/ChatController.java` — f015aab:D
- `src/main/java/com/chatbot/controller/ChatWebSocketController.java` — f015aab:D
- `src/main/java/com/chatbot/dto/AuthResponse.java` — f015aab:D
- `src/main/java/com/chatbot/dto/ChatRequest.java` — f015aab:D
- `src/main/java/com/chatbot/dto/ChatResponse.java` — f015aab:D
- `src/main/java/com/chatbot/dto/ChatStompPayload.java` — f015aab:D
- `src/main/java/com/chatbot/dto/ErrorResponse.java` — f015aab:D
- `src/main/java/com/chatbot/dto/LoginRequest.java` — f015aab:D
- `src/main/java/com/chatbot/dto/PriorMessageDto.java` — f015aab:D
- `src/main/java/com/chatbot/dto/RegisterRequest.java` — f015aab:D
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java` — f015aab:D
- `src/main/java/com/chatbot/exception/GlobalExceptionHandler.java` — f015aab:D
- `src/main/java/com/chatbot/impl/ChatServiceImpl.java` — f015aab:D
- `src/main/java/com/chatbot/impl/OllamaStreamingServiceImpl.java` — f015aab:D
- `src/main/java/com/chatbot/impl/UserServiceImpl.java` — f015aab:D
- `src/main/java/com/chatbot/model/ChatSession.java` — f015aab:D
- `src/main/java/com/chatbot/model/Message.java` — f015aab:D
- `src/main/java/com/chatbot/model/User.java` — f015aab:D
- `src/main/java/com/chatbot/repository/ChatSessionRepository.java` — f015aab:D
- `src/main/java/com/chatbot/repository/MessageRepository.java` — f015aab:D
- `src/main/java/com/chatbot/repository/UserRepository.java` — f015aab:D
- `src/main/java/com/chatbot/security/JwtFilter.java` — f015aab:D
- `src/main/java/com/chatbot/security/JwtUtil.java` — f015aab:D
- `src/main/java/com/chatbot/security/SecurityConfig.java` — f015aab:D
- `src/main/java/com/chatbot/service/AIService.java` — f015aab:D
- `src/main/java/com/chatbot/service/ActiveStreamRegistry.java` — f015aab:D
- `src/main/java/com/chatbot/service/ChatPromptComposer.java` — f015aab:D
- `src/main/java/com/chatbot/service/ChatService.java` — f015aab:D
- `src/main/java/com/chatbot/service/OllamaStreamingService.java` — f015aab:D
- `src/main/java/com/chatbot/service/UserService.java` — f015aab:D
- `src/main/resources/application-prod.yml` — f015aab:D
- `src/main/resources/application.properties` — f015aab:D
- `src/main/resources/application.yml` — f015aab:D
- `src/test/java/com/chatbot/ChatbotApplicationTests.java` — f015aab:D

# APIs Added/Modified
Controller mappings POST `/auth/register`, `/auth/login`, `/auth/forgot-password`, `/auth/reset-password` and GET `/auth/me`; external paths context prefix ke saath `/api/v1/auth/...` hain.

### Auth endpoint lifecycle
- POST `/api/v1/auth/register`: `@Valid RegisterRequest` (`username/password @NotBlank`, optional `email @Email`) → `UserService.register()` → BCrypt password + user save → 201 `AuthResponse`. Duplicate username/email DB/service error follows global mapping.
- POST `/api/v1/auth/login`: validated credentials → `AuthenticationManager.authenticate()` → access JWT + refresh-token creation → 200 `AuthResponse`. `BadCredentialsException` maps 401 with generic invalid-credentials message.
- GET `/api/v1/auth/me`: JWT filter establishes `Authentication`; unauthenticated returns 401, authenticated returns 200 username-only `AuthResponse`. `AuthContext` uses it during browser bootstrap and clears invalid token.
- POST `/api/v1/auth/forgot-password`: username required; service intentionally returns generic 200 `MessageResponse` to reduce account enumeration, while known user path creates hashed reset token and sends mail.
- POST `/api/v1/auth/reset-password`: token nonblank and new password minimum 6; service hashes token for lookup, checks expiry/revocation, BCrypt-updates password, revokes reset and refresh tokens, returns 200. Invalid domain cases reach RuntimeException→400 in current handler.
- REST bearer JWT passes `JwtFilter`; STOMP handshake is permitted but `StompJwtChannelInterceptor.preSend()` authenticates CONNECT headers. Raw reset/access/refresh values must not be copied into docs.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
- `V4__add_email_to_users.sql` nullable `users.email VARCHAR(255)` add karta hai and PostgreSQL partial unique index `idx_users_email ... WHERE email IS NOT NULL` banata hai. Existing users migrate ho sakte hain because column initially nullable; non-null duplicate emails DB reject karta hai while multiple NULL rows allowed hain.
- `V5__create_password_reset_tokens_table.sql` creates `password_reset_tokens` with BIGSERIAL PK, user FK `ON DELETE CASCADE`, unique 64-char SHA-256 `token_hash`, `TIMESTAMPTZ expires_at/created_at`, and `revoked DEFAULT FALSE`; user/hash indexes lookup support karte hain.
- Reset service token validation ke baad password BCrypt se replace karta hai and reset plus refresh tokens revoke karta hai. Transaction DB changes ko group karta hai; email delivery external side effect hai aur DB rollback se unsend nahi hota.
- Concurrent same-token reset ke liye explicit row lock evidence nahi hai; one-time intent exists, but strict replay serialization production improvement hai.

# Frontend Changes
AuthContext/pages/routes/profile menu and reset forms.

# Backend Changes
Interceptor, auth endpoints, mail/reset services and token repository.

# Security Changes
Bearer JWT for REST/STOMP; generic forgot response; BCrypt reset; refresh tokens revoked after reset.

# Testing Changes
Context test switched to `local` profile; no auth behavior tests.

# Technical Topics Covered
Spring Security, JWT, BCrypt, STOMP CONNECT auth, React Router, sessionStorage, SMTP, one-time hashed tokens

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Handshake remains permit-all but STOMP CONNECT authenticates; raw reset token is emailed while only SHA-256 hash persists.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Unauthenticated socket and password recovery gaps addressed.

# Edge Cases
Missing email, expired/reused reset token, mail failure, token leakage, account enumeration.

# Production Considerations
## Current implementation
Handshake remains permit-all but STOMP CONNECT authenticates; raw reset token is emailed while only SHA-256 hash persists.

## Improvement, not currently implemented
HttpOnly-cookie option, mail retry/outbox, auth integration tests, restricted origins.

# Interview Topics From This Branch
- Function boundaries: StompJwtChannelInterceptor.preSend, AuthContext.login, AuthContext bootstrap effect, PasswordResetServiceImpl.requestReset, PasswordResetServiceImpl.resetPassword, MailServiceImpl.sendPasswordResetEmail
- API contract: Controller mappings POST `/auth/register`, `/auth/login`, `/auth/forgot-password`, `/auth/reset-password` and GET `/auth/me`; external paths context prefix ke saath `/api/v1/auth/...` hain.
- Persistence: V4 nullable unique email; V5 password_reset_tokens with unique hash, expiry/revocation, user FK indexes.
- Security: Bearer JWT for REST/STOMP; generic forgot response; BCrypt reset; refresh tokens revoked after reset.
- Failure/edge cases: Missing email, expired/reused reset token, mail failure, token leakage, account enumeration.
- Separate exhaustive Q&A: `Interview/feature-login-page.txt`
