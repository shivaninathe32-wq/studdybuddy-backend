package com.studyflow.service;

import com.studyflow.dto.note.NoteRequest;
import com.studyflow.dto.note.NoteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoteService {
    Page<NoteResponse> list(Long subjectId, Boolean pinned, String q, Pageable pageable);
    NoteResponse create(NoteRequest request);
    NoteResponse update(Long id, NoteRequest request);
    void delete(Long id);
}
