package com.studyflow.service;

import com.studyflow.dto.dashboard.DashboardSummaryResponse;
import com.studyflow.dto.dashboard.SubjectDistributionResponse;
import com.studyflow.dto.dashboard.WeeklyProgressResponse;

public interface DashboardService {
    DashboardSummaryResponse summary();
    WeeklyProgressResponse weeklyProgress();
    SubjectDistributionResponse subjectDistribution();
}
