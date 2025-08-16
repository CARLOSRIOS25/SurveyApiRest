package com.carlosrios.surveys.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SurveyRequestDTO(
        String title,
        @NotNull(message = "description is required")
        String description,
        List<QuestionRequestDTO> questions
) {
}
