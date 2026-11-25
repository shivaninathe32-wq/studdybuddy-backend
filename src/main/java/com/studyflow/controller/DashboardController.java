package com.studyflow.controller;

import com.studyflow.dto.dashboard.DashboardSummaryResponse;
import com.studyflow.dto.dashboard.SubjectDistributionResponse;
import com.studyflow.dto.dashboard.WeeklyProgressResponse;
import com.studyflow.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryResponse summary() {
        return dashboardService.summary();
    }

    @GetMapping("/weekly-progress")
    public WeeklyProgressResponse weeklyProgress() {
        return dashboardService.weeklyProgress();
    }

    @GetMapping("/subject-distribution")
    public SubjectDistributionResponse subjectDistribution() {
        return dashboardService.subjectDistribution();
    }
}
