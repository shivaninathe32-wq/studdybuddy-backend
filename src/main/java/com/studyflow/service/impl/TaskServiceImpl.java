package com.studyflow.service.impl;

import com.studyflow.dto.task.TaskRequest;
import com.studyflow.dto.task.TaskResponse;
import com.studyflow.entity.*;
import com.studyflow.exception.ResourceNotFoundException;
import com.studyflow.mapper.TaskMapper;
import com.studyflow.repository.SubjectRepository;
import com.studyflow.repository.TaskRepository;
import com.studyflow.repository.UserRepository;
import com.studyflow.security.SecurityUtils;
import com.studyflow.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> list(TaskStatus status, Priority priority, Long subjectId, String q,
                                   Pageable pageable) {
        Long userId = SecurityUtils.currentUserId();
        String pattern = (q == null || q.isBlank()) ? null : "%" + q.trim().toLowerCase() + "%";
        return taskRepository.findFiltered(userId, status, priority, subjectId, pattern, pageable)
                .map(taskMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse get(Long id) {
        return taskMapper.toResponse(findOwned(id));
    }

    @Override
    @Transactional
    public TaskResponse create(TaskRequest request) {
        Long userId = SecurityUtils.currentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Task task = Task.builder()
                .user(user)
                .subject(resolveSubject(request.getSubjectId(), userId))
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .dueDateTime(request.getDueDateTime())
                .priority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM)
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.PENDING)
                .estimatedDurationMinutes(request.getEstimatedDurationMinutes())
                .actualDurationMinutes(request.getActualDurationMinutes())
                .build();

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Long userId = SecurityUtils.currentUserId();
        Task task = findOwned(id);
        taskMapper.updateEntity(task, request);
        task.setTitle(request.getTitle().trim());
        task.setSubject(resolveSubject(request.getSubjectId(), userId));
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskResponse complete(Long id) {
        Task task = findOwned(id);
        task.setStatus(TaskStatus.COMPLETED);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        taskRepository.delete(findOwned(id));
    }

    private Task findOwned(Long id) {
        return taskRepository.findByIdAndUserId(id, SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    private Subject resolveSubject(Long subjectId, Long userId) {
        if (subjectId == null) {
            return null;
        }
        return subjectRepository.findByIdAndUserId(subjectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
    }
}
