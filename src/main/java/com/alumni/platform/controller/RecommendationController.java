package com.alumni.platform.controller;

import com.alumni.platform.entity.Alumni;
import com.alumni.platform.service.AlumniService;
import com.alumni.platform.service.JobRecommendationService;
import com.alumni.platform.service.dto.JobMatch;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/recommendations")
public class RecommendationController {

    private static final int TOP_N = 10;

    private final AlumniService alumniService;
    private final JobRecommendationService jobRecommendationService;

    public RecommendationController(AlumniService alumniService, JobRecommendationService jobRecommendationService) {
        this.alumniService = alumniService;
        this.jobRecommendationService = jobRecommendationService;
    }

    @GetMapping
    public String recommendations(Authentication authentication, Model model) {
        Optional<Alumni> alumni = alumniService.findByEmail(authentication.getName());
        if (alumni.isEmpty()) {
            model.addAttribute("noProfile", true);
            return "recommendations/list";
        }

        List<JobMatch> matches = jobRecommendationService.recommendFor(alumni.get(), TOP_N);
        model.addAttribute("matches", matches);
        return "recommendations/list";
    }
}
