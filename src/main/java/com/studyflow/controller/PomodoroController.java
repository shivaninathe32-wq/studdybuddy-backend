package com.studyflow.controller;

import com.studyflow.dto.pomodoro.PomodoroSessionRequest;
import com.studyflow.dto.pomodoro.PomodoroSessionResponse;
import com.studyflow.dto.pomodoro.PomodoroSummaryResponse;
import com.studyflow.service.PomodoroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pomodoro")
@RequiredArgsConstructor
public class PomodoroController {

    private final PomodoroService pomodoroService;

    @GetMapping("/sessions")
    public Page<PomodoroSessionResponse> list(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return pomodoroService.list(pageable);
    }

    @PostMapping("/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public PomodoroSessionResponse create(@Valid @RequestBody PomodoroSessionRequest request) {
        return pomodoroService.create(request);
    }

    @GetMapping("/summary")
    public PomodoroSummaryResponse summary() {
        return pomodoroService.summary();
    }
}
