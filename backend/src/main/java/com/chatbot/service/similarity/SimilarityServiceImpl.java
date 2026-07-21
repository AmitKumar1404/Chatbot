package com.chatbot.service.similarity;

import com.chatbot.dto.similarity.SimilarityResult;
import com.chatbot.model.DocumentChunk;
import com.chatbot.model.DocumentChunkEmbedding;
import com.chatbot.repository.DocumentChunkEmbeddingRepository;
import com.chatbot.service.embedding.EmbeddingService;
import com.chatbot.service.retrieval.Bm25Scorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class SimilarityServiceImpl implements SimilarityService {

    private static final Logger log = LoggerFactory.getLogger(SimilarityServiceImpl.class);

    private final EmbeddingService embeddingService;

    private final DocumentChunkEmbeddingRepository embeddingRepository;

    private final Bm25Scorer bm25Scorer;

    @Value("${app.rag.hybrid.enabled:true}")
    private boolean hybridEnabled;

    @Value("${app.rag.retrieval.vector-weight:${app.rag.hybrid.dense-weight:0.8}}")
    private double vectorWeight;

    @Value("${app.rag.retrieval.keyword-weight:-1}")
    private double keywordWeightProperty;

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

        long startNano = System.nanoTime();

        double resolvedKeywordWeight = resolveKeywordWeight();
        boolean useKeyword = resolvedKeywordWeight > 0.0;

        log.info(
                "Retrieval started: documentId={}, queryPreview={}",
                documentId,
                preview(question, 120)
        );

        if (log.isDebugEnabled()) {
            log.debug(
                    "Retrieval weights: vectorWeight={}, keywordWeight={}",
                    vectorWeight,
                    resolvedKeywordWeight
            );
        }

        List<Float> queryEmbedding =
                embeddingService.generateQueryEmbedding(question);

        List<DocumentChunkEmbedding> storedEmbeddings =
                embeddingRepository.findByChunk_Document_Id(documentId);

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

        double[] bm25Scores = useKeyword
                ? bm25Scorer.score(question, chunkContents)
                : new double[chunkContents.size()];

        double maxCosineScore = 0.0;
        for (double cosineScore : cosineScores) {
            maxCosineScore = Math.max(maxCosineScore, cosineScore);
        }

        double maxBm25Score = 0.0;
        if (useKeyword) {
            for (double bm25Score : bm25Scores) {
                maxBm25Score = Math.max(maxBm25Score, bm25Score);
            }
        }

        // All-zero BM25 contributes nothing; skip keyword fusion/normalization.
        boolean applyKeyword = useKeyword && maxBm25Score > 0.0;

        Map<Long, double[]> scoresByChunkId = new HashMap<>();

        for (int i = 0; i < similarityResults.size(); i++) {

            DocumentChunk chunk = similarityResults.get(i).getChunk();
            double cosineScore = cosineScores.get(i);
            double bm25Score = bm25Scores[i];

            double normalizedVectorScore = maxCosineScore > 0.0
                    ? cosineScore / maxCosineScore
                    : 0.0;
            double normalizedKeywordScore = applyKeyword
                    ? bm25Score / maxBm25Score
                    : 0.0;

            double hybridScore;
            if (!useKeyword) {
                // Preserve pure-vector path when keyword weight is disabled.
                hybridScore = cosineScore;
            } else if (applyKeyword) {
                hybridScore = (vectorWeight * normalizedVectorScore)
                        + (resolvedKeywordWeight * normalizedKeywordScore);
            } else {
                // Keyword enabled but all BM25 scores are zero.
                hybridScore = vectorWeight * normalizedVectorScore;
            }

            similarityResults.get(i).setScore(hybridScore);
            scoresByChunkId.put(
                    chunk.getId(),
                    new double[]{
                            cosineScore,
                            normalizedVectorScore,
                            normalizedKeywordScore,
                            hybridScore
                    }
            );
        }

        similarityResults.sort((left, right) -> {
            double[] leftScores = scoresByChunkId.get(left.getChunk().getId());
            double[] rightScores = scoresByChunkId.get(right.getChunk().getId());

            int byHybrid = Double.compare(rightScores[3], leftScores[3]);
            if (byHybrid != 0) {
                return byHybrid;
            }

            int byCosine = Double.compare(rightScores[0], leftScores[0]);
            if (byCosine != 0) {
                return byCosine;
            }

            return Integer.compare(
                    left.getChunk().getChunkIndex(),
                    right.getChunk().getChunkIndex()
            );
        });

        List<DocumentChunk> relevantChunks = new ArrayList<>();

        for (int i = 0;
             i < Math.min(topK, similarityResults.size());
             i++) {

            relevantChunks.add(similarityResults.get(i).getChunk());
        }

        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNano);

        StringBuilder topIds = new StringBuilder();
        StringBuilder topScoreSummary = new StringBuilder();

        for (int i = 0; i < relevantChunks.size(); i++) {
            DocumentChunk chunk = relevantChunks.get(i);
            double[] scores = scoresByChunkId.get(chunk.getId());

            if (i > 0) {
                topIds.append(',');
                topScoreSummary.append("; ");
            }

            topIds.append(chunk.getId());
            topScoreSummary.append("id=")
                    .append(chunk.getId())
                    .append(" vector=")
                    .append(scores != null ? scores[1] : 0.0)
                    .append(" keyword=")
                    .append(scores != null ? scores[2] : 0.0)
                    .append(" hybrid=")
                    .append(scores != null ? scores[3] : 0.0);
        }

        log.info(
                "Retrieval completed: documentId={}, durationMs={}, candidateCount={}, returnedCount={}, topChunks=[{}]",
                documentId,
                durationMs,
                similarityResults.size(),
                relevantChunks.size(),
                topIds
        );

        if (log.isDebugEnabled()) {
            log.debug(
                    "Retrieval top chunk scores: documentId={}, topChunks=[{}]",
                    documentId,
                    topScoreSummary
            );
        }

        return relevantChunks;
    }

    private double resolveKeywordWeight() {

        if (keywordWeightProperty >= 0.0) {
            return keywordWeightProperty;
        }

        if (!hybridEnabled) {
            return 0.0;
        }

        return Math.max(0.0, 1.0 - vectorWeight);
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
