package com.alumni.platform.controller;

import com.alumni.platform.entity.JobPosting;
import com.alumni.platform.service.JobPostingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/jobs")
public class JobPostingController {

    private static final int PAGE_SIZE = 10;

    private final JobPostingService jobPostingService;

    public JobPostingController(JobPostingService jobPostingService) {
        this.jobPostingService = jobPostingService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("postedDate").descending());
        Page<JobPosting> result = jobPostingService.findAll(pageable);
        model.addAttribute("jobs", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        return "jobs/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("jobPosting", new JobPosting());
        return "jobs/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("jobPosting") JobPosting jobPosting, BindingResult bindingResult,
                        Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "jobs/form";
        }
        jobPosting.setPostedByEmail(authentication.getName());
        jobPosting.setPostedDate(LocalDate.now());
        jobPostingService.save(jobPosting);
        return "redirect:/jobs";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Authentication authentication, Model model) {
        JobPosting jobPosting = jobPostingService.findById(id).orElseThrow();
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("jobPosting", jobPosting);
        model.addAttribute("canDelete", admin || jobPosting.getPostedByEmail().equalsIgnoreCase(authentication.getName()));
        return "jobs/view";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication authentication) {
        JobPosting jobPosting = jobPostingService.findById(id).orElseThrow();
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!admin && !jobPosting.getPostedByEmail().equalsIgnoreCase(authentication.getName())) {
            throw new AccessDeniedException("You can only delete your own job posting");
        }
        jobPostingService.delete(id);
        return "redirect:/jobs";
    }
}
