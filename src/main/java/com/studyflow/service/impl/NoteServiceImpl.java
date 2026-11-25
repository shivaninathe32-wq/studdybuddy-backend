package com.studyflow.service.impl;

import com.studyflow.dto.note.NoteRequest;
import com.studyflow.dto.note.NoteResponse;
import com.studyflow.entity.Note;
import com.studyflow.entity.Subject;
import com.studyflow.entity.User;
import com.studyflow.exception.ResourceNotFoundException;
import com.studyflow.mapper.NoteMapper;
import com.studyflow.repository.NoteRepository;
import com.studyflow.repository.SubjectRepository;
import com.studyflow.repository.UserRepository;
import com.studyflow.security.SecurityUtils;
import com.studyflow.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final NoteMapper noteMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<NoteResponse> list(Long subjectId, Boolean pinned, String q, Pageable pageable) {
        Long userId = SecurityUtils.currentUserId();
        String pattern = (q == null || q.isBlank()) ? null : "%" + q.trim().toLowerCase() + "%";
        return noteRepository.findFiltered(userId, subjectId, pinned, pattern, pageable)
                .map(noteMapper::toResponse);
    }

    @Override
    @Transactional
    public NoteResponse create(NoteRequest request) {
        Long userId = SecurityUtils.currentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Note note = Note.builder()
                .user(user)
                .subject(resolveSubject(request.getSubjectId(), userId))
                .title(request.getTitle().trim())
                .content(request.getContent())
                .pinned(Boolean.TRUE.equals(request.getPinned()))
                .build();

        return noteMapper.toResponse(noteRepository.save(note));
    }

    @Override
    @Transactional
    public NoteResponse update(Long id, NoteRequest request) {
        Long userId = SecurityUtils.currentUserId();
        Note note = noteRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));
        noteMapper.updateEntity(note, request);
        note.setTitle(request.getTitle().trim());
        note.setSubject(resolveSubject(request.getSubjectId(), userId));
        return noteMapper.toResponse(noteRepository.save(note));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Note note = noteRepository.findByIdAndUserId(id, SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));
        noteRepository.delete(note);
    }

    private Subject resolveSubject(Long subjectId, Long userId) {
        if (subjectId == null) {
            return null;
        }
        return subjectRepository.findByIdAndUserId(subjectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
    }
}
