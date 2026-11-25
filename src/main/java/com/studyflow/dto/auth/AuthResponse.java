package com.studyflow.dto.auth;

import com.studyflow.dto.user.UserResponse;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthResponse {
    String token;
    String type;
    UserResponse user;
}
