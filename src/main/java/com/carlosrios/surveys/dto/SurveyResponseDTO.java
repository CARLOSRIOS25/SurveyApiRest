package com.carlosrios.surveys.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SurveyResponseDTO(
        Long id,
        String title,
        String description,
        List<QuestionResponseDTO> questions
) {
}
