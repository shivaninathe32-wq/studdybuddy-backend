package com.studyflow.service.impl;

import com.studyflow.dto.pomodoro.PomodoroSessionRequest;
import com.studyflow.dto.pomodoro.PomodoroSessionResponse;
import com.studyflow.dto.pomodoro.PomodoroSummaryResponse;
import com.studyflow.entity.*;
import com.studyflow.exception.ResourceNotFoundException;
import com.studyflow.mapper.PomodoroMapper;
import com.studyflow.repository.PomodoroSessionRepository;
import com.studyflow.repository.SubjectRepository;
import com.studyflow.repository.TaskRepository;
import com.studyflow.repository.UserRepository;
import com.studyflow.security.SecurityUtils;
import com.studyflow.service.PomodoroService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PomodoroServiceImpl implements PomodoroService {

    private final PomodoroSessionRepository pomodoroSessionRepository;
    private final SubjectRepository subjectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final PomodoroMapper pomodoroMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<PomodoroSessionResponse> list(Pageable pageable) {
        return pomodoroSessionRepository.findByUserIdOrderByCreatedAtDesc(SecurityUtils.currentUserId(), pageable)
                .map(pomodoroMapper::toResponse);
    }

    @Override
    @Transactional
    public PomodoroSessionResponse create(PomodoroSessionRequest request) {
        Long userId = SecurityUtils.currentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean completed = request.getCompleted() == null || request.getCompleted();
        PomodoroSession session = PomodoroSession.builder()
                .user(user)
                .subject(resolveSubject(request.getSubjectId(), userId))
                .task(resolveTask(request.getTaskId(), userId))
                .mode(request.getMode())
                .plannedDurationMinutes(request.getPlannedDurationMinutes())
                .actualDurationMinutes(request.getActualDurationMinutes() != null
                        ? request.getActualDurationMinutes()
                        : request.getPlannedDurationMinutes())
                .completedAt(completed ? Instant.now() : null)
                .build();

        return pomodoroMapper.toResponse(pomodoroSessionRepository.save(session));
    }

    @Override
    @Transactional(readOnly = true)
    public PomodoroSummaryResponse summary() {
        Long userId = SecurityUtils.currentUserId();
        long focusSessions = pomodoroSessionRepository
                .countByUserIdAndModeAndCompletedAtIsNotNull(userId, PomodoroMode.FOCUS);
        long shortBreaks = pomodoroSessionRepository
                .countByUserIdAndModeAndCompletedAtIsNotNull(userId, PomodoroMode.SHORT_BREAK);
        long longBreaks = pomodoroSessionRepository
                .countByUserIdAndModeAndCompletedAtIsNotNull(userId, PomodoroMode.LONG_BREAK);
        long focusMinutes = nullToZero(pomodoroSessionRepository
                .sumCompletedMinutes(userId, PomodoroMode.FOCUS));
        long shortMinutes = nullToZero(pomodoroSessionRepository
                .sumCompletedMinutes(userId, PomodoroMode.SHORT_BREAK));
        long longMinutes = nullToZero(pomodoroSessionRepository
                .sumCompletedMinutes(userId, PomodoroMode.LONG_BREAK));

        return PomodoroSummaryResponse.builder()
                .totalFocusSessions(focusSessions)
                .totalShortBreaks(shortBreaks)
                .totalLongBreaks(longBreaks)
                .totalFocusMinutes(focusMinutes)
                .totalBreakMinutes(shortMinutes + longMinutes)
                .build();
    }

    private long nullToZero(Long value) {
        return value == null ? 0L : value;
    }

    private Subject resolveSubject(Long subjectId, Long userId) {
        if (subjectId == null) {
            return null;
        }
        return subjectRepository.findByIdAndUserId(subjectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
    }

    private Task resolveTask(Long taskId, Long userId) {
        if (taskId == null) {
            return null;
        }
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }
}
