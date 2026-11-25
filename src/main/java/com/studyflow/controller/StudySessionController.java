package com.studyflow.controller;

import com.studyflow.dto.session.StudySessionRequest;
import com.studyflow.dto.session.StudySessionResponse;
import com.studyflow.service.StudySessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/study-sessions")
@RequiredArgsConstructor
public class StudySessionController {

    private final StudySessionService studySessionService;

    @GetMapping
    public Page<StudySessionResponse> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return studySessionService.list(date, from, to, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudySessionResponse create(@Valid @RequestBody StudySessionRequest request) {
        return studySessionService.create(request);
    }

    @PutMapping("/{id}")
    public StudySessionResponse update(@PathVariable Long id, @Valid @RequestBody StudySessionRequest request) {
        return studySessionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        studySessionService.delete(id);
    }
}
