package com.studyflow.dto.task;

import com.studyflow.entity.Priority;
import com.studyflow.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequest {

    private Long subjectId;

    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    private LocalDateTime dueDateTime;

    private Priority priority;

    private TaskStatus status;

    @PositiveOrZero
    private Integer estimatedDurationMinutes;

    @PositiveOrZero
    private Integer actualDurationMinutes;
}
