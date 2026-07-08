package com.chatbot.controller;

import com.chatbot.constant.ResponseCode;
import com.chatbot.dto.DocumentUploadResponse;
import com.chatbot.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import static com.chatbot.constant.AppConstants.DOCUMENT_BASE_PATH;

@RestController
@RequestMapping(DOCUMENT_BASE_PATH)
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )    public ResponseEntity<DocumentUploadResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(ResponseCode.UNAUTHORIZED).build();
        }

        DocumentUploadResponse response = documentService.uploadDocument(
                file,
                authentication.getName()
        );

        return ResponseEntity.status(ResponseCode.CREATED).body(response);
    }
}