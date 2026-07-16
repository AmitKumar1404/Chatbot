package com.chatbot.service.retrieval;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Component
public class Bm25Scorer {

    private static final double K1 = 1.2;
    private static final double B = 0.75;

    private static final Set<String> STOPWORDS = Set.of(
            "a", "an", "and", "are", "as", "at", "be", "by", "for",
            "from", "had", "has", "have", "he", "her", "his", "i",
            "in", "is", "it", "its", "me", "my", "of", "on", "or",
            "she", "that", "the", "their", "them", "they", "this",
            "to", "was", "we", "were", "what", "when", "where",
            "which", "who", "will", "with", "you", "your"
    );

    private static final Map<String, List<String>> SYNONYMS = Map.ofEntries(
            Map.entry("qualification", List.of("qualification", "degree", "education")),
            Map.entry("educational", List.of("educational", "education", "degree")),
            Map.entry("degree", List.of("degree", "qualification", "education")),
            Map.entry("master", List.of("master", "mca", "postgraduate")),
            Map.entry("mca", List.of("mca", "master", "postgraduate")),
            Map.entry("bachelor", List.of("bachelor", "bca", "graduation")),
            Map.entry("bca", List.of("bca", "bachelor", "graduation")),
            Map.entry("company", List.of("company", "employer", "work")),
            Map.entry("employer", List.of("employer", "company", "work")),
            Map.entry("work", List.of("work", "company", "employer")),
            Map.entry("job", List.of("job", "work", "company", "employer"))
    );

    public double[] score(String query, List<String> documents) {

        if (query == null || query.isBlank() || documents == null || documents.isEmpty()) {
            return new double[documents == null ? 0 : documents.size()];
        }

        String expandedQuery = expandQuery(query);
        List<String> queryTokens = tokenize(expandedQuery);

        System.out.println();
        System.out.println("Original query: " + query);
        System.out.println("Expanded BM25 query: " + expandedQuery);
        System.out.println("BM25 tokens: " + queryTokens);

        if (queryTokens.isEmpty()) {
            return new double[documents.size()];
        }

        List<List<String>> documentTokens = new ArrayList<>(documents.size());
        for (String document : documents) {
            documentTokens.add(tokenize(document));
        }

        int documentCount = documentTokens.size();
        Map<String, Integer> documentFrequency = computeDocumentFrequency(documentTokens);

        double averageDocumentLength = documentTokens.stream()
                .mapToInt(List::size)
                .average()
                .orElse(0.0);

        if (averageDocumentLength == 0.0) {
            return new double[documentCount];
        }

        double[] scores = new double[documentCount];

        for (int i = 0; i < documentCount; i++) {

            List<String> tokens = documentTokens.get(i);
            int documentLength = tokens.size();

            if (documentLength == 0) {
                scores[i] = 0.0;
                continue;
            }

            Map<String, Integer> termFrequency = new HashMap<>();
            for (String token : tokens) {
                termFrequency.merge(token, 1, Integer::sum);
            }

            double documentScore = 0.0;

            for (String term : queryTokens) {

                Integer frequency = termFrequency.get(term);
                if (frequency == null) {
                    continue;
                }

                int df = documentFrequency.getOrDefault(term, 0);
                double idf = Math.log(1.0 + (documentCount - df + 0.5) / (df + 0.5));

                double numerator = frequency * (K1 + 1.0);
                double denominator = frequency
                        + K1 * (1.0 - B + B * documentLength / averageDocumentLength);

                documentScore += idf * numerator / denominator;
            }

            scores[i] = documentScore;
        }

        return scores;
    }

    private String expandQuery(String query) {

        String normalized = query.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .trim();

        if (normalized.isEmpty()) {
            return query;
        }

        LinkedHashSet<String> expandedTerms = new LinkedHashSet<>();

        for (String word : normalized.split("\\s+")) {
            if (word.isBlank()) {
                continue;
            }

            List<String> synonyms = SYNONYMS.get(word);
            if (synonyms != null) {
                expandedTerms.addAll(synonyms);
            } else {
                expandedTerms.add(word);
            }
        }

        return String.join(" ", expandedTerms);
    }

    private Map<String, Integer> computeDocumentFrequency(List<List<String>> documentTokens) {

        Map<String, Integer> documentFrequency = new HashMap<>();

        for (List<String> tokens : documentTokens) {
            Set<String> uniqueTerms = new HashSet<>(tokens);
            for (String term : uniqueTerms) {
                documentFrequency.merge(term, 1, Integer::sum);
            }
        }

        return documentFrequency;
    }

    private List<String> tokenize(String text) {

        String normalized = text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .trim();

        if (normalized.isEmpty()) {
            return List.of();
        }

        List<String> tokens = new ArrayList<>();

        for (String token : normalized.split("\\s+")) {
            if (!token.isBlank() && !STOPWORDS.contains(token)) {
                tokens.add(token);
            }
        }

        return tokens;
    }
}
