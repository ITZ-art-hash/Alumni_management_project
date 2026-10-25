package com.alumni.platform.service;

import com.alumni.platform.entity.Event;
import com.alumni.platform.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Page<Event> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    public void delete(Long id) {
        eventRepository.deleteById(id);
    }

    public void rsvp(Long id, String email) {
        Event event = eventRepository.findById(id).orElseThrow();
        event.getAttendees().add(email);
        eventRepository.save(event);
    }

    public void cancelRsvp(Long id, String email) {
        Event event = eventRepository.findById(id).orElseThrow();
        event.getAttendees().remove(email);
        eventRepository.save(event);
    }
}
