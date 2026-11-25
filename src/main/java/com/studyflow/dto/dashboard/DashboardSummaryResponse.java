package com.studyflow.dto.dashboard;

import com.studyflow.dto.assignment.AssignmentResponse;
import com.studyflow.dto.session.StudySessionResponse;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class DashboardSummaryResponse {
    String fullName;
    LocalDate date;
    long totalSubjects;
    double todayStudyHours;
    long pendingTasks;
    long completedTasks;
    List<AssignmentResponse> upcomingDeadlines;
    double dailyStudyGoal;
    List<StudySessionResponse> todaySchedule;
    List<ActivityItem> recentActivity;

    @Value
    @Builder
    public static class ActivityItem {
        String type;
        String title;
        String detail;
        java.time.Instant timestamp;
    }
}
