# feature/stream-stop-fix — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Create full backend/frontend structure, then add NEW/EDIT/STOP streaming contract and in-place regeneration.

# Branch Purpose
Create full backend/frontend structure, then add NEW/EDIT/STOP streaming contract and in-place regeneration.

# Base Branch / Branch Lineage
Based on root `45e54e3`; PR #1 scaffold then PR #2 stream/edit change.

# Git Commit History
- `b08d5b1` — Add backend and frontend full project structure
- `4fac2b3` — Implement  message & cancel editing with in-place response regeneration and improved chat bubble UI

# Files Changed
- `backend/.gitattributes` — b08d5b1:A
- `backend/.gitignore` — b08d5b1:A
- `backend/.mvn/wrapper/maven-wrapper.properties` — b08d5b1:A
- `backend/Chatbot.postman_collection.json` — b08d5b1:A
- `backend/chatbot-backend-prompt.md` — b08d5b1:A
- `backend/mvnw` — b08d5b1:A
- `backend/mvnw.cmd` — b08d5b1:A
- `backend/pom.xml` — b08d5b1:A
- `backend/src/main/java/com/chatbot/ChatbotApplication.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/client/OllamaClient.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/config/CorsConfig.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/config/PasswordConfig.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/config/WebClientConfig.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java` — b08d5b1:A, 4fac2b3:M
- `backend/src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java` — b08d5b1:A, 4fac2b3:M
- `backend/src/main/java/com/chatbot/constant/AppConstants.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/constant/ResponseCode.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/constant/StreamConstants.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/controller/AuthController.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/controller/ChatController.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java` — b08d5b1:A, 4fac2b3:M
- `backend/src/main/java/com/chatbot/dto/AuthResponse.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/dto/ChatRequest.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/dto/ChatResponse.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java` — 4fac2b3:A
- `backend/src/main/java/com/chatbot/dto/ErrorResponse.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/dto/LoginRequest.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/dto/PriorMessageDto.java` — 4fac2b3:A
- `backend/src/main/java/com/chatbot/dto/RegisterRequest.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java` — 4fac2b3:A
- `backend/src/main/java/com/chatbot/exception/GlobalExceptionHandler.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/impl/OllamaStreamingServiceImpl.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/impl/UserServiceImpl.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/model/ChatSession.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/model/Message.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/model/User.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/repository/ChatSessionRepository.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/repository/MessageRepository.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/repository/UserRepository.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/security/JwtFilter.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/security/JwtUtil.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/service/AIService.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java` — b08d5b1:A, 4fac2b3:M
- `backend/src/main/java/com/chatbot/service/ChatPromptComposer.java` — 4fac2b3:A
- `backend/src/main/java/com/chatbot/service/ChatService.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/service/OllamaStreamingService.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/service/UserService.java` — b08d5b1:A
- `backend/src/main/resources/application-prod.yml` — b08d5b1:A
- `backend/src/main/resources/application.properties` — b08d5b1:A
- `backend/src/main/resources/application.yml` — b08d5b1:A
- `backend/src/test/java/com/chatbot/ChatbotApplicationTests.java` — b08d5b1:A
- `frontend/.gitignore` — b08d5b1:A
- `frontend/README.md` — b08d5b1:A
- `frontend/eslint.config.js` — b08d5b1:A
- `frontend/index.html` — b08d5b1:A
- `frontend/package-lock.json` — b08d5b1:A
- `frontend/package.json` — b08d5b1:A
- `frontend/public/favicon.svg` — b08d5b1:A
- `frontend/public/icons.svg` — b08d5b1:A
- `frontend/src/App.css` — b08d5b1:A, 4fac2b3:M
- `frontend/src/App.jsx` — b08d5b1:A, 4fac2b3:M
- `frontend/src/assets/hero.png` — b08d5b1:A
- `frontend/src/assets/react.svg` — b08d5b1:A
- `frontend/src/assets/vite.svg` — b08d5b1:A
- `frontend/src/components/ChatWindow.jsx` — b08d5b1:A, 4fac2b3:M
- `frontend/src/components/InputBox.jsx` — b08d5b1:A
- `frontend/src/index.css` — b08d5b1:A
- `frontend/src/main.jsx` — b08d5b1:A
- `frontend/src/websocket.js` — b08d5b1:A, 4fac2b3:M
- `frontend/vite.config.js` — b08d5b1:A
- `src/main/java/com/chatbot/config/WebSocketConfig.java` — 4fac2b3:M
- `src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java` — 4fac2b3:M
- `src/main/java/com/chatbot/controller/ChatWebSocketController.java` — 4fac2b3:M
- `src/main/java/com/chatbot/dto/ChatStompPayload.java` — 4fac2b3:A
- `src/main/java/com/chatbot/dto/PriorMessageDto.java` — 4fac2b3:A
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java` — 4fac2b3:A
- `src/main/java/com/chatbot/service/ActiveStreamRegistry.java` — 4fac2b3:M
- `src/main/java/com/chatbot/service/ChatPromptComposer.java` — 4fac2b3:A

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `ChatWebSocketController.handleChat`
- **Function name / class:** `ChatWebSocketController.handleChat`
- **Input:** ChatStompPayload, Principal
- **Output / side effect:** private user-queue chunk/error/done events
- **Internal flow:** Validate principal/type/content/IDs; compose prior history; create/replace active stream; append chunks; persist/finalize.
- **Dependencies, DB/API, errors, security:** OllamaStreamingService, ActiveStreamRegistry, ChatService, SimpMessagingTemplate. Invalid payload sends error+done; stream failures map to events.
- **Before → After / impact:** Before: primitive stream flow. After: NEW/EDIT correlation and typed envelopes.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 2. `ActiveStreamRegistry.replaceAndStart`
- **Function name / class:** `ActiveStreamRegistry.replaceAndStart`
- **Input:** principal, stream IDs, session IDs, subscription supplier
- **Output / side effect:** registered ActiveStream/side effect
- **Internal flow:** Atomically replace prior stream, dispose old subscription, subscribe new and retain Disposable.
- **Dependencies, DB/API, errors, security:** ConcurrentHashMap; later Redis store. Thread-safety matters for STOP race.
- **Before → After / impact:** Before register/swap was weaker; after replacement owns cancellation handle.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 3. `ChatPromptComposer.compose`
- **Function name / class:** `ChatPromptComposer.compose`
- **Input:** ordered PriorMessageDto list, latest content
- **Output / side effect:** single prompt String
- **Internal flow:** Normalize supported roles, append history and latest user turn.
- **Dependencies, DB/API, errors, security:** No DB; called before Ollama streaming.
- **Before → After / impact:** New helper separates prompt construction from controller.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 4. `ChatWindow.commitEdit`
- **Function name / class:** `ChatWindow.commitEdit`
- **Input:** edited UI text
- **Output / side effect:** calls onEditSave
- **Internal flow:** Validate edit text, leave edit mode, ask parent to regenerate paired assistant message.
- **Dependencies, DB/API, errors, security:** React props/state; existing STOMP EDIT path.
- **Before → After / impact:** Added with in-place regeneration UI.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 5. `JwtUtil.generateToken / validateToken`
- **Function name / class:** `JwtUtil.generateToken / validateToken`
- **Input:** username or raw JWT
- **Output / side effect:** signed JWT / boolean
- **Internal flow:** Build claims+expiry or parse signature/expiry.
- **Dependencies, DB/API, errors, security:** JJWT and configured secret; failures return invalid.
- **Before → After / impact:** Part of scaffold, later reused by STOMP interceptor.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`ChatWebSocketController.handleChat`** — Before: primitive stream flow. After: NEW/EDIT correlation and typed envelopes.
- **`ActiveStreamRegistry.replaceAndStart`** — Before register/swap was weaker; after replacement owns cancellation handle.
- **`ChatPromptComposer.compose`** — New helper separates prompt construction from controller.
- **`ChatWindow.commitEdit`** — Added with in-place regeneration UI.
- **`JwtUtil.generateToken / validateToken`** — Part of scaffold, later reused by STOMP interceptor.

## Exact diff-level function and hunk inventory
- `backend/src/main/java/com/chatbot/ChatbotApplication.java`: `main()`
- `backend/src/main/java/com/chatbot/client/OllamaClient.java`: `chat()`, `buildPrompt()`, `parseResponse()`
- `backend/src/main/java/com/chatbot/config/CorsConfig.java`: `corsFilter()`
- `backend/src/main/java/com/chatbot/config/PasswordConfig.java`: `passwordEncoder()`
- `backend/src/main/java/com/chatbot/config/WebClientConfig.java`: `webClientBuilder()`
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java`: `registerStompEndpoints()`, `determineUser()`, `configureMessageBroker()`, `configureMessageConverters()`
- `backend/src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java`: `onSessionDisconnect()`
- `backend/src/main/java/com/chatbot/controller/AuthController.java`: `register()`, `login()`
- `backend/src/main/java/com/chatbot/controller/ChatController.java`: `chat()`, `sessions()`, `messages()`
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java`: `handleChat()`, `handleStop()`, `sendJsonToUser()`
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java`: `getType()`, `setType()`, `getClientStreamId()`, `setClientStreamId()`, `getMessageId()`, `setMessageId()`, `getContent()`, `setContent()`, `getEditTargetMessageId()`, `setEditTargetMessageId()`, `getPriorMessages()`, `setPriorMessages()`
- `backend/src/main/java/com/chatbot/dto/PriorMessageDto.java`: `getRole()`, `setRole()`, `getContent()`, `setContent()`
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java`: `chunk()`, `done()`, `error()`, `getClientStreamId()`, `setClientStreamId()`, `getAssistantMessageId()`, `setAssistantMessageId()`, `getType()`, `setType()`, `getChunk()`, `setChunk()`, `getMessage()`, `setMessage()`
- `backend/src/main/java/com/chatbot/exception/GlobalExceptionHandler.java`: `handleUsernameNotFound()`, `handleBadCredentials()`, `handleRuntime()`, `handleGeneric()`
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java`: `chat()`, `listSessions()`, `getMessages()`, `resolveCurrentUser()`, `resolveSession()`, `buildTitle()`
- `backend/src/main/java/com/chatbot/impl/OllamaStreamingServiceImpl.java`: `streamChat()`, `mapOllamaLineToTextFlux()`
- `backend/src/main/java/com/chatbot/impl/UserServiceImpl.java`: `loadUserByUsername()`, `register()`
- `backend/src/main/java/com/chatbot/security/JwtFilter.java`: `doFilterInternal()`
- `backend/src/main/java/com/chatbot/security/JwtUtil.java`: `generateToken()`, `extractUsername()`, `validateToken()`, `parseClaims()`
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java`: `authenticationManager()`, `securityFilterChain()`
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java`: `register()`, `replaceAndStart()`, `cancel()`, `removeIfMatches()`, `ActiveStream()`
- `backend/src/main/java/com/chatbot/service/ChatPromptComposer.java`: `compose()`
- `frontend/src/App.jsx`: `App()`, `setStreamingState()`, `activeChat()`, `finalizeStream()`, `handleChunk()`, `handleSend()`, `stopResponse()`, `createNewChat()`, `deleteChat()`, `renameChat()`, `appendAssistantChunk()`, `handleStreamBody()`, `snapshot()`, `handleEditSave()`, `chatSnapshot()`, `userIdx()`
- `frontend/src/components/ChatWindow.jsx`: `ChatWindow()`, `UserBubble()`, `AssistantBubble()`, `onScroll()`, `beginEdit()`, `cancelEdit()`, `commitEdit()`
- `frontend/src/components/InputBox.jsx`: `InputBox()`, `handleSubmit()`, `handleKeyDown()`
- `frontend/src/websocket.js`: `connectWebSocket()`, `sub()`, `sendMessage()`, `sendStopSignal()`, `disconnectWebSocket()`
- `src/main/java/com/chatbot/config/WebSocketConfig.java`: `configureMessageConverters()`
- `src/main/java/com/chatbot/controller/ChatWebSocketController.java`: `handleChat()`, `handleStop()`, `sendJsonToUser()`
- `src/main/java/com/chatbot/dto/ChatStompPayload.java`: `getType()`, `setType()`, `getClientStreamId()`, `setClientStreamId()`, `getMessageId()`, `setMessageId()`, `getContent()`, `setContent()`, `getEditTargetMessageId()`, `setEditTargetMessageId()`, `getPriorMessages()`, `setPriorMessages()`
- `src/main/java/com/chatbot/dto/PriorMessageDto.java`: `getRole()`, `setRole()`, `getContent()`, `setContent()`
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java`: `chunk()`, `done()`, `error()`, `getClientStreamId()`, `setClientStreamId()`, `getAssistantMessageId()`, `setAssistantMessageId()`, `getType()`, `setType()`, `getChunk()`, `setChunk()`, `getMessage()`, `setMessage()`
- `src/main/java/com/chatbot/service/ActiveStreamRegistry.java`: `replaceAndStart()`, `cancel()`, `ActiveStream()`
- `src/main/java/com/chatbot/service/ChatPromptComposer.java`: `compose()`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `backend/src/main/java/com/chatbot/ChatbotApplication.java::main()` — Top-level entry/component function hai jo child state, handlers aur rendering/application bootstrap compose karta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/client/OllamaClient.java::buildPrompt()` — Inputs/configuration ko target DTO/prompt/chunk/bucket object me construct karta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/client/OllamaClient.java::chat()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/client/OllamaClient.java::parseResponse()` — Response payload ko defensive parse karta hai aur malformed/empty response ko controlled path deta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/config/CorsConfig.java::corsFilter()` — Cross-cutting transport/security processing karta hai before controller/message handler execution. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/config/PasswordConfig.java::passwordEncoder()` — Spring configuration factory method hai jo application infrastructure bean banata hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/config/WebClientConfig.java::webClientBuilder()` — Spring configuration factory method hai jo application infrastructure bean banata hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java::configureMessageBroker()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java::configureMessageConverters()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java::determineUser()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java::registerStompEndpoints()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java::onSessionDisconnect()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/controller/AuthController.java::login()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/controller/AuthController.java::register()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/controller/ChatController.java::chat()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/controller/ChatController.java::messages()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/controller/ChatController.java::sessions()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java::handleChat()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1, 4fac2b3`.
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java::handleStop()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1, 4fac2b3`.
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java::sendJsonToUser()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::getClientStreamId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::getContent()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::getEditTargetMessageId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::getMessageId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::getPriorMessages()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::getType()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::setClientStreamId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::setContent()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::setEditTargetMessageId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::setMessageId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::setPriorMessages()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::setType()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/PriorMessageDto.java::getContent()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/PriorMessageDto.java::getRole()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/PriorMessageDto.java::setContent()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/PriorMessageDto.java::setRole()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::chunk()` — `StreamDownstreamEvent` ka `chunk` envelope construct karta hai with correlation fields. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::done()` — `StreamDownstreamEvent` ka `done` envelope construct karta hai with correlation fields. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::error()` — `StreamDownstreamEvent` ka `error` envelope construct karta hai with correlation fields. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::getAssistantMessageId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::getChunk()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::getClientStreamId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::getMessage()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::getType()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::setAssistantMessageId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::setChunk()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::setClientStreamId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::setMessage()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::setType()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `backend/src/main/java/com/chatbot/exception/GlobalExceptionHandler.java::handleBadCredentials()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/exception/GlobalExceptionHandler.java::handleGeneric()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/exception/GlobalExceptionHandler.java::handleRuntime()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/exception/GlobalExceptionHandler.java::handleUsernameNotFound()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::buildTitle()` — Inputs/configuration ko target DTO/prompt/chunk/bucket object me construct karta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::chat()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::getMessages()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::listSessions()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::resolveCurrentUser()` — Identifier/context se owner-scoped ya current resource/state resolve karta hai; missing case caller ko propagate hota hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java::resolveSession()` — Identifier/context se owner-scoped ya current resource/state resolve karta hai; missing case caller ko propagate hota hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/impl/OllamaStreamingServiceImpl.java::mapOllamaLineToTextFlux()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/impl/OllamaStreamingServiceImpl.java::streamChat()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/impl/UserServiceImpl.java::loadUserByUsername()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/impl/UserServiceImpl.java::register()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/security/JwtFilter.java::doFilterInternal()` — Cross-cutting transport/security processing karta hai before controller/message handler execution. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/security/JwtUtil.java::extractUsername()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/security/JwtUtil.java::generateToken()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/security/JwtUtil.java::parseClaims()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/security/JwtUtil.java::validateToken()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java::authenticationManager()` — Spring configuration factory method hai jo application infrastructure bean banata hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java::securityFilterChain()` — Cross-cutting transport/security processing karta hai before controller/message handler execution. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java::ActiveStream()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1, 4fac2b3`.
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java::cancel()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1, 4fac2b3`.
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java::register()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java::removeIfMatches()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java::replaceAndStart()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1, 4fac2b3`.
- `backend/src/main/java/com/chatbot/service/ChatPromptComposer.java::compose()` — Inputs/configuration ko target DTO/prompt/chunk/bucket object me construct karta hai. Git evidence: `4fac2b3`.
- `frontend/src/App.jsx::App()` — Top-level entry/component function hai jo child state, handlers aur rendering/application bootstrap compose karta hai. Git evidence: `b08d5b1`.
- `frontend/src/App.jsx::activeChat()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1, 4fac2b3`.
- `frontend/src/App.jsx::appendAssistantChunk()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `4fac2b3`.
- `frontend/src/App.jsx::chatSnapshot()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `4fac2b3`.
- `frontend/src/App.jsx::createNewChat()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `frontend/src/App.jsx::deleteChat()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `frontend/src/App.jsx::finalizeStream()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `frontend/src/App.jsx::handleChunk()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `frontend/src/App.jsx::handleEditSave()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `4fac2b3`.
- `frontend/src/App.jsx::handleSend()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `frontend/src/App.jsx::handleStreamBody()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `4fac2b3`.
- `frontend/src/App.jsx::renameChat()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `frontend/src/App.jsx::setStreamingState()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `b08d5b1`.
- `frontend/src/App.jsx::snapshot()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `4fac2b3`.
- `frontend/src/App.jsx::stopResponse()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `frontend/src/App.jsx::userIdx()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `4fac2b3`.
- `frontend/src/components/ChatWindow.jsx::AssistantBubble()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `4fac2b3`.
- `frontend/src/components/ChatWindow.jsx::ChatWindow()` — Top-level entry/component function hai jo child state, handlers aur rendering/application bootstrap compose karta hai. Git evidence: `b08d5b1, 4fac2b3`.
- `frontend/src/components/ChatWindow.jsx::UserBubble()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `4fac2b3`.
- `frontend/src/components/ChatWindow.jsx::beginEdit()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `4fac2b3`.
- `frontend/src/components/ChatWindow.jsx::cancelEdit()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `4fac2b3`.
- `frontend/src/components/ChatWindow.jsx::commitEdit()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `4fac2b3`.
- `frontend/src/components/ChatWindow.jsx::onScroll()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `4fac2b3`.
- `frontend/src/components/InputBox.jsx::InputBox()` — Top-level entry/component function hai jo child state, handlers aur rendering/application bootstrap compose karta hai. Git evidence: `b08d5b1`.
- `frontend/src/components/InputBox.jsx::handleKeyDown()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `frontend/src/components/InputBox.jsx::handleSubmit()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `frontend/src/websocket.js::connectWebSocket()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `frontend/src/websocket.js::disconnectWebSocket()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `frontend/src/websocket.js::sendMessage()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1, 4fac2b3`.
- `frontend/src/websocket.js::sendStopSignal()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `b08d5b1`.
- `frontend/src/websocket.js::sub()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `b08d5b1`.
- `src/main/java/com/chatbot/config/WebSocketConfig.java::configureMessageConverters()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/controller/ChatWebSocketController.java::handleChat()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/controller/ChatWebSocketController.java::handleStop()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/controller/ChatWebSocketController.java::sendJsonToUser()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/ChatStompPayload.java::getClientStreamId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/ChatStompPayload.java::getContent()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/ChatStompPayload.java::getEditTargetMessageId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/ChatStompPayload.java::getMessageId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/ChatStompPayload.java::getPriorMessages()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/ChatStompPayload.java::getType()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/ChatStompPayload.java::setClientStreamId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/ChatStompPayload.java::setContent()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/ChatStompPayload.java::setEditTargetMessageId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/ChatStompPayload.java::setMessageId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/ChatStompPayload.java::setPriorMessages()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/ChatStompPayload.java::setType()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/PriorMessageDto.java::getContent()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/PriorMessageDto.java::getRole()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/PriorMessageDto.java::setContent()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/PriorMessageDto.java::setRole()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::chunk()` — `StreamDownstreamEvent` ka `chunk` envelope construct karta hai with correlation fields. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::done()` — `StreamDownstreamEvent` ka `done` envelope construct karta hai with correlation fields. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::error()` — `StreamDownstreamEvent` ka `error` envelope construct karta hai with correlation fields. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::getAssistantMessageId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::getChunk()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::getClientStreamId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::getMessage()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::getType()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::setAssistantMessageId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::setChunk()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::setClientStreamId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::setMessage()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java::setType()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/service/ActiveStreamRegistry.java::ActiveStream()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/service/ActiveStreamRegistry.java::cancel()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/service/ActiveStreamRegistry.java::replaceAndStart()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `4fac2b3`.
- `src/main/java/com/chatbot/service/ChatPromptComposer.java::compose()` — Inputs/configuration ko target DTO/prompt/chunk/bucket object me construct karta hai. Git evidence: `4fac2b3`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `backend/.gitattributes` — b08d5b1:A
- `backend/.gitignore` — b08d5b1:A
- `backend/.mvn/wrapper/maven-wrapper.properties` — b08d5b1:A
- `backend/Chatbot.postman_collection.json` — b08d5b1:A
- `backend/chatbot-backend-prompt.md` — b08d5b1:A
- `backend/mvnw` — b08d5b1:A
- `backend/mvnw.cmd` — b08d5b1:A
- `backend/pom.xml` — b08d5b1:A
- `backend/src/main/java/com/chatbot/ChatbotApplication.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/client/OllamaClient.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/config/CorsConfig.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/config/PasswordConfig.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/config/WebClientConfig.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java` — b08d5b1:A, 4fac2b3:M
- `backend/src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java` — b08d5b1:A, 4fac2b3:M
- `backend/src/main/java/com/chatbot/constant/AppConstants.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/constant/ResponseCode.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/constant/StreamConstants.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/controller/AuthController.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/controller/ChatController.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java` — b08d5b1:A, 4fac2b3:M
- `backend/src/main/java/com/chatbot/dto/AuthResponse.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/dto/ChatRequest.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/dto/ChatResponse.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java` — 4fac2b3:A
- `backend/src/main/java/com/chatbot/dto/ErrorResponse.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/dto/LoginRequest.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/dto/PriorMessageDto.java` — 4fac2b3:A
- `backend/src/main/java/com/chatbot/dto/RegisterRequest.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java` — 4fac2b3:A
- `backend/src/main/java/com/chatbot/exception/GlobalExceptionHandler.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/impl/OllamaStreamingServiceImpl.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/impl/UserServiceImpl.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/model/ChatSession.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/model/Message.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/model/User.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/repository/ChatSessionRepository.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/repository/MessageRepository.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/repository/UserRepository.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/security/JwtFilter.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/security/JwtUtil.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/service/AIService.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java` — b08d5b1:A, 4fac2b3:M
- `backend/src/main/java/com/chatbot/service/ChatPromptComposer.java` — 4fac2b3:A
- `backend/src/main/java/com/chatbot/service/ChatService.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/service/OllamaStreamingService.java` — b08d5b1:A
- `backend/src/main/java/com/chatbot/service/UserService.java` — b08d5b1:A
- `backend/src/main/resources/application-prod.yml` — b08d5b1:A
- `backend/src/main/resources/application.properties` — b08d5b1:A
- `backend/src/main/resources/application.yml` — b08d5b1:A
- `backend/src/test/java/com/chatbot/ChatbotApplicationTests.java` — b08d5b1:A
- `frontend/.gitignore` — b08d5b1:A
- `frontend/README.md` — b08d5b1:A
- `frontend/eslint.config.js` — b08d5b1:A
- `frontend/index.html` — b08d5b1:A
- `frontend/package-lock.json` — b08d5b1:A
- `frontend/package.json` — b08d5b1:A
- `frontend/public/favicon.svg` — b08d5b1:A
- `frontend/public/icons.svg` — b08d5b1:A
- `frontend/src/App.css` — b08d5b1:A, 4fac2b3:M
- `frontend/src/App.jsx` — b08d5b1:A, 4fac2b3:M
- `frontend/src/assets/hero.png` — b08d5b1:A
- `frontend/src/assets/react.svg` — b08d5b1:A
- `frontend/src/assets/vite.svg` — b08d5b1:A
- `frontend/src/components/ChatWindow.jsx` — b08d5b1:A, 4fac2b3:M
- `frontend/src/components/InputBox.jsx` — b08d5b1:A
- `frontend/src/index.css` — b08d5b1:A
- `frontend/src/main.jsx` — b08d5b1:A
- `frontend/src/websocket.js` — b08d5b1:A, 4fac2b3:M
- `frontend/vite.config.js` — b08d5b1:A
- `src/main/java/com/chatbot/config/WebSocketConfig.java` — 4fac2b3:M
- `src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java` — 4fac2b3:M
- `src/main/java/com/chatbot/controller/ChatWebSocketController.java` — 4fac2b3:M
- `src/main/java/com/chatbot/dto/ChatStompPayload.java` — 4fac2b3:A
- `src/main/java/com/chatbot/dto/PriorMessageDto.java` — 4fac2b3:A
- `src/main/java/com/chatbot/dto/StreamDownstreamEvent.java` — 4fac2b3:A
- `src/main/java/com/chatbot/service/ActiveStreamRegistry.java` — 4fac2b3:M
- `src/main/java/com/chatbot/service/ChatPromptComposer.java` — 4fac2b3:A

# APIs Added/Modified
REST auth/chat scaffold (effective REST prefix `/api/v1`); STOMP application destinations `/app/chat`, `/app/chat/stop`, user destination `/user/queue/messages`, handshake endpoint `/api/v1/ws-chat` with context path.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
Initial users/chat_sessions/messages entity model (pre-Flyway).

# Frontend Changes
`App.jsx`, `ChatWindow`, `InputBox`, `websocket.js` implement send/edit/stop UX.

# Backend Changes
Auth/chat/Ollama/JPA scaffold plus streaming controller and registry.

# Security Changes
JWT REST base; stream initially principal-aware; later login branch hardens STOMP CONNECT.

# Testing Changes
Context-load test scaffold only.

# Technical Topics Covered
Spring Boot layering, Reactor cancellation, STOMP DTOs, JWT, JPA, React message editing, correlation IDs

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Client IDs route chunks to exact bubble; registry owns cancellable Reactor Disposable.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Stop/edit contract replaced primitive content messages with typed payload/event envelopes.

# Edge Cases
STOP racing subscription, stale chunks, invalid EDIT IDs, Ollama failures.

# Production Considerations
## Current implementation
Client IDs route chunks to exact bubble; registry owns cancellable Reactor Disposable.

## Improvement, not currently implemented
Contract tests, bounded prompt history and explicit stream state machine.

# Interview Topics From This Branch
- Function boundaries: ChatWebSocketController.handleChat, ActiveStreamRegistry.replaceAndStart, ChatPromptComposer.compose, ChatWindow.commitEdit, JwtUtil.generateToken / validateToken
- API contract: REST auth/chat scaffold (effective REST prefix `/api/v1`); STOMP application destinations `/app/chat`, `/app/chat/stop`, user destination `/user/queue/messages`, handshake endpoint `/api/v1/ws-chat` with context path.
- Persistence: Initial users/chat_sessions/messages entity model (pre-Flyway).
- Security: JWT REST base; stream initially principal-aware; later login branch hardens STOMP CONNECT.
- Failure/edge cases: STOP racing subscription, stale chunks, invalid EDIT IDs, Ollama failures.
- Separate exhaustive Q&A: `Interview/feature-stream-stop-fix.txt`
