package com.studyflow.service;

import com.studyflow.dto.session.StudySessionRequest;
import com.studyflow.dto.session.StudySessionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface StudySessionService {
    Page<StudySessionResponse> list(LocalDate date, LocalDate from, LocalDate to, Pageable pageable);
    StudySessionResponse create(StudySessionRequest request);
    StudySessionResponse update(Long id, StudySessionRequest request);
    void delete(Long id);
}
