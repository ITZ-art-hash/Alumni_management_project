package com.alumni.platform.service;

import com.alumni.platform.entity.JobPosting;
import com.alumni.platform.repository.JobPostingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;

    public JobPostingService(JobPostingRepository jobPostingRepository) {
        this.jobPostingRepository = jobPostingRepository;
    }

    public JobPosting save(JobPosting jobPosting) {
        return jobPostingRepository.save(jobPosting);
    }

    public List<JobPosting> findAll() {
        return jobPostingRepository.findAll();
    }

    public Page<JobPosting> findAll(Pageable pageable) {
        return jobPostingRepository.findAll(pageable);
    }

    public Optional<JobPosting> findById(Long id) {
        return jobPostingRepository.findById(id);
    }

    public void delete(Long id) {
        jobPostingRepository.deleteById(id);
    }
}
