package com.studyflow.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiError {
    String message;
    int status;
    Instant timestamp;
    List<FieldErrorDetail> fieldErrors;

    @Value
    @Builder
    public static class FieldErrorDetail {
        String field;
        String message;
    }
}
