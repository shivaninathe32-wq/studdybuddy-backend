package com.studyflow.dto.note;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class NoteResponse {
    Long id;
    Long subjectId;
    String subjectName;
    String title;
    String content;
    boolean pinned;
    Instant createdAt;
    Instant updatedAt;
}
