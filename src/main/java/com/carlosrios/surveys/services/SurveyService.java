package com.carlosrios.surveys.services;

import com.carlosrios.surveys.dto.QuestionRequestDTO;
import com.carlosrios.surveys.dto.QuestionResponseDTO;
import com.carlosrios.surveys.dto.SurveyRequestDTO;
import com.carlosrios.surveys.dto.SurveyResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SurveyService {
    SurveyResponseDTO createSurvey(SurveyRequestDTO surveyRequestDTO);
    QuestionResponseDTO addQuestion(QuestionRequestDTO questionRequestDTO, String title);
    SurveyResponseDTO readSurveyByTitle(String title);
    QuestionResponseDTO readQuestionById(String title, Long id);
    Page<SurveyResponseDTO> retrieveAllSurveys(Pageable pageable);
    List<QuestionResponseDTO> retrieveAllSurveyQuestions(String title);
    SurveyResponseDTO updateSurvey(SurveyRequestDTO surveyRequestDTO, String title);
    QuestionResponseDTO updateQuestion(QuestionRequestDTO questionRequestDTO, String title, Long id);
    void deleteSurveyById(Long id);
    void deleteQuestionById(String title, Long id);

}
