package com.alumni.platform.repository;

import com.alumni.platform.entity.Alumni;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlumniRepository extends JpaRepository<Alumni, Long> {

    List<Alumni> findBySkillsContainingIgnoreCase(String skill);

    Page<Alumni> findBySkillsContainingIgnoreCase(String skill, Pageable pageable);

    Optional<Alumni> findByEmail(String email);

    @Query("""
            SELECT a FROM Alumni a
            WHERE (:batch IS NULL OR a.batch = :batch)
            AND (:department IS NULL OR a.department = :department)
            AND (:skillPattern IS NULL OR LOWER(a.skills) LIKE :skillPattern)
            """)
    Page<Alumni> search(@Param("batch") Integer batch,
                         @Param("department") String department,
                         @Param("skillPattern") String skillPattern,
                         Pageable pageable);
}
