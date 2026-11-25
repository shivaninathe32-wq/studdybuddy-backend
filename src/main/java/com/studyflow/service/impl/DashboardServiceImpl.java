package com.studyflow.service.impl;

import com.studyflow.dto.assignment.AssignmentResponse;
import com.studyflow.dto.dashboard.DashboardSummaryResponse;
import com.studyflow.dto.dashboard.SubjectDistributionResponse;
import com.studyflow.dto.dashboard.WeeklyProgressResponse;
import com.studyflow.dto.session.StudySessionResponse;
import com.studyflow.entity.*;
import com.studyflow.exception.ResourceNotFoundException;
import com.studyflow.mapper.AssignmentMapper;
import com.studyflow.mapper.StudySessionMapper;
import com.studyflow.repository.*;
import com.studyflow.security.SecurityUtils;
import com.studyflow.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final StudySessionRepository studySessionRepository;
    private final TaskRepository taskRepository;
    private final AssignmentRepository assignmentRepository;
    private final PomodoroSessionRepository pomodoroSessionRepository;
    private final StudySessionMapper studySessionMapper;
    private final AssignmentMapper assignmentMapper;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse summary() {
        Long userId = SecurityUtils.currentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        LocalDate today = LocalDate.now();

        double todayHours = studySessionRepository.findByUserIdAndDateAndCompletedTrue(userId, today).stream()
                .mapToDouble(StudySessionMapper::durationHours)
                .sum();
        List<Subject> subjects = subjectRepository.findByUserIdOrderByNameAsc(userId);
        double dailyGoal = subjects.stream()
                .map(Subject::getTargetHoursPerWeek)
                .filter(h -> h != null && h > 0)
                .mapToDouble(h -> h / 7.0)
                .sum();

        List<StudySessionResponse> todaySchedule = studySessionRepository
                .findByUserIdAndDateOrderByStartTimeAsc(userId, today)
                .stream()
                .map(studySessionMapper::toResponse)
                .toList();

        List<AssignmentResponse> upcoming = assignmentRepository
                .findTop5ByUserIdAndSubmissionStatusNotAndDueDateTimeAfterOrderByDueDateTimeAsc(
                        userId, SubmissionStatus.SUBMITTED, LocalDateTime.now())
                .stream()
                .map(assignmentMapper::toResponse)
                .toList();

        return DashboardSummaryResponse.builder()
                .fullName(user.getFullName())
                .date(today)
                .totalSubjects(subjectRepository.countByUserId(userId))
                .todayStudyHours(round2(todayHours))
                .pendingTasks(taskRepository.countByUserIdAndStatus(userId, TaskStatus.PENDING)
                        + taskRepository.countByUserIdAndStatus(userId, TaskStatus.IN_PROGRESS))
                .completedTasks(taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED))
                .upcomingDeadlines(upcoming)
                .dailyStudyGoal(round2(dailyGoal))
                .todaySchedule(todaySchedule)
                .recentActivity(buildRecentActivity(userId))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public WeeklyProgressResponse weeklyProgress() {
        Long userId = SecurityUtils.currentUserId();
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(6);

        List<StudySession> sessions = studySessionRepository
                .findByUserIdAndDateBetweenOrderByDateAscStartTimeAsc(userId, start, end);

        Map<LocalDate, Double> hoursByDate = sessions.stream()
                .filter(StudySession::isCompleted)
                .collect(Collectors.groupingBy(
                        StudySession::getDate,
                        Collectors.summingDouble(StudySessionMapper::durationHours)));

        List<WeeklyProgressResponse.DayHours> days = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            days.add(WeeklyProgressResponse.DayHours.builder()
                    .date(d)
                    .hours(round2(hoursByDate.getOrDefault(d, 0.0)))
                    .build());
        }

        return WeeklyProgressResponse.builder().days(days).build();
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectDistributionResponse subjectDistribution() {
        Long userId = SecurityUtils.currentUserId();
        LocalDate from = LocalDate.now().minusDays(30);
        Map<Long, SubjectDistributionResponse.SubjectHours> aggregated = new java.util.LinkedHashMap<>();

        for (StudySession session : studySessionRepository
                .findByUserIdAndCompletedTrueAndDateBetween(userId, from, LocalDate.now())) {
            Long subjectId = session.getSubject().getId();
            double hours = StudySessionMapper.durationHours(session);
            aggregated.merge(subjectId,
                    SubjectDistributionResponse.SubjectHours.builder()
                            .subjectId(subjectId)
                            .subjectName(session.getSubject().getName())
                            .hours(hours)
                            .build(),
                    (a, b) -> SubjectDistributionResponse.SubjectHours.builder()
                            .subjectId(a.getSubjectId())
                            .subjectName(a.getSubjectName())
                            .hours(round2(a.getHours() + b.getHours()))
                            .build());
        }

        List<SubjectDistributionResponse.SubjectHours> subjects = aggregated.values().stream()
                .map(s -> SubjectDistributionResponse.SubjectHours.builder()
                        .subjectId(s.getSubjectId())
                        .subjectName(s.getSubjectName())
                        .hours(round2(s.getHours()))
                        .build())
                .sorted(Comparator.comparingDouble(SubjectDistributionResponse.SubjectHours::getHours).reversed())
                .toList();

        return SubjectDistributionResponse.builder().subjects(subjects).build();
    }

    private List<DashboardSummaryResponse.ActivityItem> buildRecentActivity(Long userId) {
        List<DashboardSummaryResponse.ActivityItem> items = new ArrayList<>();

        studySessionRepository.findTop5ByUserIdOrderByUpdatedAtDesc(userId).forEach(s ->
                items.add(DashboardSummaryResponse.ActivityItem.builder()
                        .type("STUDY_SESSION")
                        .title(s.getSubject().getName())
                        .detail(s.getStudyGoal() != null ? s.getStudyGoal() : "Study session")
                        .timestamp(s.getUpdatedAt())
                        .build()));

        taskRepository.findTop5ByUserIdOrderByUpdatedAtDesc(userId).forEach(t ->
                items.add(DashboardSummaryResponse.ActivityItem.builder()
                        .type("TASK")
                        .title(t.getTitle())
                        .detail(t.getStatus().name())
                        .timestamp(t.getUpdatedAt())
                        .build()));

        assignmentRepository.findTop5ByUserIdOrderByUpdatedAtDesc(userId).forEach(a ->
                items.add(DashboardSummaryResponse.ActivityItem.builder()
                        .type("ASSIGNMENT")
                        .title(a.getTitle())
                        .detail(a.getSubmissionStatus().name())
                        .timestamp(a.getUpdatedAt())
                        .build()));

        pomodoroSessionRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId).forEach(p ->
                items.add(DashboardSummaryResponse.ActivityItem.builder()
                        .type("POMODORO")
                        .title(p.getMode().name())
                        .detail(p.getPlannedDurationMinutes() + " min")
                        .timestamp(p.getCreatedAt())
                        .build()));

        return items.stream()
                .sorted(Comparator.comparing(DashboardSummaryResponse.ActivityItem::getTimestamp).reversed())
                .limit(10)
                .toList();
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
