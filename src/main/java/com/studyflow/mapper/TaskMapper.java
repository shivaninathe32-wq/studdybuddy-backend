package com.studyflow.mapper;

import com.studyflow.dto.task.TaskRequest;
import com.studyflow.dto.task.TaskResponse;
import com.studyflow.entity.Priority;
import com.studyflow.entity.Task;
import com.studyflow.entity.TaskStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .subjectId(task.getSubject() != null ? task.getSubject().getId() : null)
                .subjectName(task.getSubject() != null ? task.getSubject().getName() : null)
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDateTime(task.getDueDateTime())
                .priority(task.getPriority())
                .status(task.getStatus())
                .estimatedDurationMinutes(task.getEstimatedDurationMinutes())
                .actualDurationMinutes(task.getActualDurationMinutes())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public void updateEntity(Task task, TaskRequest request) {
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDateTime(request.getDueDateTime());
        task.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.PENDING);
        task.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
        task.setActualDurationMinutes(request.getActualDurationMinutes());
    }
}
