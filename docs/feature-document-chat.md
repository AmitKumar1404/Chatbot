# feature/document-chat — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Implement PDF upload/indexing and explicit NORMAL/DOCUMENT hybrid RAG chat.

# Branch Purpose
Implement PDF upload/indexing and explicit NORMAL/DOCUMENT hybrid RAG chat.

# Base Branch / Branch Lineage
Fourteen commits merged PR #34; tip-only `75fed10` distinct mode-separation patch/tests; main has related `920edf5`.

# Git Commit History
- `7243ca7` — feat(rag): add PDF upload infrastructure
- `577e560` — feat(rag): add PDF text extraction service
- `18de8ee` — feat(document): implement PDF text chunking
- `686f82e` — feat(document): persist extracted PDF chunks in database
- `9f5cf10` — feat: support dynamic document selection for RAG chat
- `411ee9a` — feat(rag): implement hybrid retrieval with BM25 score fusion
- `8d7d866` — feat(rag): improve document chunking with heading and sentence awareness
- `9b23108` — feat(rag): add heading-aware embedding builder for document indexing
- `ecc8aaa` — feat(rag): improve embedding pipeline observability and resilience
- `75203d1` — feat(rag): improve hybrid retrieval observability and configuration
- `12b3e7a` — refactor(rag): improve hybrid retrieval quality
- `0617ead` — Limit and dedupe RAG context assembly
- `c126e4f` — Harden RAG prompt with clear context boundaries
- `14b38eb` — fix(db): add file hash migration
- `75fed10` — fix: enforce normal and document chat mode separation

