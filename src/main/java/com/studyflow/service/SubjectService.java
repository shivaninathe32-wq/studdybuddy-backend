package com.studyflow.service;

import com.studyflow.dto.subject.SubjectRequest;
import com.studyflow.dto.subject.SubjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubjectService {
    Page<SubjectResponse> list(String q, Pageable pageable);
    SubjectResponse get(Long id);
    SubjectResponse create(SubjectRequest request);
    SubjectResponse update(Long id, SubjectRequest request);
    void delete(Long id);
}
