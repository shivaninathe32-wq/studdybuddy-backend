package com.studyflow.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 120)
    private String fullName;

    @Size(max = 80)
    private String educationLevel;

    @Size(min = 8, max = 100)
    private String currentPassword;

    @Size(min = 8, max = 100)
    private String newPassword;

    private String confirmNewPassword;
}
