package com.studyflow.dto.subject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubjectRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    @Size(max = 40)
    private String code;

    @Size(max = 1000)
    private String description;

    @PositiveOrZero
    private Double targetHoursPerWeek;

    @Size(max = 20)
    private String color;
}
