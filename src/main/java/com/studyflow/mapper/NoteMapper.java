package com.studyflow.mapper;

import com.studyflow.dto.note.NoteRequest;
import com.studyflow.dto.note.NoteResponse;
import com.studyflow.entity.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    public NoteResponse toResponse(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .subjectId(note.getSubject() != null ? note.getSubject().getId() : null)
                .subjectName(note.getSubject() != null ? note.getSubject().getName() : null)
                .title(note.getTitle())
                .content(note.getContent())
                .pinned(note.isPinned())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }

    public void updateEntity(Note note, NoteRequest request) {
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        if (request.getPinned() != null) {
            note.setPinned(request.getPinned());
        }
    }
}
