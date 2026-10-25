package com.alumni.platform.controller;

import com.alumni.platform.entity.Alumni;
import com.alumni.platform.service.AlumniService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/alumni")
public class AlumniController {

    private static final int PAGE_SIZE = 10;

    private final AlumniService alumniService;

    public AlumniController(AlumniService alumniService) {
        this.alumniService = alumniService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Integer batch,
                        @RequestParam(required = false) String department,
                        @RequestParam(required = false) String skill,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("name"));
        String departmentFilter = (department != null && !department.isBlank()) ? department : null;
        String skillFilter = (skill != null && !skill.isBlank()) ? skill : null;
        Page<Alumni> result = alumniService.search(batch, departmentFilter, skillFilter, pageable);

        model.addAttribute("alumniList", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("batch", batch);
        model.addAttribute("department", department);
        model.addAttribute("skill", skill);
        return "alumni/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=alumni.csv");
        PrintWriter writer = response.getWriter();
        writer.println("Name,Email,Batch,Department,Company,Skills,LinkedIn,Phone");
        for (Alumni a : alumniService.findAll()) {
            writer.printf("%s,%s,%d,%s,%s,%s,%s,%s%n",
                    csv(a.getName()), csv(a.getEmail()), a.getBatch(), csv(a.getDepartment()),
                    csv(a.getCompany()), csv(a.getSkills()), csv(a.getLinkedin()), csv(a.getPhone()));
        }
        writer.flush();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("alumni", new Alumni());
        return "alumni/form";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        String email = authentication.getName();
        var existing = alumniService.findByEmail(email);
        if (existing.isPresent()) {
            return "redirect:/alumni/" + existing.get().getId() + "/edit";
        }
        Alumni alumni = new Alumni();
        alumni.setEmail(email);
        model.addAttribute("alumni", alumni);
        return "alumni/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("alumni") Alumni alumni, BindingResult bindingResult,
                        Authentication authentication, RedirectAttributes redirectAttributes) {
        if (!isAdmin(authentication)) {
            String email = authentication.getName();
            if (alumni.getId() != null) {
                Alumni existing = alumniService.findById(alumni.getId())
                        .orElseThrow(() -> new AccessDeniedException("Profile not found"));
                if (!existing.getEmail().equalsIgnoreCase(email)) {
                    throw new AccessDeniedException("You can only edit your own profile");
                }
            } else if (alumniService.findByEmail(email).isPresent()) {
                throw new AccessDeniedException("You already have a profile");
            }
            alumni.setEmail(email);
        }

        if (bindingResult.hasErrors()) {
            return "alumni/form";
        }

        try {
            alumniService.save(alumni);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "That email is already used by another alumni record.");
            return alumni.getId() != null ? "redirect:/alumni/" + alumni.getId() + "/edit" : "redirect:/alumni/profile";
        }
        return "redirect:/alumni";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Authentication authentication, Model model) {
        Alumni alumni = alumniService.findById(id).orElseThrow();
        model.addAttribute("alumni", alumni);
        model.addAttribute("canManage", isAdmin(authentication) || alumni.getEmail().equalsIgnoreCase(authentication.getName()));
        return "alumni/view";
    }

    @PostMapping("/{id}/photo")
    public String uploadPhoto(@PathVariable Long id, @RequestParam("photo") MultipartFile photo,
                               Authentication authentication) throws IOException {
        Alumni alumni = alumniService.findById(id).orElseThrow();
        if (!isAdmin(authentication) && !alumni.getEmail().equalsIgnoreCase(authentication.getName())) {
            throw new AccessDeniedException("You can only update your own photo");
        }
        if (!photo.isEmpty()) {
            alumni.setPhoto(photo.getBytes());
            alumni.setPhotoContentType(photo.getContentType());
            alumniService.save(alumni);
        }
        return "redirect:/alumni/" + id;
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> viewPhoto(@PathVariable Long id) {
        Alumni alumni = alumniService.findById(id).orElseThrow();
        if (alumni.getPhoto() == null) {
            return ResponseEntity.notFound().build();
        }
        MediaType mediaType = alumni.getPhotoContentType() != null
                ? MediaType.parseMediaType(alumni.getPhotoContentType())
                : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok().contentType(mediaType).body(alumni.getPhoto());
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Authentication authentication, Model model) {
        Alumni alumni = alumniService.findById(id).orElseThrow();
        if (!isAdmin(authentication) && !alumni.getEmail().equalsIgnoreCase(authentication.getName())) {
            throw new AccessDeniedException("You can only edit your own profile");
        }
        model.addAttribute("alumni", alumni);
        return "alumni/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        alumniService.delete(id);
        return "redirect:/alumni";
    }

    @GetMapping("/mentors")
    public String mentors(@RequestParam(required = false) String skill,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {
        boolean hasSkill = skill != null && !skill.isBlank();
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("name"));
        Page<Alumni> result = hasSkill
                ? alumniService.findMentorsBySkill(skill, pageable)
                : Page.empty(pageable);

        model.addAttribute("mentors", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("skill", skill);
        return "alumni/mentors";
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private String csv(String value) {
        return "\"" + (value == null ? "" : value.replace("\"", "\"\"")) + "\"";
    }
}
