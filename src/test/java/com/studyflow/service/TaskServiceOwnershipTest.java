package com.studyflow.service;

import com.studyflow.dto.task.TaskRequest;
import com.studyflow.entity.Task;
import com.studyflow.entity.User;
import com.studyflow.exception.ResourceNotFoundException;
import com.studyflow.mapper.TaskMapper;
import com.studyflow.repository.SubjectRepository;
import com.studyflow.repository.TaskRepository;
import com.studyflow.repository.UserRepository;
import com.studyflow.security.UserPrincipal;
import com.studyflow.service.impl.TaskServiceImpl;
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
class TaskServiceOwnershipTest {

    @Mock private TaskRepository taskRepository;
    @Mock private SubjectRepository subjectRepository;
    @Mock private UserRepository userRepository;
    @Mock private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

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
    void get_otherUsersTask_notFound() {
        when(taskRepository.findByIdAndUserId(42L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.get(42L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void complete_otherUsersTask_notFound() {
        when(taskRepository.findByIdAndUserId(42L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.complete(42L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_otherUsersTask_notFound() {
        when(taskRepository.findByIdAndUserId(42L, 1L)).thenReturn(Optional.empty());
        TaskRequest request = new TaskRequest();
        request.setTitle("Homework");

        assertThatThrownBy(() -> taskService.update(42L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_otherUsersTask_notFound() {
        when(taskRepository.findByIdAndUserId(42L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.delete(42L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
