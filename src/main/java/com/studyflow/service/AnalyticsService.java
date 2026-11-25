package com.studyflow.service;

import com.studyflow.dto.analytics.DailyProductivityResponse;
import com.studyflow.dto.analytics.TaskCompletionResponse;

public interface AnalyticsService {
    DailyProductivityResponse dailyProductivity(int days);
    TaskCompletionResponse taskCompletion();
}
