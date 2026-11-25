package com.studyflow.repository;

import com.studyflow.entity.PomodoroMode;
import com.studyflow.entity.PomodoroSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PomodoroSessionRepository extends JpaRepository<PomodoroSession, Long> {

    Optional<PomodoroSession> findByIdAndUserId(Long id, Long userId);

    Page<PomodoroSession> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    long countByUserIdAndModeAndCompletedAtIsNotNull(Long userId, PomodoroMode mode);

    @Query("""
            SELECT COALESCE(SUM(COALESCE(p.actualDurationMinutes, p.plannedDurationMinutes)), 0)
            FROM PomodoroSession p
            WHERE p.user.id = :userId
              AND p.mode = :mode
              AND p.completedAt IS NOT NULL
            """)
    Long sumCompletedMinutes(
            @Param("userId") Long userId,
            @Param("mode") PomodoroMode mode);

    @Query("""
            SELECT COALESCE(SUM(COALESCE(p.actualDurationMinutes, p.plannedDurationMinutes)), 0)
            FROM PomodoroSession p
            WHERE p.user.id = :userId
              AND p.mode = :mode
              AND p.completedAt IS NOT NULL
              AND p.completedAt >= :from
              AND p.completedAt < :to
            """)
    Long sumCompletedMinutesBetween(
            @Param("userId") Long userId,
            @Param("mode") PomodoroMode mode,
            @Param("from") Instant from,
            @Param("to") Instant to);

    @Query("""
            SELECT p FROM PomodoroSession p
            WHERE p.user.id = :userId
              AND p.mode = :mode
              AND p.completedAt IS NOT NULL
              AND p.completedAt >= :from
              AND p.completedAt < :to
            """)
    List<PomodoroSession> findCompletedBetween(
            @Param("userId") Long userId,
            @Param("mode") PomodoroMode mode,
            @Param("from") Instant from,
            @Param("to") Instant to);

    List<PomodoroSession> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);
}
