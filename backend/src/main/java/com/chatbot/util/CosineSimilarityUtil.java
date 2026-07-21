package com.chatbot.util;

import java.util.List;

public final class CosineSimilarityUtil {

    private CosineSimilarityUtil() {
    }

    public static double calculateSimilarity(
            List<Float> vector1,
            List<Float> vector2) {

        if (vector1 == null || vector2 == null) {
            throw new IllegalArgumentException("Vectors cannot be null.");
        }

        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException(
                    "Vectors must have the same dimensions."
            );
        }

        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;

        for (int i = 0; i < vector1.size(); i++) {

            double a = vector1.get(i);
            double b = vector2.get(i);

            dotProduct += a * b;

            magnitude1 += a * a;

            magnitude2 += b * b;
        }

        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);

        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0.0;
        }

        return dotProduct / (magnitude1 * magnitude2);
    }
}