package com.studyflow.service;

import com.studyflow.dto.assignment.AssignmentRequest;
import com.studyflow.dto.assignment.AssignmentResponse;
import com.studyflow.entity.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssignmentService {
    Page<AssignmentResponse> list(SubmissionStatus status, Boolean upcoming, Boolean submitted,
                                  Boolean overdue, Pageable pageable);
    AssignmentResponse create(AssignmentRequest request);
    AssignmentResponse update(Long id, AssignmentRequest request);
    void delete(Long id);
}
