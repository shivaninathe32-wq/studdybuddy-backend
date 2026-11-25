package com.studyflow.dto.user;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class UserResponse {
    Long id;
    String fullName;
    String email;
    String educationLevel;
    Instant createdAt;
    Instant updatedAt;
}
