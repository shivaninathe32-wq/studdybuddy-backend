package com.studyflow.mapper;

import com.studyflow.dto.assignment.AssignmentRequest;
import com.studyflow.dto.assignment.AssignmentResponse;
import com.studyflow.entity.Assignment;
import com.studyflow.entity.Priority;
import com.studyflow.entity.SubmissionStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AssignmentMapper {

    public AssignmentResponse toResponse(Assignment assignment) {
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .subjectId(assignment.getSubject().getId())
                .subjectName(assignment.getSubject().getName())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .dueDateTime(assignment.getDueDateTime())
                .priority(assignment.getPriority())
                .submissionStatus(resolveStatus(assignment))
                .submissionNotes(assignment.getSubmissionNotes())
                .createdAt(assignment.getCreatedAt())
                .updatedAt(assignment.getUpdatedAt())
                .build();
    }

    public void updateEntity(Assignment assignment, AssignmentRequest request) {
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDueDateTime(request.getDueDateTime());
        assignment.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
        if (request.getSubmissionStatus() != null) {
            assignment.setSubmissionStatus(request.getSubmissionStatus());
        }
        assignment.setSubmissionNotes(request.getSubmissionNotes());
    }

    public SubmissionStatus resolveStatus(Assignment assignment) {
        if (assignment.getSubmissionStatus() == SubmissionStatus.SUBMITTED) {
            return SubmissionStatus.SUBMITTED;
        }
        if (assignment.getDueDateTime() != null && assignment.getDueDateTime().isBefore(LocalDateTime.now())) {
            return SubmissionStatus.OVERDUE;
        }
        return assignment.getSubmissionStatus() != null
                ? assignment.getSubmissionStatus()
                : SubmissionStatus.PENDING;
    }
}
