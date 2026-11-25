package com.studyflow.dto.pomodoro;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PomodoroSummaryResponse {
    long totalFocusSessions;
    long totalShortBreaks;
    long totalLongBreaks;
    long totalFocusMinutes;
    long totalBreakMinutes;
}
