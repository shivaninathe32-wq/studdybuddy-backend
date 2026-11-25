package com.studyflow.controller;

import com.studyflow.dto.analytics.DailyProductivityResponse;
import com.studyflow.dto.analytics.TaskCompletionResponse;
import com.studyflow.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/daily-productivity")
    public DailyProductivityResponse dailyProductivity(@RequestParam(defaultValue = "7") int days) {
        return analyticsService.dailyProductivity(days);
    }

    @GetMapping("/task-completion")
    public TaskCompletionResponse taskCompletion() {
        return analyticsService.taskCompletion();
    }
}
