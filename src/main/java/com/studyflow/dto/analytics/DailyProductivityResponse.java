package com.studyflow.dto.analytics;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class DailyProductivityResponse {
    List<DayPoint> points;

    @Value
    @Builder
    public static class DayPoint {
        LocalDate date;
        double studyHours;
        long completedTasks;
        long focusMinutes;
    }
}
