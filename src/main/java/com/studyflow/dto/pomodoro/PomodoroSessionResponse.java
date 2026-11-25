package com.studyflow.dto.pomodoro;

import com.studyflow.entity.PomodoroMode;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class PomodoroSessionResponse {
    Long id;
    Long subjectId;
    String subjectName;
    Long taskId;
    String taskTitle;
    PomodoroMode mode;
    Integer plannedDurationMinutes;
    Integer actualDurationMinutes;
    Instant completedAt;
    Instant createdAt;
}
