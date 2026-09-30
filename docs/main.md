# main — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Integrated repository state through PR #36: authenticated persistent chatbot, STOMP streaming/recovery, search, Redis state, hardening, feedback, RAG and voice.

# Branch Purpose
Integrated repository state through PR #36: authenticated persistent chatbot, STOMP streaming/recovery, search, Redis state, hardening, feedback, RAG and voice.

# Base Branch / Branch Lineage
Root `45e54e3`; first-parent integration branch; HEAD `a62003a`.

# Git Commit History
- `45e54e3` through `a62003a`: see cross-branch evolution; main integrates PR #1–#36.

# Files Changed
- `.github/workflows/ci.yml`
- `backend/.DS_Store`
- `backend/.env.staging.example`
- `backend/.gitattributes`
- `backend/.gitignore`
- `backend/.java-version`
- `backend/.mvn/wrapper/maven-wrapper.properties`
- `backend/Chatbot.postman_collection.json`
- `backend/chatbot-backend-prompt.md`
- `backend/mvnw`
- `backend/mvnw.cmd`
- `backend/pom.xml`
- `backend/src/.DS_Store`
- `backend/src/main/.DS_Store`
- `backend/src/main/java/.DS_Store`
- `backend/src/main/java/com/.DS_Store`
- `backend/src/main/java/com/chatbot/.DS_Store`
- `backend/src/main/java/com/chatbot/ChatbotApplication.java`
- `backend/src/main/java/com/chatbot/client/OllamaClient.java`
- `backend/src/main/java/com/chatbot/config/CorsConfig.java`
- `backend/src/main/java/com/chatbot/config/OpenApiConfig.java`
- `backend/src/main/java/com/chatbot/config/PasswordConfig.java`
- `backend/src/main/java/com/chatbot/config/RestTemplateConfig.java`
- `backend/src/main/java/com/chatbot/config/StompJwtChannelInterceptor.java`
- `backend/src/main/java/com/chatbot/config/WebClientConfig.java`
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java`
- `backend/src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java`
- `backend/src/main/java/com/chatbot/constant/AppConstants.java`
- `backend/src/main/java/com/chatbot/constant/ResponseCode.java`
- `backend/src/main/java/com/chatbot/constant/StreamConstants.java`
- `backend/src/main/java/com/chatbot/controller/AuthController.java`
- `backend/src/main/java/com/chatbot/controller/ChatController.java`
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java`
- `backend/src/main/java/com/chatbot/controller/DocumentController.java`
- `backend/src/main/java/com/chatbot/controller/FeedbackAnalyticsController.java`
- `backend/src/main/java/com/chatbot/controller/MessageFeedbackController.java`
- `backend/src/main/java/com/chatbot/controller/SearchController.java`
- `backend/src/main/java/com/chatbot/controller/SimilarityTestController.java`
- `backend/src/main/java/com/chatbot/dto/ActiveStreamStatusDto.java`
- `backend/src/main/java/com/chatbot/dto/AuthResponse.java`
- `backend/src/main/java/com/chatbot/dto/ChatMessageResponse.java`
- `backend/src/main/java/com/chatbot/dto/ChatRequest.java`
- `backend/src/main/java/com/chatbot/dto/ChatResponse.java`
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java`
- `backend/src/main/java/com/chatbot/dto/DocumentUploadResponse.java`
- `backend/src/main/java/com/chatbot/dto/ErrorResponse.java`
- `backend/src/main/java/com/chatbot/dto/FeedbackStatsResponse.java`
- `backend/src/main/java/com/chatbot/dto/ForgotPasswordRequest.java`
- `backend/src/main/java/com/chatbot/dto/LoginRequest.java`
- `backend/src/main/java/com/chatbot/dto/MessageFeedbackRequest.java`
- `backend/src/main/java/com/chatbot/dto/MessageFeedbackResponse.java`
- `backend/src/main/java/com/chatbot/dto/MessageResponse.java`
- `backend/src/main/java/com/chatbot/dto/PriorMessageDto.java`
- `backend/src/main/java/com/chatbot/dto/RefreshTokenRequest.java`
- `backend/src/main/java/com/chatbot/dto/RegisterRequest.java`
- `backend/src/main/java/com/chatbot/dto/ResetPasswordRequest.java`
- `backend/src/main/java/com/chatbot/dto/SearchResultDto.java`
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java`
- `backend/src/main/java/com/chatbot/dto/UpdateSessionPinnedRequest.java`
- `backend/src/main/java/com/chatbot/dto/UpdateSessionTitleRequest.java`
- `backend/src/main/java/com/chatbot/dto/embedding/EmbeddingRequest.java`
- `backend/src/main/java/com/chatbot/dto/embedding/EmbeddingResponse.java`
- `backend/src/main/java/com/chatbot/dto/similarity/SimilarityResult.java`
- `backend/src/main/java/com/chatbot/exception/GlobalExceptionHandler.java`
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/DocumentServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/FeedbackAnalyticsServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/MailServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/MessageFeedbackServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/OllamaStreamingServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/PasswordResetServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/RefreshTokenServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/SearchServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/UserServiceImpl.java`
- `backend/src/main/java/com/chatbot/model/ChatSession.java`
- `backend/src/main/java/com/chatbot/model/Document.java`
- `backend/src/main/java/com/chatbot/model/DocumentChunk.java`
- `backend/src/main/java/com/chatbot/model/DocumentChunkEmbedding.java`
- `backend/src/main/java/com/chatbot/model/FeedbackReason.java`
- `backend/src/main/java/com/chatbot/model/Message.java`
- `backend/src/main/java/com/chatbot/model/MessageFeedback.java`
- `backend/src/main/java/com/chatbot/model/MessageFeedbackType.java`
- `backend/src/main/java/com/chatbot/model/PasswordResetToken.java`
- `backend/src/main/java/com/chatbot/model/RefreshToken.java`
- `backend/src/main/java/com/chatbot/model/User.java`
- `backend/src/main/java/com/chatbot/ratelimit/IpRateLimitingFilter.java`
- `backend/src/main/java/com/chatbot/ratelimit/RateLimitProperties.java`
- `backend/src/main/java/com/chatbot/repository/ChatSessionRepository.java`
- `backend/src/main/java/com/chatbot/repository/DocumentChunkEmbeddingRepository.java`
- `backend/src/main/java/com/chatbot/repository/DocumentChunkRepository.java`
- `backend/src/main/java/com/chatbot/repository/DocumentRepository.java`
- `backend/src/main/java/com/chatbot/repository/MessageFeedbackRepository.java`
- `backend/src/main/java/com/chatbot/repository/MessageRepository.java`
- `backend/src/main/java/com/chatbot/repository/PasswordResetTokenRepository.java`
- `backend/src/main/java/com/chatbot/repository/RefreshTokenRepository.java`
- `backend/src/main/java/com/chatbot/repository/RefreshTokenRevocationRepository.java`
- `backend/src/main/java/com/chatbot/repository/UserRepository.java`
- `backend/src/main/java/com/chatbot/security/JwtFilter.java`
- `backend/src/main/java/com/chatbot/security/JwtUtil.java`
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java`
- `backend/src/main/java/com/chatbot/service/AIService.java`
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java`
- `backend/src/main/java/com/chatbot/service/ChatPromptComposer.java`
- `backend/src/main/java/com/chatbot/service/ChatService.java`
- `backend/src/main/java/com/chatbot/service/DocumentService.java`
- `backend/src/main/java/com/chatbot/service/FeedbackAnalyticsService.java`
- `backend/src/main/java/com/chatbot/service/MailService.java`
- `backend/src/main/java/com/chatbot/service/MessageFeedbackService.java`
- `backend/src/main/java/com/chatbot/service/OllamaStreamingService.java`
- `backend/src/main/java/com/chatbot/service/PasswordResetService.java`
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java`
- `backend/src/main/java/com/chatbot/service/RefreshTokenService.java`
- `backend/src/main/java/com/chatbot/service/SearchService.java`
- `backend/src/main/java/com/chatbot/service/UserService.java`
- `backend/src/main/java/com/chatbot/service/chunk/EmbeddingTextBuilder.java`
- `backend/src/main/java/com/chatbot/service/chunk/TextChunk.java`
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java`
- `backend/src/main/java/com/chatbot/service/embedding/EmbeddingService.java`
- `backend/src/main/java/com/chatbot/service/embedding/EmbeddingServiceImpl.java`
- `backend/src/main/java/com/chatbot/service/pdf/PdfTextExtractor.java`
- `backend/src/main/java/com/chatbot/service/pdf/PdfTextExtractorImpl.java`
- `backend/src/main/java/com/chatbot/service/rag/ContextBuilderService.java`
- `backend/src/main/java/com/chatbot/service/rag/ContextBuilderServiceImpl.java`
- `backend/src/main/java/com/chatbot/service/rag/PromptBuilderService.java`
- `backend/src/main/java/com/chatbot/service/rag/PromptBuilderServiceImpl.java`
- `backend/src/main/java/com/chatbot/service/retrieval/Bm25Scorer.java`
- `backend/src/main/java/com/chatbot/service/similarity/SimilarityService.java`
- `backend/src/main/java/com/chatbot/service/similarity/SimilarityServiceImpl.java`
- `backend/src/main/java/com/chatbot/util/CosineSimilarityUtil.java`
- `backend/src/main/resources/application-local.yml`
- `backend/src/main/resources/application-prod.yml`
- `backend/src/main/resources/application.properties`
- `backend/src/main/resources/application.yml`
- `backend/src/main/resources/db/migration/V10__create_document_chunks_table.sql`
- `backend/src/main/resources/db/migration/V11__create_document_chunk_embeddings_table.sql`
- `backend/src/main/resources/db/migration/V12__add_file_hash_to_documents.sql`
- `backend/src/main/resources/db/migration/V1__initial_schema.sql`
- `backend/src/main/resources/db/migration/V2__add_model_name_column.sql`
- `backend/src/main/resources/db/migration/V3__create_refresh_tokens_table.sql`
- `backend/src/main/resources/db/migration/V4__add_email_to_users.sql`
- `backend/src/main/resources/db/migration/V5__create_password_reset_tokens_table.sql`
- `backend/src/main/resources/db/migration/V6__add_is_pinned_to_chat_sessions.sql`
- `backend/src/main/resources/db/migration/V7__create_message_feedbacks_table.sql`
- `backend/src/main/resources/db/migration/V8__add_feedback_reason_to_message_feedbacks.sql`
- `backend/src/main/resources/db/migration/V9__create_documents_table.sql`
- `backend/src/test/java/com/chatbot/ChatbotApplicationTests.java`
- `frontend/.gitignore`
- `frontend/README.md`
- `frontend/eslint.config.js`
- `frontend/index.html`
- `frontend/package-lock.json`
- `frontend/package.json`
- `frontend/public/favicon.svg`
- `frontend/public/icons.svg`
- `frontend/src/App.css`
- `frontend/src/App.jsx`
- `frontend/src/ChatApp.jsx`
- `frontend/src/apiConfig.js`
- `frontend/src/assets/hero.png`
- `frontend/src/assets/react.svg`
- `frontend/src/assets/vite.svg`
- `frontend/src/chatApi.js`
- `frontend/src/components/ChatModeToggle.jsx`
- `frontend/src/components/ChatWindow.jsx`
- `frontend/src/components/FeedbackButtons.jsx`
- `frontend/src/components/FeedbackReasonModal.jsx`
- `frontend/src/components/InputBox.jsx`
- `frontend/src/components/SearchBar.jsx`
- `frontend/src/components/StreamingMessageRenderer.jsx`
- `frontend/src/context/AuthContext.jsx`
- `frontend/src/hooks/useNetworkStatus.js`
- `frontend/src/hooks/useSidebarState.js`
- `frontend/src/index.css`
- `frontend/src/main.jsx`
- `frontend/src/pages/ForgotPasswordPage.jsx`
- `frontend/src/pages/LoginPage.jsx`
- `frontend/src/pages/RegisterPage.jsx`
- `frontend/src/pages/ResetPasswordPage.jsx`
- `frontend/src/websocket.js`
- `frontend/vite.config.js`

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `ChatWebSocketController.handleChat`
- **Function name / class:** `ChatWebSocketController.handleChat`
- **Input:** ChatStompPayload, Principal
- **Output / side effect:** private chunk/error/done events
- **Internal flow:** Validate mode/IDs/ownership, resolve session, optional RAG, create incomplete turn, stream Ollama, persist partial/final.
- **Dependencies, DB/API, errors, security:** ChatService, RAG services, ActiveStreamRegistry, messaging template.
- **Before → After / impact:** Evolved across stream-stop, history, reconnect, Redis and document branches.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 2. `ChatServiceImpl`
- **Function name / class:** `ChatServiceImpl`
- **Input:** authenticated REST/stream operations
- **Output / side effect:** DTO/entity results and DB writes
- **Internal flow:** Owner-scope sessions; CRUD/history; short transactions for streaming placeholders/partials/finals.
- **Dependencies, DB/API, errors, security:** JPA repositories, AIService, TransactionTemplate.
- **Before → After / impact:** Integrated persistence boundary.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 3. `RedisInfraStateStore`
- **Function name / class:** `RedisInfraStateStore`
- **Input:** principal stream/disconnect operations
- **Output / side effect:** Redis state with local fallback
- **Internal flow:** Use TTL values/ZSET and catch Redis outage.
- **Dependencies, DB/API, errors, security:** StringRedisTemplate/ObjectMapper.
- **Before → After / impact:** Shared metadata but local execution remains.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 4. `DocumentServiceImpl.uploadDocument`
- **Function name / class:** `DocumentServiceImpl.uploadDocument`
- **Input:** PDF and username
- **Output / side effect:** upload DTO
- **Internal flow:** Validate→hash→disk→extract→chunk→embed→persist.
- **Dependencies, DB/API, errors, security:** Filesystem/Ollama/PostgreSQL.
- **Before → After / impact:** No atomic transaction across resources.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 5. `AuthController / security chain`
- **Function name / class:** `AuthController / security chain`
- **Input:** auth DTOs and bearer headers
- **Output / side effect:** tokens/authenticated principal
- **Internal flow:** Public auth endpoints, JwtFilter REST, STOMP interceptor, refresh/reset flows.
- **Dependencies, DB/API, errors, security:** Security services/repos.
- **Before → After / impact:** Integrated but frontend refresh remains unused.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`ChatWebSocketController.handleChat`** — Evolved across stream-stop, history, reconnect, Redis and document branches.
- **`ChatServiceImpl`** — Integrated persistence boundary.
- **`RedisInfraStateStore`** — Shared metadata but local execution remains.
- **`DocumentServiceImpl.uploadDocument`** — No atomic transaction across resources.
- **`AuthController / security chain`** — Integrated but frontend refresh remains unused.

## Exact diff-level function and hunk inventory
- `backend/src/main/java/com/chatbot/ChatbotApplication.java`: `main()`
- `backend/src/main/java/com/chatbot/client/OllamaClient.java`: `chat()`, `buildPrompt()`, `parseResponse()`
- `backend/src/main/java/com/chatbot/config/CorsConfig.java`: `corsFilter()`
- `backend/src/main/java/com/chatbot/config/OpenApiConfig.java`: `chatbotOpenApi()`
- `backend/src/main/java/com/chatbot/config/PasswordConfig.java`: `passwordEncoder()`
- `backend/src/main/java/com/chatbot/config/RestTemplateConfig.java`: `restTemplate()`
- `backend/src/main/java/com/chatbot/config/StompJwtChannelInterceptor.java`: `preSend()`, `requiresUser()`, `extractBearer()`
- `backend/src/main/java/com/chatbot/config/WebClientConfig.java`: `webClientBuilder()`
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java`: `configureMessageConverters()`, `configureClientInboundChannel()`, `registerStompEndpoints()`, `configureMessageBroker()`, `webSocketHeartbeatTaskScheduler()`
- `backend/src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java`: `onSessionConnect()`, `onSessionDisconnect()`, `cleanupStaleDisconnectedStreams()`
- `backend/src/main/java/com/chatbot/constant/StreamConstants.java`: `isTransientStreamingFailure()`
- `backend/src/main/java/com/chatbot/controller/AuthController.java`: `register()`, `login()`, `refresh()`, `forgotPassword()`, `resetPassword()`, `me()`
- `backend/src/main/java/com/chatbot/controller/ChatController.java`: `chat()`, `sessions()`, `createSession()`, `deleteSession()`, `updateSessionTitle()`, `updateSessionPinned()`, `messages()`, `activeStream()`
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java`: `handleChat()`, `handleStop()`, `sendJsonToUser()`, `previewQuery()`
- `backend/src/main/java/com/chatbot/controller/DocumentController.java`: `uploadDocument()`
- `backend/src/main/java/com/chatbot/controller/FeedbackAnalyticsController.java`: `stats()`
- `backend/src/main/java/com/chatbot/controller/MessageFeedbackController.java`: `upsertFeedback()`
- `backend/src/main/java/com/chatbot/controller/SearchController.java`: `search()`
- `backend/src/main/java/com/chatbot/controller/SimilarityTestController.java`: `testSearch()`
- `backend/src/main/java/com/chatbot/dto/ActiveStreamStatusDto.java`: `getClientStreamId()`, `setClientStreamId()`, `getSessionId()`, `setSessionId()`, `getAssistantMessageId()`, `setAssistantMessageId()`
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java`: `getType()`, `setType()`, `getClientStreamId()`, `setClientStreamId()`, `getMessageId()`, `setMessageId()`, `getContent()`, `setContent()`, `getSessionId()`, `setSessionId()`, `getDocumentId()`, `setDocumentId()`, `getChatMode()`, `setChatMode()`, `getUserMessageId()`, `setUserMessageId()`, `getEditTargetMessageId()`, `setEditTargetMessageId()`, `getPriorMessages()`, `setPriorMessages()`
- `backend/src/main/java/com/chatbot/dto/PriorMessageDto.java`: `getRole()`, `setRole()`, `getContent()`, `setContent()`
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java`: `chunk()`, `done()`, `error()`, `getClientStreamId()`, `setClientStreamId()`, `getAssistantMessageId()`, `setAssistantMessageId()`, `getType()`, `setType()`, `getChunk()`, `setChunk()`, `getMessage()`, `setMessage()`, `getChatSessionId()`, `setChatSessionId()`
- `backend/src/main/java/com/chatbot/exception/GlobalExceptionHandler.java`: `handleUsernameNotFound()`, `handleBadCredentials()`, `handleRuntime()`, `handleGeneric()`
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java`: `chat()`, `listSessions()`, `getMessages()`, `createEmptySession()`, `deleteSession()`, `updateSessionTitle()`, `updateSessionPinned()`, `resolveStreamingSessionForUser()`, `beginStreamingTurn()`, `updatePartialAiResponse()`, `persistWebsocketTurn()`, `findEditTarget()`, `withFeedback()`, `maybeRefreshSessionTitle()`, `resolveCurrentUser()`, `resolveSession()`, `buildTitle()`
- `backend/src/main/java/com/chatbot/impl/DocumentServiceImpl.java`: `uploadDocument()`, `preview()`, `calculateSha256()`
- `backend/src/main/java/com/chatbot/impl/FeedbackAnalyticsServiceImpl.java`: `getStats()`
- `backend/src/main/java/com/chatbot/impl/MailServiceImpl.java`: `sendPasswordResetEmail()`
- `backend/src/main/java/com/chatbot/impl/MessageFeedbackServiceImpl.java`: `upsertFeedback()`, `resolveCurrentUser()`
- `backend/src/main/java/com/chatbot/impl/OllamaStreamingServiceImpl.java`: `streamChat()`, `mapOllamaLineToTextFlux()`, `visualize()`
- `backend/src/main/java/com/chatbot/impl/PasswordResetServiceImpl.java`: `requestReset()`, `resetPassword()`, `persistResetToken()`, `hashToken()`
- `backend/src/main/java/com/chatbot/impl/RefreshTokenServiceImpl.java`: `createRefreshToken()`, `refreshAccessToken()`, `persistRefreshToken()`, `hashToken()`
- `backend/src/main/java/com/chatbot/impl/SearchServiceImpl.java`: `search()`, `resolveCurrentUser()`, `contentPreview()`
- `backend/src/main/java/com/chatbot/impl/UserServiceImpl.java`: `loadUserByUsername()`, `register()`
- `backend/src/main/java/com/chatbot/ratelimit/IpRateLimitingFilter.java`: `doFilterInternal()`, `resolveBucket()`, `isLoginRequest()`, `isChatRequest()`, `newBucket()`, `resolveClientIp()`
- `backend/src/main/java/com/chatbot/ratelimit/RateLimitProperties.java`: `getLogin()`, `setLogin()`, `getChat()`, `setChat()`, `getCapacity()`, `setCapacity()`, `getRefillTokens()`, `setRefillTokens()`, `getRefillDurationSeconds()`, `setRefillDurationSeconds()`
- `backend/src/main/java/com/chatbot/security/JwtFilter.java`: `doFilterInternal()`, `isJwtOptionalHttpPath()`
- `backend/src/main/java/com/chatbot/security/JwtUtil.java`: `generateToken()`, `extractUsername()`, `validateToken()`, `parseClaims()`
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java`: `authenticationManager()`, `securityFilterChain()`
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java`: `replaceAndStart()`, `getActiveStreamStatus()`, `isDuplicateClientStream()`, `cancel()`, `removeIfMatches()`, `hasActiveStream()`, `isCurrentStream()`, `markDisconnected()`, `clearDisconnected()`, `expiredDisconnectedUsers()`, `ActiveStreamStatus()`, `ActiveStream()`
- `backend/src/main/java/com/chatbot/service/ChatPromptComposer.java`: `compose()`
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java`: `getInstanceId()`, `saveActiveStream()`, `getActiveStream()`, `removeActiveStream()`, `removeActiveStreamIfMatches()`, `hasActiveStream()`, `isActiveStreamId()`, `markDisconnected()`, `clearDisconnected()`, `getExpiredDisconnectedUsers()`, `incrementRateLimitCounter()`, `activeStreamKey()`, `toJson()`, `fromJson()`, `withRedis()`, `fromRedis()`, `warnRedisUnavailable()`, `ActiveStreamState()`
- `backend/src/main/java/com/chatbot/service/chunk/EmbeddingTextBuilder.java`: `build()`
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java`: `chunk()`, `chunkText()`, `paragraphAwareChunks()`, `normalizeWhitespace()`, `buildSegments()`, `flushMergeBuffer()`, `splitOversized()`, `chooseSeparator()`, `endsWithListItem()`, `segmentFromBuffer()`, `packSegments()`, `packedContentLength()`, `emitPackedChunk()`, `resolveOverlapStart()`, `splitIntoUnits()`, `splitBySentenceAndSemicolon()`, `splitSemicolonClauses()`, `isListItem()`, `hardSplit()`, `isHeading()`, `isTitleCase()`, `estimatePageNumber()`, `countWords()`, `collapseWhitespace()`, `indexOfFrom()`, `clamp()`, `legacyFixedSizeChunks()`
- `backend/src/main/java/com/chatbot/service/embedding/EmbeddingServiceImpl.java`: `generateEmbedding()`, `generateDocumentEmbedding()`, `generateDocumentEmbeddings()`, `generateQueryEmbedding()`, `requestEmbedding()`
- `backend/src/main/java/com/chatbot/service/pdf/PdfTextExtractorImpl.java`: `extractText()`
- `backend/src/main/java/com/chatbot/service/rag/ContextBuilderServiceImpl.java`: `buildContext()`, `resolveDocumentId()`, `normalizeForDedup()`
- `backend/src/main/java/com/chatbot/service/rag/PromptBuilderServiceImpl.java`: `buildPrompt()`
- `backend/src/main/java/com/chatbot/service/retrieval/Bm25Scorer.java`: `score()`, `expandQuery()`, `computeDocumentFrequency()`, `tokenize()`, `dedupeTokens()`, `preview()`
- `backend/src/main/java/com/chatbot/service/similarity/SimilarityServiceImpl.java`: `findRelevantChunks()`, `resolveKeywordWeight()`, `preview()`, `cosineSimilarity()`
- `backend/src/main/java/com/chatbot/util/CosineSimilarityUtil.java`: `calculateSimilarity()`
- `frontend/src/components/ChatModeToggle.jsx`: `ChatModeToggle()`
- `frontend/src/components/ChatWindow.jsx`: `escapeRegExp()`, `UserBubble()`, `AssistantBubble()`, `ChatWindow()`, `historyFeedbackByMessageId()`, `handleCopy()`, `onScroll()`, `registerMessageRef()`, `renderContentWithSearchHighlight()`, `parts()`, `beginEdit()`, `cancelEdit()`, `commitEdit()`, `getSelectedFeedback()`, `submitFeedback()`, `closeFeedbackReasonModal()`, `handleFeedbackReasonSubmit()`, `handleFeedback()`
- `frontend/src/components/FeedbackButtons.jsx`: `FeedbackButtons()`
- `frontend/src/components/FeedbackReasonModal.jsx`: `FeedbackReasonModal()`, `onKeyDown()`
- `frontend/src/components/InputBox.jsx`: `InputBox()`, `stopRecognition()`, `getRecognition()`, `handleMicClick()`, `handleKeyDown()`
- `frontend/src/components/SearchBar.jsx`: `SearchBar()`, `handleSubmit()`
- `frontend/src/components/StreamingMessageRenderer.jsx`: `escapeHtml()`, `sanitizeHref()`, `applyInlineMarkdown()`, `renderMarkdownToHtml()`, `StreamingMessageRenderer()`, `animate()`, `html()`
- `frontend/src/context/AuthContext.jsx`: `parseJsonSafe()`, `AuthProvider()`, `useAuth()`, `login()`, `register()`, `logout()`
- `frontend/src/hooks/useNetworkStatus.js`: `useNetworkStatus()`, `handleOnline()`, `handleOffline()`
- `frontend/src/hooks/useSidebarState.js`: `getIsMobile()`, `useSidebarState()`, `onChange()`, `toggleSidebar()`, `openSidebar()`, `closeSidebar()`, `sidebarClassName()`
- `frontend/src/pages/ForgotPasswordPage.jsx`: `parseJsonSafe()`, `ForgotPasswordPage()`, `handleSubmit()`
- `frontend/src/pages/LoginPage.jsx`: `LoginPage()`, `handleSubmit()`
- `frontend/src/pages/RegisterPage.jsx`: `parseJsonSafe()`, `RegisterPage()`, `handleSubmit()`
- `frontend/src/pages/ResetPasswordPage.jsx`: `parseJsonSafe()`, `ResetPasswordPage()`, `handleSubmit()`

