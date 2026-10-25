package com.alumni.platform.service.dto;

import com.alumni.platform.entity.JobPosting;

import java.util.List;

/** A job posting scored against an alumni's skills profile by the recommendation engine. */
public record JobMatch(JobPosting job, int matchPercent, List<String> matchedKeywords) {
}
