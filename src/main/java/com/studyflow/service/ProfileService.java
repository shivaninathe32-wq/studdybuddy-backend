package com.studyflow.service;

import com.studyflow.dto.user.UpdateProfileRequest;
import com.studyflow.dto.user.UserResponse;

public interface ProfileService {
    UserResponse getProfile();
    UserResponse updateProfile(UpdateProfileRequest request);
}
