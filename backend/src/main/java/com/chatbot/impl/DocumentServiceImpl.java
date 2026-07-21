package com.chatbot.impl;

import com.chatbot.dto.DocumentUploadResponse;
import com.chatbot.model.Document;
import com.chatbot.model.User;
import com.chatbot.repository.DocumentRepository;
import com.chatbot.repository.UserRepository;
import com.chatbot.service.DocumentService;
import com.chatbot.service.pdf.PdfTextExtractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.chatbot.service.chunk.EmbeddingTextBuilder;
import com.chatbot.service.chunk.TextChunk;
import com.chatbot.service.chunk.TextChunkService;
import java.util.List;
import com.chatbot.model.DocumentChunk;
import com.chatbot.repository.DocumentChunkRepository;
import com.chatbot.service.embedding.EmbeddingService;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.chatbot.model.DocumentChunkEmbedding;
import com.chatbot.repository.DocumentChunkEmbeddingRepository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class DocumentServiceImpl implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    // 25 MB
    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${app.rag.embedding.log-previews:true}")
    private boolean logPreviews;

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final PdfTextExtractor pdfTextExtractor;
    private final TextChunkService textChunkService;
    private final DocumentChunkRepository documentChunkRepository;
    private final EmbeddingService embeddingService;
    private final DocumentChunkEmbeddingRepository documentChunkEmbeddingRepository;
    private final EmbeddingTextBuilder embeddingTextBuilder;

    public DocumentServiceImpl(
            DocumentRepository documentRepository,
            UserRepository userRepository,
            PdfTextExtractor pdfTextExtractor,
            TextChunkService textChunkService,
            DocumentChunkRepository documentChunkRepository,
            EmbeddingService embeddingService,
            DocumentChunkEmbeddingRepository documentChunkEmbeddingRepository,
            EmbeddingTextBuilder embeddingTextBuilder) {

        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.pdfTextExtractor = pdfTextExtractor;
        this.textChunkService = textChunkService;
        this.documentChunkRepository = documentChunkRepository;
        this.embeddingService = embeddingService;
        this.documentChunkEmbeddingRepository = documentChunkEmbeddingRepository;
        this.embeddingTextBuilder = embeddingTextBuilder;
    }

    @Override
    public DocumentUploadResponse uploadDocument(
            MultipartFile file,
            String username) {

        // 1. Validate file
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload a PDF file.");
        }

        // 2. Validate filename
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null ||
                !originalFileName.toLowerCase().endsWith(".pdf")) {

            throw new RuntimeException("Only PDF files are allowed.");
        }

        // 3. Validate content type
        if (!MediaType.APPLICATION_PDF_VALUE.equals(file.getContentType())) {

            throw new RuntimeException("Invalid PDF content type.");
        }

        // 4. Validate file size (25 MB)
        if (file.getSize() > MAX_FILE_SIZE) {

            throw new RuntimeException("Maximum allowed file size is 25 MB.");
        }

        // 5. Generate SHA-256 hash
        String fileHash = calculateSha256(file);

        // 6. Check duplicate document
        if (documentRepository.findByFileHash(fileHash).isPresent()) {

            throw new RuntimeException("This document has already been uploaded.");
        }

        // 7. Fetch logged-in user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found."));

        // 5.1 Check duplicate document
