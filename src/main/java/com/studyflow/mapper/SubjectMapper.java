package com.studyflow.mapper;

import com.studyflow.dto.subject.SubjectRequest;
import com.studyflow.dto.subject.SubjectResponse;
import com.studyflow.entity.Subject;
import org.springframework.stereotype.Component;

@Component
public class SubjectMapper {

    public SubjectResponse toResponse(Subject subject) {
        return SubjectResponse.builder()
                .id(subject.getId())
                .name(subject.getName())
                .code(subject.getCode())
                .description(subject.getDescription())
                .targetHoursPerWeek(subject.getTargetHoursPerWeek())
                .color(subject.getColor())
                .createdAt(subject.getCreatedAt())
                .updatedAt(subject.getUpdatedAt())
                .build();
    }

    public void updateEntity(Subject subject, SubjectRequest request) {
        subject.setName(request.getName());
        subject.setCode(request.getCode());
        subject.setDescription(request.getDescription());
        subject.setTargetHoursPerWeek(request.getTargetHoursPerWeek());
        subject.setColor(request.getColor());
    }
}
