package com.studyflow.mapper;

import com.studyflow.dto.session.StudySessionRequest;
import com.studyflow.dto.session.StudySessionResponse;
import com.studyflow.entity.StudySession;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class StudySessionMapper {

    public StudySessionResponse toResponse(StudySession session) {
        return StudySessionResponse.builder()
                .id(session.getId())
                .subjectId(session.getSubject().getId())
                .subjectName(session.getSubject().getName())
                .date(session.getDate())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .studyGoal(session.getStudyGoal())
                .notes(session.getNotes())
                .completed(session.isCompleted())
                .durationHours(durationHours(session))
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    public void updateEntity(StudySession session, StudySessionRequest request) {
        session.setDate(request.getDate());
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());
        session.setStudyGoal(request.getStudyGoal());
        session.setNotes(request.getNotes());
        if (request.getCompleted() != null) {
            session.setCompleted(request.getCompleted());
        }
    }

    public static double durationHours(StudySession session) {
        LocalDateTime start = LocalDateTime.of(session.getDate(), session.getStartTime());
        LocalDateTime end = LocalDateTime.of(session.getDate(), session.getEndTime());
        if (end.isBefore(start)) {
            end = end.plusDays(1);
        }
        return Duration.between(start, end).toMinutes() / 60.0;
    }
}
