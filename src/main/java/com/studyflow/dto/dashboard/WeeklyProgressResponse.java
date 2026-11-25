package com.studyflow.dto.dashboard;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class WeeklyProgressResponse {
    List<DayHours> days;

    @Value
    @Builder
    public static class DayHours {
        LocalDate date;
        double hours;
    }
}
