package com.carlosrios.surveys.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record QuestionRequestDTO(
        @NotNull(message = "description is required")
        String description,
        @NotNull(message = "answer is required")
        String correctAnswer,
        List<String> options
) {
}
