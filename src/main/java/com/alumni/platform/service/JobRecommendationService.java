package com.alumni.platform.service;

import com.alumni.platform.entity.Alumni;
import com.alumni.platform.entity.JobPosting;
import com.alumni.platform.ml.CosineSimilarity;
import com.alumni.platform.ml.TfIdfVectorizer;
import com.alumni.platform.repository.JobPostingRepository;
import com.alumni.platform.service.dto.JobMatch;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Content-based job recommendation engine.
 *
 * <p>Every open job posting is represented as a TF-IDF vector built from its title and
 * description; an alumni's skills profile is projected into the same vector space. Jobs are
 * ranked by the cosine similarity between the two vectors, so postings whose text most closely
 * matches the alumni's skills surface first - a classic content-based filtering approach, no
 * external model or API required.
 */
@Service
public class JobRecommendationService {

    private static final int MIN_MATCH_PERCENT = 1;

    private final JobPostingRepository jobPostingRepository;

    public JobRecommendationService(JobPostingRepository jobPostingRepository) {
        this.jobPostingRepository = jobPostingRepository;
    }

    public List<JobMatch> recommendFor(Alumni alumni, int topN) {
        List<JobPosting> jobs = jobPostingRepository.findAll();
        if (jobs.isEmpty()) {
            return List.of();
        }

        TfIdfVectorizer vectorizer = TfIdfVectorizer.fit(jobs.stream().map(this::documentFor).toList());

        Map<String, Double> alumniVector = vectorizer.transform(profileTextFor(alumni));
        if (alumniVector.isEmpty()) {
            return List.of();
        }

        return jobs.stream()
                .map(job -> toMatch(job, alumniVector, vectorizer))
                .filter(match -> match.matchPercent() >= MIN_MATCH_PERCENT)
                .sorted(Comparator.comparingInt(JobMatch::matchPercent).reversed())
                .limit(topN)
                .toList();
    }

    private JobMatch toMatch(JobPosting job, Map<String, Double> alumniVector, TfIdfVectorizer vectorizer) {
        Map<String, Double> jobVector = vectorizer.transform(documentFor(job));
        double similarity = CosineSimilarity.of(alumniVector, jobVector);
        int matchPercent = (int) Math.round(similarity * 100);
        List<String> matchedKeywords = alumniVector.keySet().stream()
                .filter(jobVector::containsKey)
                .sorted()
                .toList();
        return new JobMatch(job, matchPercent, matchedKeywords);
    }

    /** Title is repeated to weight it more heavily than the body of the description. */
    private String documentFor(JobPosting job) {
        String title = job.getTitle() == null ? "" : job.getTitle();
        String description = job.getDescription() == null ? "" : job.getDescription();
        return title + " " + title + " " + description;
    }

    private String profileTextFor(Alumni alumni) {
        String skills = alumni.getSkills() == null ? "" : alumni.getSkills();
        String department = alumni.getDepartment() == null ? "" : alumni.getDepartment();
        return skills + " " + skills + " " + department;
    }
}
