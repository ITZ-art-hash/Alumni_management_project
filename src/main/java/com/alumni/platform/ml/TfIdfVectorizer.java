package com.alumni.platform.ml;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Minimal TF-IDF (term frequency - inverse document frequency) vectorizer.
 * Fits a vocabulary and IDF weights from a corpus of documents, then converts
 * any text into a sparse bag-of-words vector weighted by term importance.
 */
public final class TfIdfVectorizer {

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from", "has", "he",
            "in", "is", "it", "its", "of", "on", "or", "our", "that", "the", "this", "to",
            "us", "was", "we", "were", "will", "with", "you", "your", "looking", "role",
            "roles", "experience", "year", "years", "team", "work", "job", "candidate",
            "candidates", "opening", "openings", "referral", "available"
    );

    private final Map<String, Double> idf;

    private TfIdfVectorizer(Map<String, Double> idf) {
        this.idf = idf;
    }

    /** Learns vocabulary + IDF weights from the given corpus. */
    public static TfIdfVectorizer fit(List<String> corpus) {
        Map<String, Integer> documentFrequency = new HashMap<>();
        for (String document : corpus) {
            for (String term : new HashSet<>(tokenize(document))) {
                documentFrequency.merge(term, 1, Integer::sum);
            }
        }

        int documentCount = corpus.size();
        Map<String, Double> idf = new HashMap<>();
        documentFrequency.forEach((term, df) ->
                idf.put(term, Math.log((1.0 + documentCount) / (1.0 + df)) + 1.0));

        return new TfIdfVectorizer(idf);
    }

    /** Transforms text into a sparse TF-IDF vector using the fitted vocabulary. */
    public Map<String, Double> transform(String text) {
        List<String> tokens = tokenize(text);
        if (tokens.isEmpty()) {
            return Map.of();
        }

        Map<String, Long> termFrequency = tokens.stream()
                .collect(Collectors.groupingBy(t -> t, Collectors.counting()));

        Map<String, Double> vector = new HashMap<>();
        termFrequency.forEach((term, count) -> {
            Double termIdf = idf.get(term);
            if (termIdf != null) {
                double tf = count.doubleValue() / tokens.size();
                vector.put(term, tf * termIdf);
            }
        });
        return vector;
    }

    static List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return Arrays.stream(text.toLowerCase().split("[^a-z0-9+#]+"))
                .filter(token -> token.length() > 1 && !STOP_WORDS.contains(token))
                .toList();
    }
}
