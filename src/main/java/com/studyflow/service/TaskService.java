package com.studyflow.service;

import com.studyflow.dto.task.TaskRequest;
import com.studyflow.dto.task.TaskResponse;
import com.studyflow.entity.Priority;
import com.studyflow.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    Page<TaskResponse> list(TaskStatus status, Priority priority, Long subjectId, String q, Pageable pageable);
    TaskResponse get(Long id);
    TaskResponse create(TaskRequest request);
    TaskResponse update(Long id, TaskRequest request);
    TaskResponse complete(Long id);
    void delete(Long id);
}
