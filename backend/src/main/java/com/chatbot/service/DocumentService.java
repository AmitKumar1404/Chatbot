package com.chatbot.service;

import com.chatbot.dto.DocumentUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    DocumentUploadResponse uploadDocument(MultipartFile file, String username);

}