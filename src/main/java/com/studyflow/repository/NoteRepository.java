package com.studyflow.repository;

import com.studyflow.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Optional<Note> findByIdAndUserId(Long id, Long userId);

    @Query("""
            SELECT n FROM Note n
            WHERE n.user.id = :userId
              AND (:subjectId IS NULL OR n.subject.id = :subjectId)
              AND (:pinned IS NULL OR n.pinned = :pinned)
              AND (:pattern IS NULL
                   OR LOWER(n.title) LIKE :pattern
                   OR LOWER(n.content) LIKE :pattern)
            """)
    Page<Note> findFiltered(
            @Param("userId") Long userId,
            @Param("subjectId") Long subjectId,
            @Param("pinned") Boolean pinned,
            @Param("pattern") String pattern,
            Pageable pageable);
}
