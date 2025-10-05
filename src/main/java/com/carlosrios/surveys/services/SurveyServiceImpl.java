package com.carlosrios.surveys.services;

import com.carlosrios.surveys.dto.QuestionRequestDTO;
import com.carlosrios.surveys.dto.QuestionResponseDTO;
import com.carlosrios.surveys.dto.SurveyRequestDTO;
import com.carlosrios.surveys.dto.SurveyResponseDTO;
import com.carlosrios.surveys.infra.exceptions.TitleNotValidException;
import com.carlosrios.surveys.repositories.QuestionRepository;
import com.carlosrios.surveys.repositories.SurveyRepository;
import com.rioscarlos.common.utils.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final SurveyMapper surveyMapper;
    private final QuestionMapper questionMapper;

    static final String ADVICE = "Entity not found with: ";

    public SurveyResponseDTO createSurvey(SurveyRequestDTO requestDTO) {
        this.validateSurveyTitle(requestDTO.title());

        var invalidQuestions = requestDTO.questions()
                .stream()
                .map(QuestionRequestDTO::description)
                .filter(d -> !Validator.isQuestion(d))
                .toList();

        if (!invalidQuestions.isEmpty()) {
            throw new IllegalArgumentException("the next questions must ends with a question mark (?): " + invalidQuestions);
        }

        var survey = this.surveyMapper.toEntity(requestDTO);

        var questionEntities = requestDTO.questions().stream()
                .map(this.questionMapper::toEntity)
                .toList();

        // associate each question with the survey
        questionEntities.forEach(q -> q.setSurvey(survey));
        survey.setQuestions(questionEntities);

        var entityCreated = this.surveyRepository.save(survey);

        return this.surveyMapper.toDto(entityCreated);
    }

    @Override
    public QuestionResponseDTO addQuestion(QuestionRequestDTO questionRequestDTO, String title) {
        this.validateQuestionDescription(questionRequestDTO.description());

        var survey = this.surveyRepository.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException(ADVICE + title));

        var entityCreated = this.questionMapper.toEntity(questionRequestDTO);

        entityCreated.setSurvey(survey);
        this.questionRepository.save(entityCreated);

        return this.questionMapper.toDto(entityCreated);
    }

    @Override
    public SurveyResponseDTO readSurveyByTitle(String title) {
        var entityResponse = this.surveyRepository.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException(ADVICE + title));

        return this.surveyMapper.toDto(entityResponse);
    }

    @Override
    public QuestionResponseDTO readQuestionById(String title, Long id) {
        var survey = this.surveyRepository.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException(ADVICE + title));
        return survey.getQuestions().stream()
                .filter(q -> q.getId().equals(id))
                .findFirst()
                .map(this.questionMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException(ADVICE + id));
    }

    @Override
    public Page<SurveyResponseDTO> retrieveAllSurveys(Pageable pageable) {
        var surveys = this.surveyRepository.findAll(pageable);
        return surveys.map(this.surveyMapper::toDto); //.map(s -> this.surveyMapper.toDto(s)).toList()
    }

    @Override
    public List<QuestionResponseDTO> retrieveAllSurveyQuestions(String title) {
        var questions = this.questionRepository.findAllBySurveyTitle(title)
                .orElseThrow(() -> new IllegalArgumentException(ADVICE + title));
        return questions.stream().map(this.questionMapper::toDto).toList(); //.map(q -> this.questionMapper.toDto(q)).toList()
    }

    @Override
    public SurveyResponseDTO updateSurvey(SurveyRequestDTO surveyRequestDTO, String title) {
        this.validateSurveyTitle(surveyRequestDTO.title());

        var surveyDB = this.surveyRepository.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException(ADVICE + title));

        surveyDB.setTitle(surveyRequestDTO.title());
        surveyDB.setDescription(surveyRequestDTO.description());

        var entityUpdated = this.surveyRepository.save(surveyDB);

        return this.surveyMapper.toDto(entityUpdated);
    }

    @Override
    public QuestionResponseDTO updateQuestion(QuestionRequestDTO questionRequestDTO, String title, Long id) {
        this.validateQuestionDescription(questionRequestDTO.description());

        var surveyDB = this.surveyRepository.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException(ADVICE + title));

        var questionDB = surveyDB.getQuestions().stream()
                .filter(q -> q.getId().equals(id))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(ADVICE + id));

        questionDB.setDescription(questionRequestDTO.description());
        questionDB.setCorrectAnswer(questionRequestDTO.correctAnswer());
        questionDB.setOptions(questionRequestDTO.options());

        var entityUpdated = this.questionRepository.save(questionDB);

        return this.questionMapper.toDto(entityUpdated);
    }

    @Override
    public void deleteSurveyById(Long id) {
        var survey = this.surveyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ADVICE + id));

        this.surveyRepository.delete(survey);
    }

    @Override
    public void deleteQuestionById(String title, Long id) {

        var surveyDB = this.surveyRepository.findByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException(ADVICE + title));

        var questionDB = surveyDB.getQuestions().stream()
                .filter(q -> q.getId().equals(id))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(ADVICE + id));

        surveyDB.getQuestions().remove(questionDB);
        this.questionRepository.delete(questionDB);
    }

    private void validateSurveyTitle(String title) {
        if (title.contains("bannedWord1") || title.contains("bannedWord2")) {
            throw new TitleNotValidException(
                    "Title contains banned words");
        }

        if (!Validator.startsWithUpperCase(title)) {
            throw new TitleNotValidException(
                    "Title must start with an uppercase letter: " + title
            );
        }

        if (!Validator.hasValidLength(title)) {
            throw new TitleNotValidException(
                    "Title must be shorter than 20 characters: " + title
            );
        }
    }

    private void validateQuestionDescription(String description){
        if (!Validator.isQuestion(description)) {
            throw new IllegalArgumentException
                    ("Question description must end with a question mark (?): " + description);
        }
    }
}
