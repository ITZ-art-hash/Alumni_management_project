package com.alumni.platform.controller;

import com.alumni.platform.entity.Alumni;
import com.alumni.platform.entity.User;
import com.alumni.platform.repository.UserRepository;
import com.alumni.platform.service.AlumniService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final int PAGE_SIZE = 10;

    private final AlumniService alumniService;
    private final UserRepository userRepository;

    public AdminController(AlumniService alumniService, UserRepository userRepository) {
        this.alumniService = alumniService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Alumni> all = alumniService.findAll();

        Map<String, Long> perDepartment = all.stream()
                .collect(Collectors.groupingBy(Alumni::getDepartment, Collectors.counting()));

        model.addAttribute("totalAlumni", all.size());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalDepartments", perDepartment.size());
        model.addAttribute("deptLabels", perDepartment.keySet());
        model.addAttribute("deptCounts", perDepartment.values());

        return "admin/dashboard";
    }

    @GetMapping("/pending-users")
    public String pendingUsers(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("email"));
        Page<User> result = userRepository.findByApprovedFalse(pageable);
        model.addAttribute("pendingUsers", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        return "admin/pending-users";
    }

    @PostMapping("/users/{id}/approve")
    public String approveUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setApproved(true);
        userRepository.save(user);
        return "redirect:/admin/pending-users";
    }
}
