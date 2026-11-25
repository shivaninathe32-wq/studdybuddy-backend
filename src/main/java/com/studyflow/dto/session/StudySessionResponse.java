package com.studyflow.dto.session;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Value
@Builder
public class StudySessionResponse {
    Long id;
    Long subjectId;
    String subjectName;
    LocalDate date;
    LocalTime startTime;
    LocalTime endTime;
    String studyGoal;
    String notes;
    boolean completed;
    double durationHours;
    Instant createdAt;
    Instant updatedAt;
}
