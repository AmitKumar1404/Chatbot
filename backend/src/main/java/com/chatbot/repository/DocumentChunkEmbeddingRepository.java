package com.chatbot.repository;

import com.chatbot.model.DocumentChunkEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentChunkEmbeddingRepository
        extends JpaRepository<DocumentChunkEmbedding, Long> {

    List<DocumentChunkEmbedding> findByChunk_Document_Id(Long documentId);

}