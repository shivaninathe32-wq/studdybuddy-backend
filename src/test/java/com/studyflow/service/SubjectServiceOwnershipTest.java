package com.studyflow.service;

import com.studyflow.dto.subject.SubjectRequest;
import com.studyflow.entity.Subject;
import com.studyflow.entity.User;
import com.studyflow.exception.ResourceNotFoundException;
import com.studyflow.mapper.SubjectMapper;
import com.studyflow.repository.SubjectRepository;
import com.studyflow.repository.UserRepository;
import com.studyflow.security.UserPrincipal;
import com.studyflow.service.impl.SubjectServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectServiceOwnershipTest {

    @Mock private SubjectRepository subjectRepository;
    @Mock private UserRepository userRepository;
    @Mock private SubjectMapper subjectMapper;

    @InjectMocks
    private SubjectServiceImpl subjectService;

    @BeforeEach
    void authenticateAsUser1() {
        User user = User.builder().id(1L).email("u1@example.com").fullName("U1").password("x").build();
        UserPrincipal principal = new UserPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void get_otherUsersSubject_notFound() {
        when(subjectRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.get(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Subject not found");
    }

    @Test
    void update_otherUsersSubject_notFound() {
        when(subjectRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.empty());

        SubjectRequest request = new SubjectRequest();
        request.setName("Math");

        assertThatThrownBy(() -> subjectService.update(5L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_otherUsersSubject_notFound() {
        when(subjectRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.delete(5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void get_ownSubject_usesOwnershipLookup() {
        User owner = User.builder().id(1L).email("u1@example.com").fullName("U1").password("x").build();
        Subject subject = Subject.builder().id(3L).user(owner).name("Physics").build();
        when(subjectRepository.findByIdAndUserId(3L, 1L)).thenReturn(Optional.of(subject));
        when(subjectMapper.toResponse(subject)).thenReturn(
                com.studyflow.dto.subject.SubjectResponse.builder().id(3L).name("Physics").build());

        var response = subjectService.get(3L);
        org.assertj.core.api.Assertions.assertThat(response.getId()).isEqualTo(3L);
        org.assertj.core.api.Assertions.assertThat(response.getName()).isEqualTo("Physics");
    }
}
