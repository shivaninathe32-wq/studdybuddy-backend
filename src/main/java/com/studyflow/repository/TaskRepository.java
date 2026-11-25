package com.studyflow.repository;

import com.studyflow.entity.Priority;
import com.studyflow.entity.Task;
import com.studyflow.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    long countByUserIdAndStatus(Long userId, TaskStatus status);

    @Query("""
            SELECT t FROM Task t
            WHERE t.user.id = :userId
              AND (:status IS NULL OR t.status = :status)
              AND (:priority IS NULL OR t.priority = :priority)
              AND (:subjectId IS NULL OR t.subject.id = :subjectId)
              AND (:pattern IS NULL
                   OR LOWER(t.title) LIKE :pattern
                   OR LOWER(COALESCE(t.description, '')) LIKE :pattern)
            """)
    Page<Task> findFiltered(
            @Param("userId") Long userId,
            @Param("status") TaskStatus status,
            @Param("priority") Priority priority,
            @Param("subjectId") Long subjectId,
            @Param("pattern") String pattern,
            Pageable pageable);

    List<Task> findTop5ByUserIdOrderByUpdatedAtDesc(Long userId);

    @Query("""
            SELECT t.status, COUNT(t) FROM Task t
            WHERE t.user.id = :userId
            GROUP BY t.status
            """)
    List<Object[]> countByStatus(@Param("userId") Long userId);
}
