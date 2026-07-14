package com.chatbot.service.similarity;

import com.chatbot.dto.similarity.SimilarityResult;
import com.chatbot.model.DocumentChunk;
import com.chatbot.model.DocumentChunkEmbedding;
import com.chatbot.repository.DocumentChunkEmbeddingRepository;
import com.chatbot.service.embedding.EmbeddingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SimilarityServiceImpl implements SimilarityService {

    private final EmbeddingService embeddingService;

    private final DocumentChunkEmbeddingRepository embeddingRepository;

    public SimilarityServiceImpl(
            EmbeddingService embeddingService,
            DocumentChunkEmbeddingRepository embeddingRepository) {

        this.embeddingService = embeddingService;
        this.embeddingRepository = embeddingRepository;
    }

    @Override
    public List<DocumentChunk> findRelevantChunks(
            String question,
            Long documentId,
            int topK) {

        // Generate embedding for user question
        List<Float> queryEmbedding =
                embeddingService.generateEmbedding(question);

        System.out.println();
        System.out.println("==================================");
        System.out.println("QUESTION");
        System.out.println(question);
        System.out.println("Embedding Size : " + queryEmbedding.size());
        System.out.println("==================================");

        // Read all stored embeddings
//        List<DocumentChunkEmbedding> storedEmbeddings =
//                embeddingRepository.findAll();
        List<DocumentChunkEmbedding> storedEmbeddings =
                embeddingRepository.findByChunk_Document_Id(documentId);

        System.out.println();
        System.out.println("==============================");
        System.out.println("Document Id : " + documentId);
        System.out.println("Embeddings Loaded : " + storedEmbeddings.size());
        System.out.println("==============================");

        // Store similarity results
        List<SimilarityResult> similarityResults =
                new ArrayList<>();

        for (DocumentChunkEmbedding storedEmbedding : storedEmbeddings) {

            List<Float> storedVector =
                    storedEmbedding.getEmbedding();

            double similarity =
                    cosineSimilarity(
                            queryEmbedding,
                            storedVector
                    );

            System.out.println();
            System.out.println("----------------------------");
            System.out.println(
                    "Chunk Id : "
                            + storedEmbedding.getChunk().getId());

            System.out.println(
                    "Stored Vector Size : "
                            + storedVector.size());

            System.out.println(
                    "Similarity Score : "
                            + similarity);

            SimilarityResult result =
                    SimilarityResult.builder()
                            .chunk(storedEmbedding.getChunk())
                            .score(similarity)
                            .build();

            similarityResults.add(result);
        }

        // Sort descending by similarity score
        similarityResults.sort(
                Comparator.comparingDouble(
                        SimilarityResult::getScore
                ).reversed()
        );

        System.out.println();
        System.out.println("========== SORTED RESULTS ==========");

        for (SimilarityResult result : similarityResults) {

            System.out.println(
                    "Chunk "
                            + result.getChunk().getId()
                            + " -> "
                            + result.getScore()
            );
        }

        // Pick top K chunks
        List<DocumentChunk> relevantChunks =
                new ArrayList<>();

        for (int i = 0;
             i < Math.min(topK, similarityResults.size());
             i++) {

            relevantChunks.add(
                    similarityResults
                            .get(i)
                            .getChunk()
            );
        }

        System.out.println();
        System.out.println("========== TOP " + topK + " CHUNKS ==========");

        for (DocumentChunk chunk : relevantChunks) {

            System.out.println(
                    "Chunk Id : "
                            + chunk.getId()
            );

            System.out.println(
                    "Chunk Index : "
                            + chunk.getChunkIndex()
            );

            System.out.println(
                    "Content : "
                            + chunk.getContent()
            );

            System.out.println("------------------------------------");
        }

        return relevantChunks;
    }

    private double cosineSimilarity(
            List<Float> vector1,
            List<Float> vector2) {

        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException(
                    "Embedding dimensions do not match."
            );
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vector1.size(); i++) {

            dotProduct += vector1.get(i) * vector2.get(i);

            normA += vector1.get(i) * vector1.get(i);

            normB += vector2.get(i) * vector2.get(i);
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct /
                (Math.sqrt(normA) * Math.sqrt(normB));
    }
}