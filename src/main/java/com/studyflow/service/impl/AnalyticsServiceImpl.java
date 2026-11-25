package com.studyflow.service.impl;

import com.studyflow.dto.analytics.DailyProductivityResponse;
import com.studyflow.dto.analytics.TaskCompletionResponse;
import com.studyflow.entity.PomodoroMode;
import com.studyflow.entity.PomodoroSession;
import com.studyflow.entity.StudySession;
import com.studyflow.entity.TaskStatus;
import com.studyflow.mapper.StudySessionMapper;
import com.studyflow.repository.PomodoroSessionRepository;
import com.studyflow.repository.StudySessionRepository;
import com.studyflow.repository.TaskRepository;
import com.studyflow.security.SecurityUtils;
import com.studyflow.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final StudySessionRepository studySessionRepository;
    private final TaskRepository taskRepository;
    private final PomodoroSessionRepository pomodoroSessionRepository;

    @Override
    @Transactional(readOnly = true)
    public DailyProductivityResponse dailyProductivity(int days) {
        int window = Math.max(1, Math.min(days, 90));
        Long userId = SecurityUtils.currentUserId();
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(window - 1L);

        List<StudySession> sessions = studySessionRepository
                .findByUserIdAndDateBetweenOrderByDateAscStartTimeAsc(userId, start, end);
        Map<LocalDate, Double> hoursByDate = sessions.stream()
                .filter(StudySession::isCompleted)
                .collect(Collectors.groupingBy(
                        StudySession::getDate,
                        Collectors.summingDouble(StudySessionMapper::durationHours)));

        Instant rangeStart = start.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant rangeEnd = end.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        List<PomodoroSession> focusSessions = pomodoroSessionRepository
                .findCompletedBetween(userId, PomodoroMode.FOCUS, rangeStart, rangeEnd);

        Map<LocalDate, Long> focusByDate = new HashMap<>();
        for (PomodoroSession session : focusSessions) {
            if (session.getCompletedAt() == null) {
                continue;
            }
            LocalDate day = session.getCompletedAt().atZone(ZoneOffset.UTC).toLocalDate();
            long minutes = session.getActualDurationMinutes() != null
                    ? session.getActualDurationMinutes()
                    : session.getPlannedDurationMinutes();
            focusByDate.merge(day, minutes, Long::sum);
        }

        List<DailyProductivityResponse.DayPoint> points = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            points.add(DailyProductivityResponse.DayPoint.builder()
                    .date(d)
                    .studyHours(round2(hoursByDate.getOrDefault(d, 0.0)))
                    .completedTasks(0)
                    .focusMinutes(focusByDate.getOrDefault(d, 0L))
                    .build());
        }

        return DailyProductivityResponse.builder().points(points).build();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskCompletionResponse taskCompletion() {
        Long userId = SecurityUtils.currentUserId();
        Map<TaskStatus, Long> counts = new EnumMap<>(TaskStatus.class);
        for (TaskStatus status : TaskStatus.values()) {
            counts.put(status, 0L);
        }
        for (Object[] row : taskRepository.countByStatus(userId)) {
            counts.put((TaskStatus) row[0], (Long) row[1]);
        }

        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        long completed = counts.getOrDefault(TaskStatus.COMPLETED, 0L);

        List<TaskCompletionResponse.StatusCount> byStatus = counts.entrySet().stream()
                .map(e -> TaskCompletionResponse.StatusCount.builder()
                        .status(e.getKey())
                        .count(e.getValue())
                        .build())
                .toList();

        return TaskCompletionResponse.builder()
                .byStatus(byStatus)
                .total(total)
                .completionRate(total == 0 ? 0.0 : round2((completed * 100.0) / total))
                .build();
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
