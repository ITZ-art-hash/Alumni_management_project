package com.alumni.platform.controller;

import com.alumni.platform.entity.Event;
import com.alumni.platform.service.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

@Controller
@RequestMapping("/events")
public class EventController {

    private static final int PAGE_SIZE = 10;

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("eventDate"));
        Page<Event> result = eventService.findAll(pageable);
        model.addAttribute("events", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        return "events/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("event", new Event());
        return "events/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String save(@Valid @ModelAttribute("event") Event event, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "events/form";
        }
        eventService.save(event);
        return "redirect:/events";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Authentication authentication, Model model) {
        Event event = eventService.findById(id).orElseThrow();
        model.addAttribute("event", event);
        model.addAttribute("attending", event.getAttendees().contains(authentication.getName()));
        return "events/view";
    }

    @PostMapping("/{id}/rsvp")
    public String rsvp(@PathVariable Long id, Authentication authentication) {
        eventService.rsvp(id, authentication.getName());
        return "redirect:/events/" + id;
    }

    @PostMapping("/{id}/cancel-rsvp")
    public String cancelRsvp(@PathVariable Long id, Authentication authentication) {
        eventService.cancelRsvp(id, authentication.getName());
        return "redirect:/events/" + id;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        eventService.delete(id);
        return "redirect:/events";
    }
}
