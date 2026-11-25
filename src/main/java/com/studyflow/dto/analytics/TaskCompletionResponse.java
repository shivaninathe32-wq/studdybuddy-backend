package com.studyflow.dto.analytics;

import com.studyflow.entity.TaskStatus;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class TaskCompletionResponse {
    List<StatusCount> byStatus;
    long total;
    double completionRate;

    @Value
    @Builder
    public static class StatusCount {
        TaskStatus status;
        long count;
    }
}
