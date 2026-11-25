package com.studyflow.repository;

import com.studyflow.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Page<Subject> findByUserId(Long userId, Pageable pageable);

    @Query("""
            SELECT s FROM Subject s
            WHERE s.user.id = :userId
              AND (LOWER(s.name) LIKE :pattern
                   OR LOWER(COALESCE(s.code, '')) LIKE :pattern
                   OR LOWER(COALESCE(s.description, '')) LIKE :pattern)
            """)
    Page<Subject> searchByUserId(
            @Param("userId") Long userId,
            @Param("pattern") String pattern,
            Pageable pageable);

    Optional<Subject> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);

    List<Subject> findByUserIdOrderByNameAsc(Long userId);
}
