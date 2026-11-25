package com.studyflow.service.impl;

import com.studyflow.dto.subject.SubjectRequest;
import com.studyflow.dto.subject.SubjectResponse;
import com.studyflow.entity.Subject;
import com.studyflow.entity.User;
import com.studyflow.exception.ResourceNotFoundException;
import com.studyflow.mapper.SubjectMapper;
import com.studyflow.repository.SubjectRepository;
import com.studyflow.repository.UserRepository;
import com.studyflow.security.SecurityUtils;
import com.studyflow.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final SubjectMapper subjectMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<SubjectResponse> list(String q, Pageable pageable) {
        Long userId = SecurityUtils.currentUserId();
        if (q == null || q.isBlank()) {
            return subjectRepository.findByUserId(userId, pageable).map(subjectMapper::toResponse);
        }
        String pattern = "%" + q.trim().toLowerCase() + "%";
        return subjectRepository.searchByUserId(userId, pattern, pageable).map(subjectMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse get(Long id) {
        return subjectMapper.toResponse(findOwned(id));
    }

    @Override
    @Transactional
    public SubjectResponse create(SubjectRequest request) {
        User user = currentUserEntity();
        Subject subject = Subject.builder()
                .user(user)
                .name(request.getName().trim())
                .code(request.getCode())
                .description(request.getDescription())
                .targetHoursPerWeek(request.getTargetHoursPerWeek())
                .color(request.getColor())
                .build();
        return subjectMapper.toResponse(subjectRepository.save(subject));
    }

    @Override
    @Transactional
    public SubjectResponse update(Long id, SubjectRequest request) {
        Subject subject = findOwned(id);
        subjectMapper.updateEntity(subject, request);
        subject.setName(request.getName().trim());
        return subjectMapper.toResponse(subjectRepository.save(subject));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Subject subject = findOwned(id);
        subjectRepository.delete(subject);
    }

    private Subject findOwned(Long id) {
        return subjectRepository.findByIdAndUserId(id, SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
    }

    private User currentUserEntity() {
        return userRepository.findById(SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
