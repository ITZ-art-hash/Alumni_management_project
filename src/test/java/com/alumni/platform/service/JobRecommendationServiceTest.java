package com.alumni.platform.service;

import com.alumni.platform.entity.Alumni;
import com.alumni.platform.entity.JobPosting;
import com.alumni.platform.repository.JobPostingRepository;
import com.alumni.platform.service.dto.JobMatch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobRecommendationServiceTest {

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Test
    void ranksTheMostRelevantJobFirst() {
        JobPosting backendJob = JobPosting.builder().id(1L).title("Backend Engineer")
                .description("Java Spring Boot backend role with cloud deployment experience").build();
        JobPosting mechanicalJob = JobPosting.builder().id(2L).title("Mechanical Design Engineer")
                .description("CAD and SolidWorks expertise for manufacturing").build();
        when(jobPostingRepository.findAll()).thenReturn(List.of(backendJob, mechanicalJob));

        Alumni javaAlumni = Alumni.builder().skills("Java, Spring, Cloud").department("CSE").build();

        JobRecommendationService service = new JobRecommendationService(jobPostingRepository);
        List<JobMatch> matches = service.recommendFor(javaAlumni, 10);

        assertThat(matches).isNotEmpty();
        assertThat(matches.get(0).job().getId()).isEqualTo(1L);
        assertThat(matches.get(0).matchPercent()).isGreaterThan(0);
        // Results must be sorted by descending match strength.
        assertThat(matches).isSortedAccordingTo((a, b) -> b.matchPercent() - a.matchPercent());
    }

    @Test
    void excludesJobsWithNoSkillOverlap() {
        JobPosting mechanicalJob = JobPosting.builder().id(2L).title("Mechanical Design Engineer")
                .description("CAD and SolidWorks expertise for manufacturing").build();
        when(jobPostingRepository.findAll()).thenReturn(List.of(mechanicalJob));

        Alumni javaAlumni = Alumni.builder().skills("Java, Spring, Cloud").department("CSE").build();

        JobRecommendationService service = new JobRecommendationService(jobPostingRepository);
        List<JobMatch> matches = service.recommendFor(javaAlumni, 10);

        assertThat(matches).isEmpty();
    }

    @Test
    void returnsNoRecommendationsWhenThereAreNoJobPostings() {
        when(jobPostingRepository.findAll()).thenReturn(List.of());

        Alumni javaAlumni = Alumni.builder().skills("Java, Spring, Cloud").department("CSE").build();

        JobRecommendationService service = new JobRecommendationService(jobPostingRepository);

        assertThat(service.recommendFor(javaAlumni, 10)).isEmpty();
    }

    @Test
    void respectsTheTopNLimit() {
        List<JobPosting> jobs = List.of(
                JobPosting.builder().id(1L).title("Java Developer").description("Java Spring backend").build(),
                JobPosting.builder().id(2L).title("Java Architect").description("Java Spring cloud architecture").build(),
                JobPosting.builder().id(3L).title("Java Consultant").description("Java Spring enterprise consulting").build()
        );
        when(jobPostingRepository.findAll()).thenReturn(jobs);

        Alumni javaAlumni = Alumni.builder().skills("Java, Spring, Cloud").department("CSE").build();

        JobRecommendationService service = new JobRecommendationService(jobPostingRepository);
        List<JobMatch> matches = service.recommendFor(javaAlumni, 2);

        assertThat(matches).hasSize(2);
    }
}
