package com.studyflow.dto.session;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class StudySessionRequest {

    @NotNull
    private Long subjectId;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @Size(max = 255)
    private String studyGoal;

    @Size(max = 2000)
    private String notes;

    private Boolean completed;
}
