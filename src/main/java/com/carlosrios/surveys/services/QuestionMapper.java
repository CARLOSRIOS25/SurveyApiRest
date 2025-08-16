package com.carlosrios.surveys.services;

import com.carlosrios.surveys.dto.QuestionRequestDTO;
import com.carlosrios.surveys.dto.QuestionResponseDTO;
import com.carlosrios.surveys.entities.Question;
import org.springframework.stereotype.Service;

@Service
public class QuestionMapper {

    public QuestionResponseDTO toDto(Question question) {
        return QuestionResponseDTO.builder()
                .id(question.getId())
                .description(question.getDescription())
                .correctAnswer(question.getCorrectAnswer())
                .options(question.getOptions())
                .build();
    }

    public Question toEntity(QuestionRequestDTO dto) {
        return Question.builder()
                .description(dto.description())
                .correctAnswer(dto.correctAnswer())
                .options(dto.options())
                .build();
    }
}
