package com.studyflow.dto.dashboard;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class SubjectDistributionResponse {
    List<SubjectHours> subjects;

    @Value
    @Builder
    public static class SubjectHours {
        Long subjectId;
        String subjectName;
        double hours;
    }
}
