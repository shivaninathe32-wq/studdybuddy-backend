package com.studyflow.service;

import com.studyflow.dto.pomodoro.PomodoroSessionRequest;
import com.studyflow.dto.pomodoro.PomodoroSessionResponse;
import com.studyflow.dto.pomodoro.PomodoroSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PomodoroService {
    Page<PomodoroSessionResponse> list(Pageable pageable);
    PomodoroSessionResponse create(PomodoroSessionRequest request);
    PomodoroSummaryResponse summary();
}
