package com.chatbot.repository;

import com.chatbot.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

//    Optional<Document> findByFileName(String fileName);

    Optional<Document> findByFileHash(String fileHash);

    boolean existsByIdAndUploadedBy_Username(Long id, String username);
}