package com.carlosrios.surveys.services;

import com.carlosrios.surveys.dto.SurveyRequestDTO;
import com.carlosrios.surveys.dto.SurveyResponseDTO;
import com.carlosrios.surveys.entities.Survey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurveyMapper {

    private final QuestionMapper questionMapper;

    public SurveyResponseDTO toDto(Survey survey) {
        return SurveyResponseDTO.builder()
                .id(survey.getId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .questions(survey.getQuestions().stream()
                        .map(q -> questionMapper.toDto(q))
                        .toList())
                .build();
    }

    public Survey toEntity(SurveyRequestDTO requestDTO) {
        return Survey.builder()
                .title(requestDTO.title())
                .description(requestDTO.description())
                .questions(requestDTO.questions().stream()
                        .map(q -> questionMapper.toEntity(q))
                        .toList())
                .build();
    }

}
