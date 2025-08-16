package com.carlosrios.surveys.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record QuestionResponseDTO(
        Long id,
        String description,
        String correctAnswer,
        List<String> options
) {
}
