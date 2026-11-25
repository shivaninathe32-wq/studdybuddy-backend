package com.studyflow.dto.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoteRequest {

    private Long subjectId;

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    private String content;

    private Boolean pinned;
}
