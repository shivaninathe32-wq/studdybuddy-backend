package com.studyflow.repository;

import com.studyflow.entity.StudySession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    Optional<StudySession> findByIdAndUserId(Long id, Long userId);

    Page<StudySession> findByUserId(Long userId, Pageable pageable);

    Page<StudySession> findByUserIdAndDate(Long userId, LocalDate date, Pageable pageable);

    Page<StudySession> findByUserIdAndDateBetween(
            Long userId, LocalDate fromDate, LocalDate toDate, Pageable pageable);

    Page<StudySession> findByUserIdAndDateGreaterThanEqual(
            Long userId, LocalDate fromDate, Pageable pageable);

    Page<StudySession> findByUserIdAndDateLessThanEqual(
            Long userId, LocalDate toDate, Pageable pageable);

    List<StudySession> findByUserIdAndDateOrderByStartTimeAsc(Long userId, LocalDate date);

    List<StudySession> findByUserIdAndDateAndCompletedTrue(Long userId, LocalDate date);

    @Query("""
            SELECT s FROM StudySession s
            WHERE s.user.id = :userId
              AND s.date = :date
              AND s.id <> :excludeId
              AND s.startTime < :endTime
              AND s.endTime > :startTime
            """)
    List<StudySession> findOverlappingExcluding(
            @Param("userId") Long userId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId);

    @Query("""
            SELECT s FROM StudySession s
            WHERE s.user.id = :userId
              AND s.date = :date
              AND s.startTime < :endTime
              AND s.endTime > :startTime
            """)
    List<StudySession> findOverlapping(
            @Param("userId") Long userId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    List<StudySession> findByUserIdAndDateBetweenOrderByDateAscStartTimeAsc(
            Long userId, LocalDate from, LocalDate to);

    List<StudySession> findByUserIdAndCompletedTrueAndDateBetween(
            Long userId, LocalDate from, LocalDate to);

    List<StudySession> findTop5ByUserIdOrderByUpdatedAtDesc(Long userId);
}
