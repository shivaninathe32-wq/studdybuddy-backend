package com.studyflow.dto.subject;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class SubjectResponse {
    Long id;
    String name;
    String code;
    String description;
    Double targetHoursPerWeek;
    String color;
    Instant createdAt;
    Instant updatedAt;
}
