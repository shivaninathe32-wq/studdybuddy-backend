package com.studyflow.controller;

import com.studyflow.dto.user.UpdateProfileRequest;
import com.studyflow.dto.user.UserResponse;
import com.studyflow.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public UserResponse get() {
        return profileService.getProfile();
    }

    @PutMapping
    public UserResponse update(@Valid @RequestBody UpdateProfileRequest request) {
        return profileService.updateProfile(request);
    }
}
