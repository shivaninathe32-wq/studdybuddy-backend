package com.studyflow.service.impl;

import com.studyflow.dto.assignment.AssignmentRequest;
import com.studyflow.dto.assignment.AssignmentResponse;
import com.studyflow.entity.*;
import com.studyflow.exception.ResourceNotFoundException;
import com.studyflow.mapper.AssignmentMapper;
import com.studyflow.repository.AssignmentRepository;
import com.studyflow.repository.SubjectRepository;
import com.studyflow.repository.UserRepository;
import com.studyflow.security.SecurityUtils;
import com.studyflow.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final AssignmentMapper assignmentMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<AssignmentResponse> list(SubmissionStatus status, Boolean upcoming, Boolean submitted,
                                         Boolean overdue, Pageable pageable) {
        Long userId = SecurityUtils.currentUserId();
        boolean upcomingFlag = Boolean.TRUE.equals(upcoming);
        boolean submittedFlag = Boolean.TRUE.equals(submitted);
        boolean overdueFlag = Boolean.TRUE.equals(overdue);

        return assignmentRepository.findFiltered(
                        userId, status, upcomingFlag, overdueFlag, submittedFlag, LocalDateTime.now(), pageable)
                .map(assignmentMapper::toResponse);
    }

    @Override
    @Transactional
    public AssignmentResponse create(AssignmentRequest request) {
        Long userId = SecurityUtils.currentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Subject subject = subjectRepository.findByIdAndUserId(request.getSubjectId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        Assignment assignment = Assignment.builder()
                .user(user)
                .subject(subject)
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .dueDateTime(request.getDueDateTime())
                .priority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM)
                .submissionStatus(request.getSubmissionStatus() != null
                        ? request.getSubmissionStatus()
                        : SubmissionStatus.PENDING)
                .submissionNotes(request.getSubmissionNotes())
                .build();

        return assignmentMapper.toResponse(assignmentRepository.save(assignment));
    }

    @Override
    @Transactional
    public AssignmentResponse update(Long id, AssignmentRequest request) {
        Long userId = SecurityUtils.currentUserId();
        Assignment assignment = assignmentRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        Subject subject = subjectRepository.findByIdAndUserId(request.getSubjectId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        assignment.setSubject(subject);
        assignmentMapper.updateEntity(assignment, request);
        assignment.setTitle(request.getTitle().trim());
        return assignmentMapper.toResponse(assignmentRepository.save(assignment));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Assignment assignment = assignmentRepository.findByIdAndUserId(id, SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        assignmentRepository.delete(assignment);
    }
}
