package com.studyflow.service;

import com.studyflow.dto.auth.*;
import com.studyflow.dto.user.UserResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    MessageResponse forgotPassword(ForgotPasswordRequest request);
    MessageResponse resetPassword(ResetPasswordRequest request);
    UserResponse me();
}
