package com.studyflow.dto.pomodoro;

import com.studyflow.entity.PomodoroMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PomodoroSessionRequest {

    private Long subjectId;
    private Long taskId;

    @NotNull
    private PomodoroMode mode;

    @NotNull
    @Positive
    private Integer plannedDurationMinutes;

    @Positive
    private Integer actualDurationMinutes;

    private Boolean completed;
}