# Files Changed
- `backend/.gitignore` — 7243ca7:M
- `backend/pom.xml` — 7243ca7:M
- `backend/src/main/java/com/chatbot/config/RestTemplateConfig.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/constant/AppConstants.java` — 7243ca7:M
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java` — 9f5cf10:M, 75203d1:M, 75fed10:M
- `backend/src/main/java/com/chatbot/controller/DocumentController.java` — 7243ca7:A
- `backend/src/main/java/com/chatbot/controller/SimilarityTestController.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java` — 9f5cf10:M, 75fed10:M
- `backend/src/main/java/com/chatbot/dto/DocumentUploadResponse.java` — 7243ca7:A
- `backend/src/main/java/com/chatbot/dto/embedding/EmbeddingRequest.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/dto/embedding/EmbeddingResponse.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/dto/similarity/SimilarityResult.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/impl/DocumentServiceImpl.java` — 7243ca7:A, 577e560:M, 18de8ee:M, 686f82e:M, 9f5cf10:M, 411ee9a:M, 8d7d866:M, 9b23108:M, ecc8aaa:M
- `backend/src/main/java/com/chatbot/model/Document.java` — 7243ca7:A, 9f5cf10:M
- `backend/src/main/java/com/chatbot/model/DocumentChunk.java` — 686f82e:A
- `backend/src/main/java/com/chatbot/model/DocumentChunkEmbedding.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/repository/DocumentChunkEmbeddingRepository.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/repository/DocumentChunkRepository.java` — 686f82e:A
- `backend/src/main/java/com/chatbot/repository/DocumentRepository.java` — 7243ca7:A, 9f5cf10:M
- `backend/src/main/java/com/chatbot/service/DocumentService.java` — 7243ca7:A, 9f5cf10:M
- `backend/src/main/java/com/chatbot/service/OllamaStreamingService.java` — 9f5cf10:M
- `backend/src/main/java/com/chatbot/service/chunk/EmbeddingTextBuilder.java` — 9b23108:A
- `backend/src/main/java/com/chatbot/service/chunk/TextChunk.java` — 8d7d866:A
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java` — 18de8ee:A, 411ee9a:M, 8d7d866:M
- `backend/src/main/java/com/chatbot/service/embedding/EmbeddingService.java` — 9f5cf10:A, 411ee9a:M, ecc8aaa:M
- `backend/src/main/java/com/chatbot/service/embedding/EmbeddingServiceImpl.java` — 9f5cf10:A, 411ee9a:M, ecc8aaa:M
- `backend/src/main/java/com/chatbot/service/pdf/PdfTextExtractor.java` — 577e560:A
- `backend/src/main/java/com/chatbot/service/pdf/PdfTextExtractorImpl.java` — 577e560:A
- `backend/src/main/java/com/chatbot/service/rag/ContextBuilderService.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/service/rag/ContextBuilderServiceImpl.java` — 9f5cf10:A, 0617ead:M
- `backend/src/main/java/com/chatbot/service/rag/PromptBuilderService.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/service/rag/PromptBuilderServiceImpl.java` — 9f5cf10:A, c126e4f:M
- `backend/src/main/java/com/chatbot/service/retrieval/Bm25Scorer.java` — 411ee9a:A, 75203d1:M, 12b3e7a:M
- `backend/src/main/java/com/chatbot/service/similarity/SimilarityService.java` — 9f5cf10:A, 75fed10:M
- `backend/src/main/java/com/chatbot/service/similarity/SimilarityServiceImpl.java` — 9f5cf10:A, 411ee9a:M, 75203d1:M, 12b3e7a:M
- `backend/src/main/java/com/chatbot/util/CosineSimilarityUtil.java` — 9f5cf10:A
- `backend/src/main/resources/application.yml` — 7243ca7:M, 9f5cf10:M, 411ee9a:M, 8d7d866:M, 9b23108:M, ecc8aaa:M, 75203d1:M, 0617ead:M
- `backend/src/main/resources/db/migration/V10__create_document_chunks_table.sql` — 686f82e:A
- `backend/src/main/resources/db/migration/V11__create_document_chunk_embeddings_table.sql` — 9f5cf10:A
- `backend/src/main/resources/db/migration/V12__add_file_hash_to_documents.sql` — 14b38eb:A
- `backend/src/main/resources/db/migration/V9__create_documents_table.sql` — 7243ca7:A
- `backend/src/test/java/com/chatbot/dto/ChatStompPayloadChatModeTest.java` — 75fed10:A
- `backend/src/test/java/com/chatbot/service/rag/PromptBuilderServiceImplTest.java` — 75fed10:A
- `frontend/src/App.css` — 75fed10:M
- `frontend/src/ChatApp.jsx` — 9f5cf10:M, 75fed10:M
- `frontend/src/chatApi.js` — 9f5cf10:M
- `frontend/src/components/ChatModeToggle.jsx` — 75fed10:A

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `DocumentServiceImpl.uploadDocument`
- **Function name / class:** `DocumentServiceImpl.uploadDocument`
- **Input:** MultipartFile, username
- **Output / side effect:** DocumentUploadResponse
- **Internal flow:** Validate PDF/name/MIME/25MB; hash+dedupe; copy disk; extract; chunk; build embedding texts; call Ollama; persist document/chunks/vectors.
- **Dependencies, DB/API, errors, security:** Repositories, PdfTextExtractor, TextChunkService, EmbeddingService; no method-level transaction/compensation.
- **Before → After / impact:** Evolved across upload→extract→chunk→persist→embed commits.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 2. `TextChunkService.chunk`
- **Function name / class:** `TextChunkService.chunk`
- **Input:** raw text
- **Output / side effect:** List<TextChunk>
- **Internal flow:** Normalize; choose legacy or paragraph-aware path; detect headings; split oversized sentence/list units; pack target size with overlap and metadata.
- **Dependencies, DB/API, errors, security:** Config chunk size/overlap; pure service.
- **Before → After / impact:** Replaced fixed-size `chunkText` with structural chunking while retaining compatibility helper.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 3. `EmbeddingServiceImpl.generateDocumentEmbeddings`
- **Function name / class:** `EmbeddingServiceImpl.generateDocumentEmbeddings`
- **Input:** texts
- **Output / side effect:** parallel-shaped List<List<Float>> with null failures
- **Internal flow:** Process sequentially in configured windows; prefix document text; catch each failure and append null.
- **Dependencies, DB/API, errors, security:** RestTemplate `/api/embeddings`; not true batch HTTP.
- **Before → After / impact:** Adds resilience but can create incomplete index.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 4. `Bm25Scorer.score`
- **Function name / class:** `Bm25Scorer.score`
- **Input:** query, document contents
- **Output / side effect:** double[] scores
- **Internal flow:** Tokenize/expand/dedupe query; compute document frequencies/IDF; score with k1/b and length normalization.
- **Dependencies, DB/API, errors, security:** In-memory candidate corpus.
- **Before → After / impact:** Adds lexical signal to dense retrieval.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 5. `SimilarityServiceImpl.findRelevantChunks`
- **Function name / class:** `SimilarityServiceImpl.findRelevantChunks`
- **Input:** question, documentId, topK
- **Output / side effect:** ranked DocumentChunk list
- **Internal flow:** Embed query; load all document vectors; cosine; optional BM25; max-normalize weighted fusion; sort with deterministic ties; return topK.
- **Dependencies, DB/API, errors, security:** EmbeddingService, embedding repository, Bm25Scorer; read-only transaction.
- **Before → After / impact:** Evolved from cosine-only to configurable hybrid retrieval/observability.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 6. `ContextBuilderServiceImpl.buildContext`
- **Function name / class:** `ContextBuilderServiceImpl.buildContext`
- **Input:** retrieved chunks
- **Output / side effect:** bounded context String
- **Internal flow:** Skip blank/exact-normalized duplicates; cap count/chars; label chunks; log IDs/counts.
- **Dependencies, DB/API, errors, security:** Configured max chunks/chars.
- **Before → After / impact:** Later commit prevents oversized/repeated prompt context.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 7. `PromptBuilderServiceImpl.buildPrompt`
- **Function name / class:** `PromptBuilderServiceImpl.buildPrompt`
- **Input:** context, question
- **Output / side effect:** grounded prompt String
- **Internal flow:** Place strict SYSTEM rules, delimited CONTEXT and QUESTION; exact fallback when absent.
- **Dependencies, DB/API, errors, security:** Used before ChatPromptComposer/Ollama.
- **Before → After / impact:** Hardened wording; no guarantee against prompt injection.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 8. `ChatWebSocketController.handleChat DOCUMENT path`
- **Function name / class:** `ChatWebSocketController.handleChat DOCUMENT path`
- **Input:** payload/principal
- **Output / side effect:** stream events
- **Internal flow:** Use explicit ChatMode; require owned document; retrieve topK; build context/prompt; compose history; stream normally.
- **Dependencies, DB/API, errors, security:** DocumentRepository, similarity/context/prompt services.
- **Before → After / impact:** Before documentId could implicitly influence mode; tip/main patches make mode explicit.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 9. `calculateSha256`
- **Function name / class:** `calculateSha256`
- **Input:** MultipartFile
- **Output / side effect:** hex hash
- **Internal flow:** Read bytes, SHA-256 digest, hex encode; wrap IO/algorithm errors.
- **Dependencies, DB/API, errors, security:** V12 unique file_hash.
- **Before → After / impact:** Filename duplicate logic replaced by content identity.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 10. `ChatModeToggle / buildModePayloadFields`
- **Function name / class:** `ChatModeToggle / buildModePayloadFields`
- **Input:** mode/document selection
- **Output / side effect:** UI mode and STOMP fields
- **Internal flow:** Only DOCUMENT sends documentId; NORMAL explicitly excludes it; upload does not automatically imply mode.
- **Dependencies, DB/API, errors, security:** ChatApp state and ChatStompPayload.ChatMode.
- **Before → After / impact:** 75fed10 and 920edf5 are related but distinct patches.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`DocumentServiceImpl.uploadDocument`** — Evolved across upload→extract→chunk→persist→embed commits.
- **`TextChunkService.chunk`** — Replaced fixed-size `chunkText` with structural chunking while retaining compatibility helper.
- **`EmbeddingServiceImpl.generateDocumentEmbeddings`** — Adds resilience but can create incomplete index.
- **`Bm25Scorer.score`** — Adds lexical signal to dense retrieval.
- **`SimilarityServiceImpl.findRelevantChunks`** — Evolved from cosine-only to configurable hybrid retrieval/observability.
- **`ContextBuilderServiceImpl.buildContext`** — Later commit prevents oversized/repeated prompt context.
- **`PromptBuilderServiceImpl.buildPrompt`** — Hardened wording; no guarantee against prompt injection.
- **`ChatWebSocketController.handleChat DOCUMENT path`** — Before documentId could implicitly influence mode; tip/main patches make mode explicit.
- **`calculateSha256`** — Filename duplicate logic replaced by content identity.
- **`ChatModeToggle / buildModePayloadFields`** — 75fed10 and 920edf5 are related but distinct patches.

