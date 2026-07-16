package com.chatbot.service.similarity;

import com.chatbot.dto.similarity.SimilarityResult;
import com.chatbot.model.DocumentChunk;
import com.chatbot.model.DocumentChunkEmbedding;
import com.chatbot.repository.DocumentChunkEmbeddingRepository;
import com.chatbot.service.embedding.EmbeddingService;
import com.chatbot.service.retrieval.Bm25Scorer;
import org.springframework.beans.factory.annotation.Value;
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

    private final Bm25Scorer bm25Scorer;

    @Value("${app.rag.hybrid.enabled:true}")
    private boolean hybridEnabled;

    @Value("${app.rag.hybrid.dense-weight:0.7}")
    private double denseWeight;

    public SimilarityServiceImpl(
            EmbeddingService embeddingService,
            DocumentChunkEmbeddingRepository embeddingRepository,
            Bm25Scorer bm25Scorer) {

        this.embeddingService = embeddingService;
        this.embeddingRepository = embeddingRepository;
        this.bm25Scorer = bm25Scorer;
    }

    @Override
    public List<DocumentChunk> findRelevantChunks(
            String question,
            Long documentId,
            int topK) {

        List<Float> queryEmbedding =
                embeddingService.generateQueryEmbedding(question);

        System.out.println();
        System.out.println("==================================");
        System.out.println("QUESTION");
        System.out.println(question);
        System.out.println("Embedding Size : " + queryEmbedding.size());
        System.out.println("Hybrid Enabled : " + hybridEnabled);
        System.out.println("Dense Weight   : " + denseWeight);
        System.out.println("==================================");

        List<DocumentChunkEmbedding> storedEmbeddings =
                embeddingRepository.findByChunk_Document_Id(documentId);

        System.out.println();
        System.out.println("==============================");
        System.out.println("Document Id : " + documentId);
        System.out.println("Embeddings Loaded : " + storedEmbeddings.size());
        System.out.println("==============================");

        List<SimilarityResult> similarityResults = new ArrayList<>();
        List<String> chunkContents = new ArrayList<>();
        List<Double> cosineScores = new ArrayList<>();

        for (DocumentChunkEmbedding storedEmbedding : storedEmbeddings) {

            DocumentChunk chunk = storedEmbedding.getChunk();
            List<Float> storedVector = storedEmbedding.getEmbedding();

            double cosineScore = cosineSimilarity(queryEmbedding, storedVector);

            chunkContents.add(chunk.getContent());
            cosineScores.add(cosineScore);

            similarityResults.add(
                    SimilarityResult.builder()
                            .chunk(chunk)
                            .score(cosineScore)
                            .build()
            );
        }

        double[] bm25Scores = hybridEnabled
                ? bm25Scorer.score(question, chunkContents)
                : new double[chunkContents.size()];

        double maxBm25Score = 0.0;
        if (hybridEnabled) {
            for (double bm25Score : bm25Scores) {
                maxBm25Score = Math.max(maxBm25Score, bm25Score);
            }
        }

        for (int i = 0; i < similarityResults.size(); i++) {

            DocumentChunk chunk = similarityResults.get(i).getChunk();
            double cosineScore = cosineScores.get(i);
            double bm25Score = bm25Scores[i];
            double normalizedBm25Score = hybridEnabled && maxBm25Score > 0.0
                    ? bm25Score / maxBm25Score
                    : 0.0;

            double finalScore = hybridEnabled
                    ? (denseWeight * cosineScore) + ((1.0 - denseWeight) * normalizedBm25Score)
                    : cosineScore;

            similarityResults.get(i).setScore(finalScore);

            System.out.println();
            System.out.println("----------------------------");
            System.out.println("Chunk Id : " + chunk.getId());
            System.out.println("Chunk Index : " + chunk.getChunkIndex());
            System.out.println("Cosine Score : " + cosineScore);

            if (hybridEnabled) {
                System.out.println("BM25 Score : " + bm25Score);
                System.out.println("Normalized BM25 Score : " + normalizedBm25Score);
                System.out.println("Final Score : " + finalScore);
            }
        }

        similarityResults.sort(
                Comparator.comparingDouble(SimilarityResult::getScore).reversed()
        );

        System.out.println();
        if (hybridEnabled) {
            System.out.println("========== FINAL SORTED RESULTS ==========");
        } else {
            System.out.println("========== SORTED RESULTS ==========");
        }

        for (SimilarityResult result : similarityResults) {

            if (hybridEnabled) {
                int index = findResultIndex(storedEmbeddings, result.getChunk().getId());
                double cosineScore = cosineScores.get(index);
                double bm25Score = bm25Scores[index];
                double normalizedBm25Score = maxBm25Score > 0.0
                        ? bm25Score / maxBm25Score
                        : 0.0;

                System.out.println(
                        "Chunk "
                                + result.getChunk().getId()
                                + " -> Final="
                                + result.getScore()
                                + " Cosine="
                                + cosineScore
                                + " BM25="
                                + bm25Score
                                + " NormalizedBM25="
                                + normalizedBm25Score
                );
            } else {
                System.out.println(
                        "Chunk "
                                + result.getChunk().getId()
                                + " -> "
                                + result.getScore()
                );
            }
        }

        List<DocumentChunk> relevantChunks = new ArrayList<>();

        for (int i = 0;
             i < Math.min(topK, similarityResults.size());
             i++) {

            relevantChunks.add(similarityResults.get(i).getChunk());
        }

        System.out.println();
        System.out.println("========== TOP " + topK + " CHUNKS ==========");

        for (DocumentChunk chunk : relevantChunks) {

            System.out.println("Chunk Id : " + chunk.getId());
            System.out.println("Chunk Index : " + chunk.getChunkIndex());
            System.out.println("Content : " + chunk.getContent());
            System.out.println("------------------------------------");
        }

        return relevantChunks;
    }

    private int findResultIndex(
            List<DocumentChunkEmbedding> storedEmbeddings,
            Long chunkId) {

        for (int i = 0; i < storedEmbeddings.size(); i++) {
            if (storedEmbeddings.get(i).getChunk().getId().equals(chunkId)) {
                return i;
            }
        }

        return 0;
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
