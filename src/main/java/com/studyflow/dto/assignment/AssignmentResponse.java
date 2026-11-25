package com.studyflow.dto.assignment;

import com.studyflow.entity.Priority;
import com.studyflow.entity.SubmissionStatus;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.time.LocalDateTime;

@Value
@Builder
public class AssignmentResponse {
    Long id;
    Long subjectId;
    String subjectName;
    String title;
    String description;
    LocalDateTime dueDateTime;
    Priority priority;
    SubmissionStatus submissionStatus;
    String submissionNotes;
    Instant createdAt;
    Instant updatedAt;
}
