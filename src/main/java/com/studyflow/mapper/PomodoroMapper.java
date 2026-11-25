package com.studyflow.mapper;

import com.studyflow.dto.pomodoro.PomodoroSessionResponse;
import com.studyflow.entity.PomodoroSession;
import org.springframework.stereotype.Component;

@Component
public class PomodoroMapper {

    public PomodoroSessionResponse toResponse(PomodoroSession session) {
        return PomodoroSessionResponse.builder()
                .id(session.getId())
                .subjectId(session.getSubject() != null ? session.getSubject().getId() : null)
                .subjectName(session.getSubject() != null ? session.getSubject().getName() : null)
                .taskId(session.getTask() != null ? session.getTask().getId() : null)
                .taskTitle(session.getTask() != null ? session.getTask().getTitle() : null)
                .mode(session.getMode())
                .plannedDurationMinutes(session.getPlannedDurationMinutes())
                .actualDurationMinutes(session.getActualDurationMinutes())
                .completedAt(session.getCompletedAt())
                .createdAt(session.getCreatedAt())
                .build();
    }
}