`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `.github/workflows/ci.yml`
- `backend/.DS_Store`
- `backend/.env.staging.example`
- `backend/.gitattributes`
- `backend/.gitignore`
- `backend/.java-version`
- `backend/.mvn/wrapper/maven-wrapper.properties`
- `backend/Chatbot.postman_collection.json`
- `backend/chatbot-backend-prompt.md`
- `backend/mvnw`
- `backend/mvnw.cmd`
- `backend/pom.xml`
- `backend/src/.DS_Store`
- `backend/src/main/.DS_Store`
- `backend/src/main/java/.DS_Store`
- `backend/src/main/java/com/.DS_Store`
- `backend/src/main/java/com/chatbot/.DS_Store`
- `backend/src/main/java/com/chatbot/ChatbotApplication.java`
- `backend/src/main/java/com/chatbot/client/OllamaClient.java`
- `backend/src/main/java/com/chatbot/config/CorsConfig.java`
- `backend/src/main/java/com/chatbot/config/OpenApiConfig.java`
- `backend/src/main/java/com/chatbot/config/PasswordConfig.java`
- `backend/src/main/java/com/chatbot/config/RestTemplateConfig.java`
- `backend/src/main/java/com/chatbot/config/StompJwtChannelInterceptor.java`
- `backend/src/main/java/com/chatbot/config/WebClientConfig.java`
- `backend/src/main/java/com/chatbot/config/WebSocketConfig.java`
- `backend/src/main/java/com/chatbot/config/WebSocketSessionCleanupListener.java`
- `backend/src/main/java/com/chatbot/constant/AppConstants.java`
- `backend/src/main/java/com/chatbot/constant/ResponseCode.java`
- `backend/src/main/java/com/chatbot/constant/StreamConstants.java`
- `backend/src/main/java/com/chatbot/controller/AuthController.java`
- `backend/src/main/java/com/chatbot/controller/ChatController.java`
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java`
- `backend/src/main/java/com/chatbot/controller/DocumentController.java`
- `backend/src/main/java/com/chatbot/controller/FeedbackAnalyticsController.java`
- `backend/src/main/java/com/chatbot/controller/MessageFeedbackController.java`
- `backend/src/main/java/com/chatbot/controller/SearchController.java`
- `backend/src/main/java/com/chatbot/controller/SimilarityTestController.java`
- `backend/src/main/java/com/chatbot/dto/ActiveStreamStatusDto.java`
- `backend/src/main/java/com/chatbot/dto/AuthResponse.java`
- `backend/src/main/java/com/chatbot/dto/ChatMessageResponse.java`
- `backend/src/main/java/com/chatbot/dto/ChatRequest.java`
- `backend/src/main/java/com/chatbot/dto/ChatResponse.java`
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java`
- `backend/src/main/java/com/chatbot/dto/DocumentUploadResponse.java`
- `backend/src/main/java/com/chatbot/dto/ErrorResponse.java`
- `backend/src/main/java/com/chatbot/dto/FeedbackStatsResponse.java`
- `backend/src/main/java/com/chatbot/dto/ForgotPasswordRequest.java`
- `backend/src/main/java/com/chatbot/dto/LoginRequest.java`
- `backend/src/main/java/com/chatbot/dto/MessageFeedbackRequest.java`
- `backend/src/main/java/com/chatbot/dto/MessageFeedbackResponse.java`
- `backend/src/main/java/com/chatbot/dto/MessageResponse.java`
- `backend/src/main/java/com/chatbot/dto/PriorMessageDto.java`
- `backend/src/main/java/com/chatbot/dto/RefreshTokenRequest.java`
- `backend/src/main/java/com/chatbot/dto/RegisterRequest.java`
- `backend/src/main/java/com/chatbot/dto/ResetPasswordRequest.java`
- `backend/src/main/java/com/chatbot/dto/SearchResultDto.java`
- `backend/src/main/java/com/chatbot/dto/StreamDownstreamEvent.java`
- `backend/src/main/java/com/chatbot/dto/UpdateSessionPinnedRequest.java`
- `backend/src/main/java/com/chatbot/dto/UpdateSessionTitleRequest.java`
- `backend/src/main/java/com/chatbot/dto/embedding/EmbeddingRequest.java`
- `backend/src/main/java/com/chatbot/dto/embedding/EmbeddingResponse.java`
- `backend/src/main/java/com/chatbot/dto/similarity/SimilarityResult.java`
- `backend/src/main/java/com/chatbot/exception/GlobalExceptionHandler.java`
- `backend/src/main/java/com/chatbot/impl/ChatServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/DocumentServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/FeedbackAnalyticsServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/MailServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/MessageFeedbackServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/OllamaStreamingServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/PasswordResetServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/RefreshTokenServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/SearchServiceImpl.java`
- `backend/src/main/java/com/chatbot/impl/UserServiceImpl.java`
- `backend/src/main/java/com/chatbot/model/ChatSession.java`
- `backend/src/main/java/com/chatbot/model/Document.java`
- `backend/src/main/java/com/chatbot/model/DocumentChunk.java`
- `backend/src/main/java/com/chatbot/model/DocumentChunkEmbedding.java`
- `backend/src/main/java/com/chatbot/model/FeedbackReason.java`
- `backend/src/main/java/com/chatbot/model/Message.java`
- `backend/src/main/java/com/chatbot/model/MessageFeedback.java`
- `backend/src/main/java/com/chatbot/model/MessageFeedbackType.java`
- `backend/src/main/java/com/chatbot/model/PasswordResetToken.java`
- `backend/src/main/java/com/chatbot/model/RefreshToken.java`
- `backend/src/main/java/com/chatbot/model/User.java`
- `backend/src/main/java/com/chatbot/ratelimit/IpRateLimitingFilter.java`
- `backend/src/main/java/com/chatbot/ratelimit/RateLimitProperties.java`
- `backend/src/main/java/com/chatbot/repository/ChatSessionRepository.java`
- `backend/src/main/java/com/chatbot/repository/DocumentChunkEmbeddingRepository.java`
- `backend/src/main/java/com/chatbot/repository/DocumentChunkRepository.java`
- `backend/src/main/java/com/chatbot/repository/DocumentRepository.java`
- `backend/src/main/java/com/chatbot/repository/MessageFeedbackRepository.java`
- `backend/src/main/java/com/chatbot/repository/MessageRepository.java`
- `backend/src/main/java/com/chatbot/repository/PasswordResetTokenRepository.java`
- `backend/src/main/java/com/chatbot/repository/RefreshTokenRepository.java`
- `backend/src/main/java/com/chatbot/repository/RefreshTokenRevocationRepository.java`
- `backend/src/main/java/com/chatbot/repository/UserRepository.java`
- `backend/src/main/java/com/chatbot/security/JwtFilter.java`
- `backend/src/main/java/com/chatbot/security/JwtUtil.java`
- `backend/src/main/java/com/chatbot/security/SecurityConfig.java`
- `backend/src/main/java/com/chatbot/service/AIService.java`
- `backend/src/main/java/com/chatbot/service/ActiveStreamRegistry.java`
- `backend/src/main/java/com/chatbot/service/ChatPromptComposer.java`
- `backend/src/main/java/com/chatbot/service/ChatService.java`
- `backend/src/main/java/com/chatbot/service/DocumentService.java`
- `backend/src/main/java/com/chatbot/service/FeedbackAnalyticsService.java`
- `backend/src/main/java/com/chatbot/service/MailService.java`
- `backend/src/main/java/com/chatbot/service/MessageFeedbackService.java`
- `backend/src/main/java/com/chatbot/service/OllamaStreamingService.java`
- `backend/src/main/java/com/chatbot/service/PasswordResetService.java`
- `backend/src/main/java/com/chatbot/service/RedisInfraStateStore.java`
- `backend/src/main/java/com/chatbot/service/RefreshTokenService.java`
- `backend/src/main/java/com/chatbot/service/SearchService.java`
- `backend/src/main/java/com/chatbot/service/UserService.java`
- `backend/src/main/java/com/chatbot/service/chunk/EmbeddingTextBuilder.java`
- `backend/src/main/java/com/chatbot/service/chunk/TextChunk.java`
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java`
- `backend/src/main/java/com/chatbot/service/embedding/EmbeddingService.java`
- `backend/src/main/java/com/chatbot/service/embedding/EmbeddingServiceImpl.java`
- `backend/src/main/java/com/chatbot/service/pdf/PdfTextExtractor.java`
- `backend/src/main/java/com/chatbot/service/pdf/PdfTextExtractorImpl.java`
- `backend/src/main/java/com/chatbot/service/rag/ContextBuilderService.java`
- `backend/src/main/java/com/chatbot/service/rag/ContextBuilderServiceImpl.java`
- `backend/src/main/java/com/chatbot/service/rag/PromptBuilderService.java`
- `backend/src/main/java/com/chatbot/service/rag/PromptBuilderServiceImpl.java`
- `backend/src/main/java/com/chatbot/service/retrieval/Bm25Scorer.java`
- `backend/src/main/java/com/chatbot/service/similarity/SimilarityService.java`
- `backend/src/main/java/com/chatbot/service/similarity/SimilarityServiceImpl.java`
- `backend/src/main/java/com/chatbot/util/CosineSimilarityUtil.java`
- `backend/src/main/resources/application-local.yml`
- `backend/src/main/resources/application-prod.yml`
- `backend/src/main/resources/application.properties`
- `backend/src/main/resources/application.yml`
- `backend/src/main/resources/db/migration/V10__create_document_chunks_table.sql`
- `backend/src/main/resources/db/migration/V11__create_document_chunk_embeddings_table.sql`
- `backend/src/main/resources/db/migration/V12__add_file_hash_to_documents.sql`
- `backend/src/main/resources/db/migration/V1__initial_schema.sql`
- `backend/src/main/resources/db/migration/V2__add_model_name_column.sql`
- `backend/src/main/resources/db/migration/V3__create_refresh_tokens_table.sql`
- `backend/src/main/resources/db/migration/V4__add_email_to_users.sql`
- `backend/src/main/resources/db/migration/V5__create_password_reset_tokens_table.sql`
- `backend/src/main/resources/db/migration/V6__add_is_pinned_to_chat_sessions.sql`
- `backend/src/main/resources/db/migration/V7__create_message_feedbacks_table.sql`
- `backend/src/main/resources/db/migration/V8__add_feedback_reason_to_message_feedbacks.sql`
- `backend/src/main/resources/db/migration/V9__create_documents_table.sql`
- `backend/src/test/java/com/chatbot/ChatbotApplicationTests.java`
- `frontend/.gitignore`
- `frontend/README.md`
- `frontend/eslint.config.js`
- `frontend/index.html`
- `frontend/package-lock.json`
- `frontend/package.json`
- `frontend/public/favicon.svg`
- `frontend/public/icons.svg`
- `frontend/src/App.css`
- `frontend/src/App.jsx`
- `frontend/src/ChatApp.jsx`
- `frontend/src/apiConfig.js`
- `frontend/src/assets/hero.png`
- `frontend/src/assets/react.svg`
- `frontend/src/assets/vite.svg`
- `frontend/src/chatApi.js`
- `frontend/src/components/ChatModeToggle.jsx`
- `frontend/src/components/ChatWindow.jsx`
- `frontend/src/components/FeedbackButtons.jsx`
- `frontend/src/components/FeedbackReasonModal.jsx`
- `frontend/src/components/InputBox.jsx`
- `frontend/src/components/SearchBar.jsx`
- `frontend/src/components/StreamingMessageRenderer.jsx`
- `frontend/src/context/AuthContext.jsx`
- `frontend/src/hooks/useNetworkStatus.js`
- `frontend/src/hooks/useSidebarState.js`
- `frontend/src/index.css`
- `frontend/src/main.jsx`
- `frontend/src/pages/ForgotPasswordPage.jsx`
- `frontend/src/pages/LoginPage.jsx`
- `frontend/src/pages/RegisterPage.jsx`
- `frontend/src/pages/ResetPasswordPage.jsx`
- `frontend/src/websocket.js`
- `frontend/vite.config.js`