//        Optional<Document> existingDocument =
//                documentRepository.findByFileName(originalFileName);
//
//        if (existingDocument.isPresent()) {
//            throw new RuntimeException("Document already uploaded.");
//        }

        // 6. Create upload directory
        Path uploadPath = Paths.get(uploadDir);

        long totalStart = System.nanoTime();
        log.info("Upload started: file={}, sizeBytes={}, user={}",
                originalFileName, file.getSize(), username);

        try {

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 7. Generate unique filename
            String storedFileName = UUID.randomUUID() + ".pdf";

            // 8. Destination path
            Path destination = uploadPath.resolve(storedFileName);

            // 9. Save file
            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // 10. Extract PDF text
            long extractionStart = System.nanoTime();
            String extractedText =
                    pdfTextExtractor.extractText(destination.toFile());
            long extractionMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - extractionStart);

            log.info("PDF extraction completed: file={}, textLength={}, durationMs={}",
                    originalFileName,
                    extractedText == null ? 0 : extractedText.length(),
                    extractionMs);

            // 11. Split extracted text into chunks (metadata kept in memory)
            long chunkingStart = System.nanoTime();
            List<TextChunk> chunks = textChunkService.chunk(extractedText);
            long chunkingMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - chunkingStart);

            log.info("Chunking completed: file={}, chunkCount={}, durationMs={}",
                    originalFileName, chunks.size(), chunkingMs);

            List<TextChunk> validChunks = new ArrayList<>();
            List<String> embeddingInputs = new ArrayList<>();

            for (TextChunk chunk : chunks) {

                String chunkText = chunk.getContent();

                if (chunkText == null
                        || chunkText.isBlank()
                        || chunkText.trim().length() < 10) {
                    log.warn("Skipping invalid chunk: index={}, length={}",
                            chunk.getChunkIndex(),
                            chunkText == null ? 0 : chunkText.length());
                    continue;
                }

                String embeddingInput = embeddingTextBuilder.build(chunk);

                if (embeddingInput == null || embeddingInput.isBlank()) {
                    log.warn("Skipping chunk with blank embedding input: index={}",
                            chunk.getChunkIndex());
                    continue;
                }

                if (logPreviews) {
                    log.info(
                            "Chunk preview: index={}, heading={}, storedLength={}, embeddingInputLength={}, storedPreview={}, embeddingPreview={}",
                            chunk.getChunkIndex(),
                            chunk.getSectionHeading(),
                            chunkText.length(),
                            embeddingInput.length(),
                            preview(chunkText, 140),
                            preview(embeddingInput, 140)
                    );
                }

                validChunks.add(chunk);
                embeddingInputs.add(embeddingInput);
            }

            if (validChunks.isEmpty()) {
                throw new RuntimeException(
                        "No valid text chunks could be indexed from the uploaded PDF."
                );
            }

            // Generate embeddings in batch (heading-aware input; stored content stays unchanged)
            long embeddingStart = System.nanoTime();
            List<List<Float>> embeddings =
                    embeddingService.generateDocumentEmbeddings(embeddingInputs);
            long embeddingMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - embeddingStart);

            log.info("Embedding generation completed: file={}, vectorCount={}, durationMs={}",
                    originalFileName, embeddings.size(), embeddingMs);

            // 12. Save metadata in database
            long persistenceStart = System.nanoTime();

            Document document = Document.builder()
                    .fileName(originalFileName)
                    .storedFileName(storedFileName)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .fileHash(fileHash)
                    .uploadedBy(user)
                    .uploadedAt(LocalDateTime.now())
                    .status("UPLOADED")
                    .build();

            Document savedDocument = documentRepository.save(document);

            int persistedCount = 0;

            for (int i = 0; i < validChunks.size(); i++) {

                TextChunk chunk = validChunks.get(i);
                List<Float> embedding =
                        i < embeddings.size() ? embeddings.get(i) : null;

                if (embedding == null || embedding.isEmpty()) {
                    log.warn("Skipping chunk persistence due to missing embedding: index={}",
                            chunk.getChunkIndex());
                    continue;
                }

                // Persist content + chunkIndex only (other TextChunk fields are future-ready)
                DocumentChunk savedChunk = documentChunkRepository.save(
                        DocumentChunk.builder()
                                .document(savedDocument)
                                .chunkIndex(chunk.getChunkIndex())
                                .content(chunk.getContent())
                                .createdAt(LocalDateTime.now())
                                .build()
                );
                documentChunkEmbeddingRepository.save(
                        DocumentChunkEmbedding.builder()
                                .chunk(savedChunk)
                                .embedding(embedding)
                                .createdAt(LocalDateTime.now())
                                .build()
                );

                persistedCount++;
            }

            long persistenceMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - persistenceStart);

            if (persistedCount == 0) {
                throw new RuntimeException(
                        "Failed to persist any chunk embeddings for the uploaded PDF."
                );
            }

            if (persistedCount < validChunks.size()) {
                log.warn("Persisted {} of {} chunks successfully.",
                        persistedCount, validChunks.size());
            }

            long totalMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - totalStart);

            log.info(
                    "Upload Summary | file={} | documentId={} | Extraction={} ms | Chunking={} ms | Embedding={} ms | Database={} ms | Total={} ms | PersistedChunks={}",
                    originalFileName,
                    savedDocument.getId(),
                    extractionMs,
                    chunkingMs,
                    embeddingMs,
                    persistenceMs,
                    totalMs,
                    persistedCount
            );

            log.info("Upload completed: documentId={}, file={}",
                    savedDocument.getId(), originalFileName);

            // 13. Return response
            return DocumentUploadResponse.builder()
                    .id(savedDocument.getId())
                    .fileName(savedDocument.getFileName())
                    .uploadedAt(savedDocument.getUploadedAt())
                    .status(savedDocument.getStatus())
                    .build();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to save uploaded file.",
                    e
            );
        }
    }

    private String preview(String text, int maxLength) {

        if (text == null) {
            return "";
        }

        String flattened = text.replace('\n', ' ').trim();
        if (flattened.length() <= maxLength) {
            return flattened;
        }

        return flattened.substring(0, maxLength) + "...";
    }

    private String calculateSha256(MultipartFile file) {

        try {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = messageDigest.digest(file.getBytes());

            StringBuilder hexString = new StringBuilder();

            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();

        } catch (IOException | NoSuchAlgorithmException e) {

            throw new RuntimeException(
                    "Failed to generate file hash.",
                    e
            );
        }
    }
}
