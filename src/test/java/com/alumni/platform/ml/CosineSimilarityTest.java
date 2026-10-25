package com.alumni.platform.ml;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class CosineSimilarityTest {

    @Test
    void identicalVectorsHaveSimilarityOfOne() {
        Map<String, Double> vector = Map.of("java", 0.8, "spring", 0.6);

        assertThat(CosineSimilarity.of(vector, vector)).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void orthogonalVectorsHaveSimilarityOfZero() {
        Map<String, Double> a = Map.of("java", 1.0);
        Map<String, Double> b = Map.of("python", 1.0);

        assertThat(CosineSimilarity.of(a, b)).isZero();
    }

    @Test
    void emptyVectorsAreHandledSafely() {
        assertThat(CosineSimilarity.of(Map.of(), Map.of("java", 1.0))).isZero();
        assertThat(CosineSimilarity.of(Map.of(), Map.of())).isZero();
    }

    @Test
    void partialOverlapProducesAValueBetweenZeroAndOne() {
        Map<String, Double> a = Map.of("java", 1.0, "spring", 1.0);
        Map<String, Double> b = Map.of("java", 1.0, "python", 1.0);

        double similarity = CosineSimilarity.of(a, b);

        assertThat(similarity).isGreaterThan(0.0).isLessThan(1.0);
    }
}
