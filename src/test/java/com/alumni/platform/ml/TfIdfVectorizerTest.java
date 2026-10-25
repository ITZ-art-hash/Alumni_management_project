package com.alumni.platform.ml;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TfIdfVectorizerTest {

    @Test
    void assignsHigherWeightToTermsThatAreRarerAcrossTheCorpus() {
        TfIdfVectorizer vectorizer = TfIdfVectorizer.fit(List.of(
                "Java Spring Boot backend engineer",
                "Java backend engineer with cloud experience",
                "Python data science and machine learning"
        ));

        Map<String, Double> vector = vectorizer.transform("Java Spring backend engineer");

        assertThat(vector).containsKey("spring");
        assertThat(vector).containsKey("java");
        // "spring" appears in 1 of 3 documents, "java" appears in 2 of 3, so spring should
        // carry a higher IDF weight than the more common "java".
        assertThat(vector.get("spring")).isGreaterThan(vector.get("java"));
    }

    @Test
    void ignoresStopWordsAndShortTokens() {
        TfIdfVectorizer vectorizer = TfIdfVectorizer.fit(List.of("We are looking for a Java role", "Python data role"));

        Map<String, Double> vector = vectorizer.transform("We are looking for a Java role");

        assertThat(vector).doesNotContainKeys("we", "are", "for", "a");
    }

    @Test
    void transformOfBlankTextReturnsEmptyVector() {
        TfIdfVectorizer vectorizer = TfIdfVectorizer.fit(List.of("Java Spring backend"));

        assertThat(vectorizer.transform("")).isEmpty();
        assertThat(vectorizer.transform(null)).isEmpty();
    }

    @Test
    void tokenizerKeepsTechTokensWithPlusAndHashSymbols() {
        assertThat(TfIdfVectorizer.tokenize("C++ and C# developer")).contains("c++", "c#");
    }
}
