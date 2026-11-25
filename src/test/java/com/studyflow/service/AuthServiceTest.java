package com.studyflow.service;

import com.studyflow.dto.auth.*;
import com.studyflow.entity.PasswordResetToken;
import com.studyflow.entity.User;
import com.studyflow.exception.BadRequestException;
import com.studyflow.exception.ConflictException;
import com.studyflow.mapper.UserMapper;
import com.studyflow.repository.PasswordResetTokenRepository;
import com.studyflow.repository.UserRepository;
import com.studyflow.security.JwtService;
import com.studyflow.security.UserPrincipal;
import com.studyflow.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "resetTokenExpirationMs", 3_600_000L);
    }

    @Test
    void register_duplicateEmail_throwsConflict() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Sejal");
        request.setEmail("sejal@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        when(userRepository.existsByEmailIgnoreCase("sejal@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    void register_passwordMismatch_throwsBadRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Sejal");
        request.setEmail("sejal@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("different");

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("do not match");
    }

    @Test
    void login_badPassword_throwsBadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("sejal@example.com");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void forgotPassword_knownEmail_createsToken() {
        User user = User.builder().id(1L).email("sejal@example.com").fullName("Sejal").password("x").build();
        when(userRepository.findByEmailIgnoreCase("sejal@example.com")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("sejal@example.com");

        MessageResponse response = authService.forgotPassword(request);

        assertThat(response.getMessage()).contains("reset");
        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getToken()).isNotBlank();
        assertThat(captor.getValue().getUser()).isEqualTo(user);
    }

    @Test
    void resetPassword_validToken_updatesPassword() {
        User user = User.builder().id(1L).email("sejal@example.com").fullName("Sejal").password("old").build();
        PasswordResetToken token = PasswordResetToken.builder()
                .id(1L)
                .user(user)
                .token("abc")
                .expiryDate(Instant.now().plusSeconds(3600))
                .used(false)
                .build();

        when(passwordResetTokenRepository.findByToken("abc")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newpass12")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(passwordResetTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("abc");
        request.setNewPassword("newpass12");
        request.setConfirmPassword("newpass12");

        MessageResponse response = authService.resetPassword(request);

        assertThat(response.getMessage()).contains("reset successfully");
        assertThat(user.getPassword()).isEqualTo("encoded");
        assertThat(token.isUsed()).isTrue();
    }

    @Test
    void resetPassword_expiredToken_throws() {
        User user = User.builder().id(1L).email("sejal@example.com").fullName("Sejal").password("old").build();
        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .token("abc")
                .expiryDate(Instant.now().minusSeconds(10))
                .used(false)
                .build();
        when(passwordResetTokenRepository.findByToken("abc")).thenReturn(Optional.of(token));

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("abc");
        request.setNewPassword("newpass12");
        request.setConfirmPassword("newpass12");

        assertThatThrownBy(() -> authService.resetPassword(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void register_success_returnsToken() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Sejal");
        request.setEmail("sejal@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        when(userRepository.existsByEmailIgnoreCase("sejal@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });
        when(jwtService.generateToken(any(UserPrincipal.class))).thenReturn("jwt-token");
        when(userMapper.toResponse(any(User.class))).thenReturn(
                com.studyflow.dto.user.UserResponse.builder()
                        .id(10L).fullName("Sejal").email("sejal@example.com").build());

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getUser().getEmail()).isEqualTo("sejal@example.com");
    }
}