## Exact diff-level function and hunk inventory
- `backend/src/main/java/com/chatbot/config/RestTemplateConfig.java`: `restTemplate()`
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java`: `previewQuery()`
- `backend/src/main/java/com/chatbot/controller/DocumentController.java`: `uploadDocument()`
- `backend/src/main/java/com/chatbot/controller/SimilarityTestController.java`: `testSearch()`
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java`: `getDocumentId()`, `setDocumentId()`, `getChatMode()`, `setChatMode()`
- `backend/src/main/java/com/chatbot/impl/DocumentServiceImpl.java`: `uploadDocument()`, `calculateSha256()`, `preview()`
- `backend/src/main/java/com/chatbot/service/chunk/EmbeddingTextBuilder.java`: `build()`
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java`: `chunkText()`, `chunk()`, `paragraphAwareChunks()`, `normalizeWhitespace()`, `buildSegments()`, `flushMergeBuffer()`, `splitOversized()`, `chooseSeparator()`, `endsWithListItem()`, `segmentFromBuffer()`, `packSegments()`, `packedContentLength()`, `emitPackedChunk()`, `resolveOverlapStart()`, `splitIntoUnits()`, `splitBySentenceAndSemicolon()`, `splitSemicolonClauses()`, `isListItem()`, `hardSplit()`, `isHeading()`, `isTitleCase()`, `estimatePageNumber()`, `countWords()`, `collapseWhitespace()`, `indexOfFrom()`, `clamp()`, `legacyFixedSizeChunks()`
- `backend/src/main/java/com/chatbot/service/embedding/EmbeddingServiceImpl.java`: `generateEmbedding()`, `generateDocumentEmbedding()`, `generateQueryEmbedding()`, `requestEmbedding()`, `generateDocumentEmbeddings()`
- `backend/src/main/java/com/chatbot/service/pdf/PdfTextExtractorImpl.java`: `extractText()`
- `backend/src/main/java/com/chatbot/service/rag/ContextBuilderServiceImpl.java`: `buildContext()`, `resolveDocumentId()`, `normalizeForDedup()`
- `backend/src/main/java/com/chatbot/service/rag/PromptBuilderServiceImpl.java`: `buildPrompt()`
- `backend/src/main/java/com/chatbot/service/retrieval/Bm25Scorer.java`: `score()`, `expandQuery()`, `computeDocumentFrequency()`, `tokenize()`, `preview()`, `dedupeTokens()`
- `backend/src/main/java/com/chatbot/service/similarity/SimilarityServiceImpl.java`: `findRelevantChunks()`, `cosineSimilarity()`, `findResultIndex()`, `resolveKeywordWeight()`, `preview()`
- `backend/src/main/java/com/chatbot/util/CosineSimilarityUtil.java`: `calculateSimilarity()`
- `frontend/src/ChatApp.jsx`: `handleDocumentUpload()`, `applyChatMode()`, `buildModePayloadFields()`, changed context `export default function ChatApp() {`
- `frontend/src/chatApi.js`: `uploadDocumentApi()`, changed context `export async function createChatSession(token) {`
- `frontend/src/components/ChatModeToggle.jsx`: `ChatModeToggle()`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `backend/src/main/java/com/chatbot/config/RestTemplateConfig.java::restTemplate()` — Spring configuration factory method hai jo application infrastructure bean banata hai. Git evidence: `9f5cf10`.
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java::previewQuery()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `75fed10`.
- `backend/src/main/java/com/chatbot/controller/DocumentController.java::uploadDocument()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `7243ca7`.
- `backend/src/main/java/com/chatbot/controller/SimilarityTestController.java::testSearch()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `9f5cf10`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::getChatMode()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `75fed10`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::getDocumentId()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `9f5cf10`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::setChatMode()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `75fed10`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::setDocumentId()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `9f5cf10`.
- `backend/src/main/java/com/chatbot/impl/DocumentServiceImpl.java::calculateSha256()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `9f5cf10`.
- `backend/src/main/java/com/chatbot/impl/DocumentServiceImpl.java::preview()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `9b23108`.
- `backend/src/main/java/com/chatbot/impl/DocumentServiceImpl.java::uploadDocument()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `7243ca7`.
- `backend/src/main/java/com/chatbot/service/chunk/EmbeddingTextBuilder.java::build()` — Inputs/configuration ko target DTO/prompt/chunk/bucket object me construct karta hai. Git evidence: `9b23108`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::buildSegments()` — Inputs/configuration ko target DTO/prompt/chunk/bucket object me construct karta hai. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::chooseSeparator()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::chunk()` — `StreamDownstreamEvent` ka `chunk` envelope construct karta hai with correlation fields. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::chunkText()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `18de8ee, 8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::clamp()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::collapseWhitespace()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::countWords()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::emitPackedChunk()` — Inputs/configuration ko target DTO/prompt/chunk/bucket object me construct karta hai. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::endsWithListItem()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::estimatePageNumber()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::flushMergeBuffer()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::hardSplit()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::indexOfFrom()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::isHeading()` — Boolean predicate/guard hai jo branch invariant ya eligibility check karta hai. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::isListItem()` — Boolean predicate/guard hai jo branch invariant ya eligibility check karta hai. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::isTitleCase()` — Boolean predicate/guard hai jo branch invariant ya eligibility check karta hai. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::legacyFixedSizeChunks()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::normalizeWhitespace()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::packSegments()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::packedContentLength()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::paragraphAwareChunks()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::resolveOverlapStart()` — Identifier/context se owner-scoped ya current resource/state resolve karta hai; missing case caller ko propagate hota hai. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::segmentFromBuffer()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::splitBySentenceAndSemicolon()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::splitIntoUnits()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::splitOversized()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java::splitSemicolonClauses()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `8d7d866`.
- `backend/src/main/java/com/chatbot/service/embedding/EmbeddingServiceImpl.java::generateDocumentEmbedding()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `411ee9a`.
- `backend/src/main/java/com/chatbot/service/embedding/EmbeddingServiceImpl.java::generateDocumentEmbeddings()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `ecc8aaa`.
- `backend/src/main/java/com/chatbot/service/embedding/EmbeddingServiceImpl.java::generateEmbedding()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `9f5cf10`.
- `backend/src/main/java/com/chatbot/service/embedding/EmbeddingServiceImpl.java::generateQueryEmbedding()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `411ee9a`.
- `backend/src/main/java/com/chatbot/service/embedding/EmbeddingServiceImpl.java::requestEmbedding()` — Business/state transition execute karta hai, relevant external/repository dependency invoke karke result ya durable side effect deta hai. Git evidence: `411ee9a`.
- `backend/src/main/java/com/chatbot/service/pdf/PdfTextExtractorImpl.java::extractText()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `577e560`.
- `backend/src/main/java/com/chatbot/service/rag/ContextBuilderServiceImpl.java::buildContext()` — Inputs/configuration ko target DTO/prompt/chunk/bucket object me construct karta hai. Git evidence: `9f5cf10, 0617ead`.
- `backend/src/main/java/com/chatbot/service/rag/ContextBuilderServiceImpl.java::normalizeForDedup()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `0617ead`.
- `backend/src/main/java/com/chatbot/service/rag/ContextBuilderServiceImpl.java::resolveDocumentId()` — Identifier/context se owner-scoped ya current resource/state resolve karta hai; missing case caller ko propagate hota hai. Git evidence: `0617ead`.
- `backend/src/main/java/com/chatbot/service/rag/PromptBuilderServiceImpl.java::buildPrompt()` — Inputs/configuration ko target DTO/prompt/chunk/bucket object me construct karta hai. Git evidence: `9f5cf10`.
- `backend/src/main/java/com/chatbot/service/retrieval/Bm25Scorer.java::computeDocumentFrequency()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `411ee9a`.
- `backend/src/main/java/com/chatbot/service/retrieval/Bm25Scorer.java::dedupeTokens()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `12b3e7a`.
- `backend/src/main/java/com/chatbot/service/retrieval/Bm25Scorer.java::expandQuery()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `411ee9a`.
- `backend/src/main/java/com/chatbot/service/retrieval/Bm25Scorer.java::preview()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `75203d1`.
- `backend/src/main/java/com/chatbot/service/retrieval/Bm25Scorer.java::score()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `411ee9a`.
- `backend/src/main/java/com/chatbot/service/retrieval/Bm25Scorer.java::tokenize()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `411ee9a`.
- `backend/src/main/java/com/chatbot/service/similarity/SimilarityServiceImpl.java::cosineSimilarity()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `9f5cf10`.
- `backend/src/main/java/com/chatbot/service/similarity/SimilarityServiceImpl.java::findRelevantChunks()` — Identifier/context se owner-scoped ya current resource/state resolve karta hai; missing case caller ko propagate hota hai. Git evidence: `9f5cf10`.
- `backend/src/main/java/com/chatbot/service/similarity/SimilarityServiceImpl.java::findResultIndex()` — Identifier/context se owner-scoped ya current resource/state resolve karta hai; missing case caller ko propagate hota hai. Git evidence: `411ee9a`.
- `backend/src/main/java/com/chatbot/service/similarity/SimilarityServiceImpl.java::preview()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `75203d1`.
- `backend/src/main/java/com/chatbot/service/similarity/SimilarityServiceImpl.java::resolveKeywordWeight()` — Identifier/context se owner-scoped ya current resource/state resolve karta hai; missing case caller ko propagate hota hai. Git evidence: `75203d1`.
- `backend/src/main/java/com/chatbot/util/CosineSimilarityUtil.java::calculateSimilarity()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `9f5cf10`.
- `frontend/src/ChatApp.jsx::applyChatMode()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `75fed10`.
- `frontend/src/ChatApp.jsx::buildModePayloadFields()` — Inputs/configuration ko target DTO/prompt/chunk/bucket object me construct karta hai. Git evidence: `75fed10`.
- `frontend/src/ChatApp.jsx::handleDocumentUpload()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `9f5cf10`.
- `frontend/src/chatApi.js::uploadDocumentApi()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `9f5cf10`.
- `frontend/src/components/ChatModeToggle.jsx::ChatModeToggle()` — Top-level entry/component function hai jo child state, handlers aur rendering/application bootstrap compose karta hai. Git evidence: `75fed10`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `backend/.gitignore` — 7243ca7:M
- `backend/pom.xml` — 7243ca7:M
- `backend/src/main/java/com/chatbot/config/RestTemplateConfig.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/constant/AppConstants.java` — 7243ca7:M
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java` — 9f5cf10:M, 75203d1:M, 75fed10:M
- `backend/src/main/java/com/chatbot/controller/DocumentController.java` — 7243ca7:A
- `backend/src/main/java/com/chatbot/controller/SimilarityTestController.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java` — 9f5cf10:M, 75fed10:M
- `backend/src/main/java/com/chatbot/dto/DocumentUploadResponse.java` — 7243ca7:A
- `backend/src/main/java/com/chatbot/dto/embedding/EmbeddingRequest.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/dto/embedding/EmbeddingResponse.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/dto/similarity/SimilarityResult.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/impl/DocumentServiceImpl.java` — 7243ca7:A, 577e560:M, 18de8ee:M, 686f82e:M, 9f5cf10:M, 411ee9a:M, 8d7d866:M, 9b23108:M, ecc8aaa:M
- `backend/src/main/java/com/chatbot/model/Document.java` — 7243ca7:A, 9f5cf10:M
- `backend/src/main/java/com/chatbot/model/DocumentChunk.java` — 686f82e:A
- `backend/src/main/java/com/chatbot/model/DocumentChunkEmbedding.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/repository/DocumentChunkEmbeddingRepository.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/repository/DocumentChunkRepository.java` — 686f82e:A
- `backend/src/main/java/com/chatbot/repository/DocumentRepository.java` — 7243ca7:A, 9f5cf10:M
- `backend/src/main/java/com/chatbot/service/DocumentService.java` — 7243ca7:A, 9f5cf10:M
- `backend/src/main/java/com/chatbot/service/OllamaStreamingService.java` — 9f5cf10:M
- `backend/src/main/java/com/chatbot/service/chunk/EmbeddingTextBuilder.java` — 9b23108:A
- `backend/src/main/java/com/chatbot/service/chunk/TextChunk.java` — 8d7d866:A
- `backend/src/main/java/com/chatbot/service/chunk/TextChunkService.java` — 18de8ee:A, 411ee9a:M, 8d7d866:M
- `backend/src/main/java/com/chatbot/service/embedding/EmbeddingService.java` — 9f5cf10:A, 411ee9a:M, ecc8aaa:M
- `backend/src/main/java/com/chatbot/service/embedding/EmbeddingServiceImpl.java` — 9f5cf10:A, 411ee9a:M, ecc8aaa:M
- `backend/src/main/java/com/chatbot/service/pdf/PdfTextExtractor.java` — 577e560:A
- `backend/src/main/java/com/chatbot/service/pdf/PdfTextExtractorImpl.java` — 577e560:A
- `backend/src/main/java/com/chatbot/service/rag/ContextBuilderService.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/service/rag/ContextBuilderServiceImpl.java` — 9f5cf10:A, 0617ead:M
- `backend/src/main/java/com/chatbot/service/rag/PromptBuilderService.java` — 9f5cf10:A
- `backend/src/main/java/com/chatbot/service/rag/PromptBuilderServiceImpl.java` — 9f5cf10:A, c126e4f:M
- `backend/src/main/java/com/chatbot/service/retrieval/Bm25Scorer.java` — 411ee9a:A, 75203d1:M, 12b3e7a:M
- `backend/src/main/java/com/chatbot/service/similarity/SimilarityService.java` — 9f5cf10:A, 75fed10:M
- `backend/src/main/java/com/chatbot/service/similarity/SimilarityServiceImpl.java` — 9f5cf10:A, 411ee9a:M, 75203d1:M, 12b3e7a:M
- `backend/src/main/java/com/chatbot/util/CosineSimilarityUtil.java` — 9f5cf10:A
- `backend/src/main/resources/application.yml` — 7243ca7:M, 9f5cf10:M, 411ee9a:M, 8d7d866:M, 9b23108:M, ecc8aaa:M, 75203d1:M, 0617ead:M
- `backend/src/main/resources/db/migration/V10__create_document_chunks_table.sql` — 686f82e:A
- `backend/src/main/resources/db/migration/V11__create_document_chunk_embeddings_table.sql` — 9f5cf10:A
- `backend/src/main/resources/db/migration/V12__add_file_hash_to_documents.sql` — 14b38eb:A
- `backend/src/main/resources/db/migration/V9__create_documents_table.sql` — 7243ca7:A
- `backend/src/test/java/com/chatbot/dto/ChatStompPayloadChatModeTest.java` — 75fed10:A
- `backend/src/test/java/com/chatbot/service/rag/PromptBuilderServiceImplTest.java` — 75fed10:A
- `frontend/src/App.css` — 75fed10:M
- `frontend/src/ChatApp.jsx` — 9f5cf10:M, 75fed10:M
- `frontend/src/chatApi.js` — 9f5cf10:M
- `frontend/src/components/ChatModeToggle.jsx` — 75fed10:A

# APIs Added/Modified
Controller mapping `/api/documents/upload`; effective `/api/v1/api/documents/upload`; DOCUMENT fields travel over STOMP.

### Endpoint lifecycle: PDF upload
- POST `/api/v1/api/documents/upload`, `multipart/form-data`, field name `file`. `DocumentController.uploadDocument()` manually checks authenticated principal and returns 401 without body if absent.
- Service validates non-empty file, `.pdf` name/content type and configured 25 MB bound; computes SHA-256, checks duplicate, writes generated stored filename, extracts/chunks/embeds and persists metadata/chunks/vectors. Hash uniqueness global hai, per-user nahi; same bytes dusra user bhi re-upload nahi kar sakta.
- Success: 201 `DocumentUploadResponse`. Validation/extraction/Ollama/persistence failures reach global error mapping; no controller-level compensation. Request is synchronous.
- Frontend `chatApi.uploadDocument()` calls endpoint, selected document/mode state builds STOMP payload. DOCUMENT chat is not a separate REST endpoint: `ChatStompPayload.chatMode` plus `documentId` route `ChatWebSocketController.handleChat()` into ownership check, retrieval, bounded context, grounded prompt and normal streaming response path.

### Diagnostic endpoint: similarity test
- `SimilarityTestController.testSearch()` exposes GET `/api/v1/test-search?documentId=...`, calls `findRelevantChunks("What is Redis?", documentId, 3)` and returns fixed text `Similarity Test Completed`.
- Endpoint document ownership verify nahi karta; only global authenticated-route policy applies. Production me ise remove, profile-gate, ya owner/admin authorization dena chahiye.
- Yeh public product search API nahi hai: query hard-coded hai and ranked chunks response me return nahi hote.

### Mode regression and repair
- Merged document implementation through `14b38eb` ne `documentId` mandatory karke every chat ko RAG path me route kiya, jis se normal chat temporarily break hua.
- Tip-only `75fed10` explicit `ChatMode.NORMAL/DOCUMENT` aur tests add karta hai. Main ko related but patch-non-equivalent repair `920edf5` voice lineage se mila; missing mode NORMAL hai, and DOCUMENT requires an owned document ID.

### Tip-only contract tests (`75fed10`)
- `ChatStompPayloadChatModeTest.missingChatMode_isTreatedAsNormalEvenWhenDocumentIdPresent()` prevents implicit document-mode activation.
- `documentMode_requiresExplicitEnum()` proves explicit `DOCUMENT` plus ID contract.
- `normalMode_ignoresDocumentIdAtContractLevel()` proves stale ID alone does not change NORMAL mode.
- `PromptBuilderServiceImplTest.relatedQuestionWithAnswerAbsent_keepsExactUnavailablePhrase()` protects SYSTEM/CONTEXT/QUESTION structure and exact unavailable-answer phrase.
- These tests are on the branch tip and absent from integrated `main`; they do not cover multipart upload, PDF parsing, persistence, Ollama integration or ranking quality.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
- `V9__create_documents_table.sql`: document metadata (`file_name`, generated `stored_file_name`, MIME, size, uploader, timestamp, status) with `uploaded_by -> users(id)`. FK has no `ON DELETE CASCADE`, so user deletion with documents can be blocked unless application handles it.
- `V10__create_document_chunks_table.sql`: ordered `chunk_index`, `TEXT content`, timestamp and `document_id -> documents(id) ON DELETE CASCADE`. Schema does not add unique `(document_id,chunk_index)` or retrieval index.
- `V11__create_document_chunk_embeddings_table.sql`: one row per chunk via `chunk_id BIGINT NOT NULL UNIQUE`, JSONB vector, and cascading FK. JSONB stores vectors but no pgvector/HNSW/IVFFlat index; retrieval loads document vectors and scores in Java.
- `V12__add_file_hash_to_documents.sql`: nullable 64-char SHA-256 column first, named unique constraint guarded by `DO $$`, then explicit precondition abort if any legacy NULL remains, finally `SET NOT NULL`. Migration placeholders invent nahi karta; old PDFs ke original bytes se real backfill required hai.
- Upload checks hash before work and DB uniqueness is final duplicate invariant, but synchronous filesystem copy + multiple DB saves have no method-level transaction/compensation. Concurrent duplicates can leave an orphan file/partial records when unique insert or later Ollama call fails.

# Frontend Changes
Upload selection and ChatModeToggle/payload fields.

# Backend Changes
DocumentService, PDF/chunk/embed/retrieve/context/prompt pipeline.

# Security Changes
Upload/auth required; document ownership checked before retrieval.

# Testing Changes
Tip-only payload/prompt tests; absent from main; no upload/retrieval integration suite.

# Technical Topics Covered
PDFBox, chunking, embeddings, cosine, BM25, hybrid retrieval, prompt grounding

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Synchronous indexing is simple but request-latency/partial-write risk; bounded context reduces prompt size.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Mode leakage and unsafe file-hash migration addressed.

# Edge Cases
25MB/malformed PDF, duplicate race, partial indexing, Ollama failure, no relevant context, prompt injection.

# Production Considerations
## Current implementation
Synchronous indexing is simple but request-latency/partial-write risk; bounded context reduces prompt size.

## Improvement, not currently implemented
Async job/object storage, transaction compensation, pgvector, quality evals and security scanning.

# Interview Topics From This Branch
- Function boundaries: DocumentServiceImpl.uploadDocument, TextChunkService.chunk, EmbeddingServiceImpl.generateDocumentEmbeddings, Bm25Scorer.score, SimilarityServiceImpl.findRelevantChunks, ContextBuilderServiceImpl.buildContext, PromptBuilderServiceImpl.buildPrompt, ChatWebSocketController.handleChat DOCUMENT path, calculateSha256, ChatModeToggle / buildModePayloadFields
- API contract: Controller mapping `/api/documents/upload`; effective `/api/v1/api/documents/upload`; DOCUMENT fields travel over STOMP.
- Persistence: V9 documents; V10 chunks cascade; V11 unique chunk JSONB embedding; V12 unique non-null SHA-256.
- Security: Upload/auth required; document ownership checked before retrieval.
- Failure/edge cases: 25MB/malformed PDF, duplicate race, partial indexing, Ollama failure, no relevant context, prompt injection.
- Separate exhaustive Q&A: `Interview/feature-document-chat.txt`
