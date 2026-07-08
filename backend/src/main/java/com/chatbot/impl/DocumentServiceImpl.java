package com.chatbot.impl;

import com.chatbot.dto.DocumentUploadResponse;
import com.chatbot.model.Document;
import com.chatbot.model.User;
import com.chatbot.repository.DocumentRepository;
import com.chatbot.repository.UserRepository;
import com.chatbot.service.DocumentService;

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

@Service
public class DocumentServiceImpl implements DocumentService {

    // 25 MB
    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    public DocumentServiceImpl(
            DocumentRepository documentRepository,
            UserRepository userRepository) {

        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
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

        // 5. Fetch logged-in user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found."));

        // 6. Create upload directory
        Path uploadPath = Paths.get(uploadDir);

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

            // 10. Save metadata in database
            Document document = Document.builder()
                    .fileName(originalFileName)
                    .storedFileName(storedFileName)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .uploadedBy(user)
                    .uploadedAt(LocalDateTime.now())
                    .status("UPLOADED")
                    .build();

            Document savedDocument = documentRepository.save(document);

            // 11. Return response
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
}