# APIs Added/Modified
All current REST and STOMP contracts; effective context prefix `/api/v1`.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
V1–V12: users, sessions, messages, tokens, feedback, documents, chunks, JSONB embeddings.

# Frontend Changes
`ChatApp.jsx` orchestrates auth-adjacent chat UI, sessions, recovery, search, feedback, document mode and voice.

# Backend Changes
Controllers delegate to services/repositories; Ollama sync/stream paths and Redis metadata coexist.

# Security Changes
Stateless JWT, BCrypt, STOMP CONNECT JWT, CSRF disabled, CORS, IP rate filter.

# Testing Changes
Current main: only `ChatbotApplicationTests.contextLoads()`; no frontend tests.

# Technical Topics Covered
Java 17, Spring Boot 3.3.5, React 19, REST, STOMP, JPA, PostgreSQL, Flyway, Redis, JWT, Bucket4j, Ollama, RAG, PDFBox, SMTP, CI

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Main combines layered REST plus event-driven streaming; README may be stale, code/history wins.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Integrated fixes are attributed in feature documents.

# Edge Cases
Distributed stream ownership, token expiry, duplicate writes, external failures and long payloads.

# Production Considerations
## Current implementation
Main combines layered REST plus event-driven streaming; README may be stale, code/history wins.

## Improvement, not currently implemented
External broker, distributed limiter, object storage/vector index, observability and comprehensive tests.

# Interview Topics From This Branch
- Function boundaries: ChatWebSocketController.handleChat, ChatServiceImpl, RedisInfraStateStore, DocumentServiceImpl.uploadDocument, AuthController / security chain
- API contract: All current REST and STOMP contracts; effective context prefix `/api/v1`.
- Persistence: V1–V12: users, sessions, messages, tokens, feedback, documents, chunks, JSONB embeddings.
- Security: Stateless JWT, BCrypt, STOMP CONNECT JWT, CSRF disabled, CORS, IP rate filter.
- Failure/edge cases: Distributed stream ownership, token expiry, duplicate writes, external failures and long payloads.
- Separate exhaustive Q&A: `Interview/main.txt`
