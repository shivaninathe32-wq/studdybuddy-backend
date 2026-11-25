package com.studyflow.service.impl;

import com.studyflow.dto.user.UpdateProfileRequest;
import com.studyflow.dto.user.UserResponse;
import com.studyflow.entity.User;
import com.studyflow.exception.BadRequestException;
import com.studyflow.exception.ResourceNotFoundException;
import com.studyflow.mapper.UserMapper;
import com.studyflow.repository.UserRepository;
import com.studyflow.security.SecurityUtils;
import com.studyflow.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile() {
        return userMapper.toResponse(currentUser());
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        User user = currentUser();

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getEducationLevel() != null) {
            user.setEducationLevel(request.getEducationLevel());
        }

        boolean changingPassword = request.getNewPassword() != null && !request.getNewPassword().isBlank();
        if (changingPassword) {
            if (request.getCurrentPassword() == null
                    || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BadRequestException("Current password is incorrect");
            }
            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                throw new BadRequestException("New passwords do not match");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    private User currentUser() {
        return userRepository.findById(SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
