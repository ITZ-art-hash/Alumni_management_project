package com.alumni.platform.ml;

import java.util.Map;

/** Cosine similarity between two sparse vectors, used to score TF-IDF document pairs. */
public final class CosineSimilarity {

    private CosineSimilarity() {
    }

    public static double of(Map<String, Double> a, Map<String, Double> b) {
        if (a.isEmpty() || b.isEmpty()) {
            return 0.0;
        }

        Map<String, Double> smaller = a.size() <= b.size() ? a : b;
        Map<String, Double> larger = a.size() <= b.size() ? b : a;

        double dotProduct = 0.0;
        for (Map.Entry<String, Double> entry : smaller.entrySet()) {
            Double other = larger.get(entry.getKey());
            if (other != null) {
                dotProduct += entry.getValue() * other;
            }
        }

        double magnitude = magnitude(a) * magnitude(b);
        return magnitude == 0.0 ? 0.0 : dotProduct / magnitude;
    }

    private static double magnitude(Map<String, Double> vector) {
        double sumOfSquares = vector.values().stream().mapToDouble(v -> v * v).sum();
        return Math.sqrt(sumOfSquares);
    }
}
