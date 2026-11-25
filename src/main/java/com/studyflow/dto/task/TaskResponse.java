package com.studyflow.dto.task;

import com.studyflow.entity.Priority;
import com.studyflow.entity.TaskStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.time.LocalDateTime;

@Value
@Builder
public class TaskResponse {
    Long id;
    Long subjectId;
    String subjectName;
    String title;
    String description;
    LocalDateTime dueDateTime;
    Priority priority;
    TaskStatus status;
    Integer estimatedDurationMinutes;
    Integer actualDurationMinutes;
    Instant createdAt;
    Instant updatedAt;
}
