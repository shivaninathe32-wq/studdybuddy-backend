package com.studyflow.dto.assignment;

import com.studyflow.entity.Priority;
import com.studyflow.entity.SubmissionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentRequest {

    @NotNull
    private Long subjectId;

    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    private LocalDateTime dueDateTime;

    private Priority priority;

    private SubmissionStatus submissionStatus;

    @Size(max = 2000)
    private String submissionNotes;
}
