package com.studyflow.service.impl;

import com.studyflow.dto.session.StudySessionRequest;
import com.studyflow.dto.session.StudySessionResponse;
import com.studyflow.entity.StudySession;
import com.studyflow.entity.Subject;
import com.studyflow.entity.User;
import com.studyflow.exception.BadRequestException;
import com.studyflow.exception.ResourceNotFoundException;
import com.studyflow.mapper.StudySessionMapper;
import com.studyflow.repository.StudySessionRepository;
import com.studyflow.repository.SubjectRepository;
import com.studyflow.repository.UserRepository;
import com.studyflow.security.SecurityUtils;
import com.studyflow.service.StudySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudySessionServiceImpl implements StudySessionService {

    private final StudySessionRepository studySessionRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final StudySessionMapper studySessionMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<StudySessionResponse> list(LocalDate date, LocalDate from, LocalDate to, Pageable pageable) {
        Long userId = SecurityUtils.currentUserId();
        Page<StudySession> page;
        if (date != null) {
            page = studySessionRepository.findByUserIdAndDate(userId, date, pageable);
        } else if (from != null && to != null) {
            page = studySessionRepository.findByUserIdAndDateBetween(userId, from, to, pageable);
        } else if (from != null) {
            page = studySessionRepository.findByUserIdAndDateGreaterThanEqual(userId, from, pageable);
        } else if (to != null) {
            page = studySessionRepository.findByUserIdAndDateLessThanEqual(userId, to, pageable);
        } else {
            page = studySessionRepository.findByUserId(userId, pageable);
        }
        return page.map(studySessionMapper::toResponse);
    }

    @Override
    @Transactional
    public StudySessionResponse create(StudySessionRequest request) {
        validateTimes(request);
        Long userId = SecurityUtils.currentUserId();
        ensureNoOverlap(userId, request, null);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Subject subject = findOwnedSubject(request.getSubjectId(), userId);

        StudySession session = StudySession.builder()
                .user(user)
                .subject(subject)
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .studyGoal(request.getStudyGoal())
                .notes(request.getNotes())
                .completed(Boolean.TRUE.equals(request.getCompleted()))
                .build();

        return studySessionMapper.toResponse(studySessionRepository.save(session));
    }

    @Override
    @Transactional
    public StudySessionResponse update(Long id, StudySessionRequest request) {
        validateTimes(request);
        Long userId = SecurityUtils.currentUserId();
        StudySession session = studySessionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Study session not found"));

        ensureNoOverlap(userId, request, id);
        Subject subject = findOwnedSubject(request.getSubjectId(), userId);
        session.setSubject(subject);
        studySessionMapper.updateEntity(session, request);

        return studySessionMapper.toResponse(studySessionRepository.save(session));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        StudySession session = studySessionRepository.findByIdAndUserId(id, SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Study session not found"));
        studySessionRepository.delete(session);
    }

    private void validateTimes(StudySessionRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BadRequestException("End time must be after start time");
        }
    }

    private void ensureNoOverlap(Long userId, StudySessionRequest request, Long excludeId) {
        List<StudySession> overlaps = excludeId == null
                ? studySessionRepository.findOverlapping(
                        userId, request.getDate(), request.getStartTime(), request.getEndTime())
                : studySessionRepository.findOverlappingExcluding(
                        userId, request.getDate(), request.getStartTime(), request.getEndTime(), excludeId);
        if (!overlaps.isEmpty()) {
            throw new BadRequestException("Study session overlaps with an existing session");
        }
    }

    private Subject findOwnedSubject(Long subjectId, Long userId) {
        return subjectRepository.findByIdAndUserId(subjectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
    }
}
