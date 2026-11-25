package com.studyflow.repository;

import com.studyflow.entity.Assignment;
import com.studyflow.entity.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    Optional<Assignment> findByIdAndUserId(Long id, Long userId);

    @Query("""
            SELECT a FROM Assignment a
            WHERE a.user.id = :userId
              AND (:status IS NULL OR a.submissionStatus = :status)
              AND (:upcoming = false OR (a.submissionStatus <> com.studyflow.entity.SubmissionStatus.SUBMITTED
                   AND a.dueDateTime IS NOT NULL AND a.dueDateTime >= :now))
              AND (:overdue = false OR (a.submissionStatus <> com.studyflow.entity.SubmissionStatus.SUBMITTED
                   AND a.dueDateTime IS NOT NULL AND a.dueDateTime < :now))
              AND (:submitted = false OR a.submissionStatus = com.studyflow.entity.SubmissionStatus.SUBMITTED)
            """)
    Page<Assignment> findFiltered(
            @Param("userId") Long userId,
            @Param("status") SubmissionStatus status,
            @Param("upcoming") boolean upcoming,
            @Param("overdue") boolean overdue,
            @Param("submitted") boolean submitted,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    List<Assignment> findTop5ByUserIdAndSubmissionStatusNotAndDueDateTimeAfterOrderByDueDateTimeAsc(
            Long userId, SubmissionStatus status, LocalDateTime after);

    List<Assignment> findTop5ByUserIdOrderByUpdatedAtDesc(Long userId);
}
