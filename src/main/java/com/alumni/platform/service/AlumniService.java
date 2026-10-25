package com.alumni.platform.service;

import com.alumni.platform.entity.Alumni;
import com.alumni.platform.repository.AlumniRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlumniService {

    private final AlumniRepository alumniRepository;

    public AlumniService(AlumniRepository alumniRepository) {
        this.alumniRepository = alumniRepository;
    }

    public Alumni save(Alumni alumni) {
        return alumniRepository.save(alumni);
    }

    public List<Alumni> findAll() {
        return alumniRepository.findAll();
    }

    public Optional<Alumni> findById(Long id) {
        return alumniRepository.findById(id);
    }

    public Optional<Alumni> findByEmail(String email) {
        return alumniRepository.findByEmail(email);
    }

    public void delete(Long id) {
        alumniRepository.deleteById(id);
    }

    public Page<Alumni> search(Integer batch, String department, String skill, Pageable pageable) {
        String skillPattern = skill != null ? "%" + skill.toLowerCase() + "%" : null;
        return alumniRepository.search(batch, department, skillPattern, pageable);
    }

    public List<Alumni> findMentorsBySkill(String skill) {
        return alumniRepository.findBySkillsContainingIgnoreCase(skill);
    }

    public Page<Alumni> findMentorsBySkill(String skill, Pageable pageable) {
        return alumniRepository.findBySkillsContainingIgnoreCase(skill, pageable);
    }
}
