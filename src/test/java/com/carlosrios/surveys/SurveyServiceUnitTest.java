package com.carlosrios.surveys;

import com.carlosrios.surveys.dto.QuestionRequestDTO;
import com.carlosrios.surveys.dto.QuestionResponseDTO;
import com.carlosrios.surveys.dto.SurveyRequestDTO;
import com.carlosrios.surveys.dto.SurveyResponseDTO;
import com.carlosrios.surveys.entities.Question;
import com.carlosrios.surveys.entities.Survey;
import com.carlosrios.surveys.infra.exceptions.TitleNotValidException;
import com.carlosrios.surveys.repositories.QuestionRepository;
import com.carlosrios.surveys.repositories.SurveyRepository;
import com.carlosrios.surveys.services.QuestionMapper;
import com.carlosrios.surveys.services.SurveyMapper;
import com.carlosrios.surveys.services.SurveyServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class SurveyServiceUnitTest {

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private SurveyMapper surveyMapper;

    @Mock
    private QuestionMapper questionMapper;

    @InjectMocks
    private SurveyServiceImpl surveyService;

    private Survey survey;
    private Question question;

    private SurveyRequestDTO surveyRequestDTO;
    private QuestionRequestDTO questionRequestDTO;

    private SurveyResponseDTO surveyResponseDTO;
    private QuestionResponseDTO questionResponseDTO;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        log.info("Executing: {}", testInfo.getDisplayName());

        survey = new Survey(1L, "Test survey 1", "survey for test 1", List.of());

        surveyRequestDTO = new SurveyRequestDTO("Test survey req", "survey req for test", List.of());

        surveyResponseDTO = SurveyResponseDTO.builder()
                .id(1L)
                .title("Test survey 1")
                .description("survey for test 1")
                .questions(List.of())
                .build();

        question = new Question(1L, "test question 1?", "question for test 1", List.of(), survey);

        questionRequestDTO = new QuestionRequestDTO("test question req?", "question req for test", List.of());

        questionResponseDTO = QuestionResponseDTO.builder()
                .id(1L)
                .description("question for test 1?")
                .options(List.of())
                .build();

        //for question methods which we search the question in the survey like updateQuestion
        survey.setQuestions(new ArrayList<>(List.of(question)));
    }

    @Nested
    @DisplayName("All related survey tests")
    class SurveyTests{

        @Nested
        @DisplayName("All survey happy path tests")
        class SurveyHappyPathTests{

            @Test
            void retrieveAllSurveys_shouldReturnPageOfSurveys(){
                Survey survey1 = new Survey(1L, "Test survey 1", "survey for test 1", List.of());
                Survey survey2 = new Survey(2L, "Test survey 2", "survey for test 2", List.of());

                when(surveyRepository.findAll(PageRequest.of(0, 10)))
                        .thenReturn(new PageImpl<>(List.of(survey1, survey2), PageRequest.of(0, 10), 2));

                when(surveyMapper.toDto(survey1)).thenReturn(new SurveyResponseDTO(1L, "Test survey 1", "survey for test 1", List.of()));
                when(surveyMapper.toDto(survey2)).thenReturn(new SurveyResponseDTO(2L, "Test survey 2", "survey for test 2", List.of()));

                Page<SurveyResponseDTO> surveysDTO = surveyService.retrieveAllSurveys(PageRequest.of(0, 10));

                assertAll(
                        () -> assertEquals(2, surveysDTO.getTotalElements()),
                        () -> assertEquals("Test survey 1", surveysDTO.toList().get(0).title()),
                        () -> assertEquals("Test survey 2", surveysDTO.toList().get(1).title())
                );

                verify(surveyRepository).findAll(PageRequest.of(0, 10));
                verify(surveyMapper, times(2)).toDto(any(Survey.class));
            }

            @Test
            void retrieveSurveyByTitle_shouldReturnSurveyIfExists(){
                var title = "Test survey 1";

                when(surveyRepository.findByTitle(title)).thenReturn(Optional.of(survey));

                when(surveyMapper.toDto(survey))
                        .thenReturn(surveyResponseDTO);

                var surveyDTO = surveyService.readSurveyByTitle(title);

                assertNotNull(surveyDTO);
                assertEquals("Test survey 1", surveyDTO.title());

                verify(surveyRepository).findByTitle(title);
                verify(surveyMapper).toDto(survey);
            }

            @Test
            void updateSurvey_shouldUpdateSurveyIfExists() {
                var title = "Test survey 1";

                var updatedSurvey = new Survey(1L, surveyRequestDTO.title(), surveyRequestDTO.description(), List.of());

                when(surveyRepository.findByTitle(title)).thenReturn(Optional.of(survey));
                when(surveyRepository.save(survey)).thenReturn(updatedSurvey);

                // setting the values from the request to the response
                surveyResponseDTO = SurveyResponseDTO.builder()
                        .id(updatedSurvey.getId())
                        .title(updatedSurvey.getTitle())
                        .description(updatedSurvey.getDescription())
                        .questions(List.of())
                        .build();

                System.out.println(surveyResponseDTO);

                when(surveyMapper.toDto(updatedSurvey)).thenReturn(surveyResponseDTO);

                surveyService.updateSurvey(surveyRequestDTO, title);

                assertAll(
                        () -> assertNotNull(surveyResponseDTO),
                        () -> assertEquals(surveyResponseDTO.title(), surveyRequestDTO.title()),
                        () -> assertEquals(surveyResponseDTO.description(), surveyRequestDTO.description())
                );

                verify(surveyRepository).findByTitle(title);
                verify(surveyRepository).save(survey);
                verify(surveyMapper).toDto(updatedSurvey);
            }

            @Test
            void deleteSurvey_shouldDeleteSurveyIfExists() {
                var surveyId = 1L;

                when(surveyRepository.findById(surveyId)).thenReturn(Optional.of(survey));

                doNothing().when(surveyRepository).delete(survey);

                surveyService.deleteSurveyById(surveyId);

                verify(surveyRepository).findById(surveyId);
                verify(surveyRepository).delete(survey);
            }
        }

        @Nested
        @DisplayName("All survey unhappy path tests")
        class SurveyUnhappyPathTests{

            @Test
            void createSurvey_shouldThrowTitleValidException() {

                surveyRequestDTO = new SurveyRequestDTO("bannedWord1", "survey for test 1", List.of());

                TitleNotValidException ex =
                        assertThrows(TitleNotValidException.class, () -> surveyService.createSurvey(surveyRequestDTO));

                System.out.println(ex.getMessage());

                assertEquals("Title contains banned words", ex.getMessage());

                verify(surveyRepository, never()).save(any(Survey.class));
            }

            @Test
            void createSurvey_shouldThrowTitleValidExceptionForUppercase() {

                surveyRequestDTO = new SurveyRequestDTO("test survey 1", "survey for test 1", List.of());

                TitleNotValidException ex =
                        assertThrows(TitleNotValidException.class, () -> surveyService.createSurvey(surveyRequestDTO));

                System.out.println(ex.getMessage());

                assertEquals("Title must start with an uppercase letter: " + surveyRequestDTO.title(), ex.getMessage());

                verify(surveyRepository, never()).save(any(Survey.class));
            }

            @Test
            void createSurvey_shouldThrowTitleValidExceptionForTitleLength() {

                surveyRequestDTO = new SurveyRequestDTO("Test survey 1, test survey 1", "survey for test 1", List.of());

                TitleNotValidException ex =
                        assertThrows(TitleNotValidException.class, () -> surveyService.createSurvey(surveyRequestDTO));

                System.out.println(ex.getMessage());

                assertEquals("Title must be shorter than 20 characters: " + surveyRequestDTO.title(), ex.getMessage());

                verify(surveyRepository, never()).save(any(Survey.class));
            }

            @Test
            void createSurvey_shouldThrowIllegalArgumentException() {

                questionRequestDTO = new QuestionRequestDTO("test question req", "question req for test", List.of());

                surveyRequestDTO = new SurveyRequestDTO
                        ("Test survey 1", "survey for test 1",
                                List.of(questionRequestDTO));

                IllegalArgumentException ex =
                        assertThrows(IllegalArgumentException.class, () -> surveyService.createSurvey(surveyRequestDTO));

                System.out.println(ex.getMessage());

                var invalidQuestions = List.of(questionRequestDTO.description());

                assertEquals("the next questions must ends with a question mark (?): " + invalidQuestions, ex.getMessage());

                verify(surveyRepository, never()).save(any(Survey.class));
            }

            @Test
            void retrieveSurveyByTitle_shouldThrowExceptionIfTitleDoesNotExist(){
                var title = "Test survey 2";

                when(surveyRepository.findByTitle(title)).thenReturn(Optional.empty());

                IllegalArgumentException ex =
                        assertThrows(IllegalArgumentException.class, () -> surveyService.readSurveyByTitle(title));

                System.out.println(ex.getMessage());

                assertTrue(ex.getMessage().contains("Entity not found with: " + title));

                verify(surveyRepository).findByTitle(title);
                verifyNoInteractions(surveyMapper);
            }

            @Test
            void updateSurvey_shouldThrowExceptionIfTitleDoesNotExist(){
                var title = "Test survey 2";

                when(surveyRepository.findByTitle(title)).thenReturn(Optional.empty());

                IllegalArgumentException ex =
                        assertThrows(IllegalArgumentException.class, () -> surveyService.updateSurvey(surveyRequestDTO, title));

                System.out.println(ex.getMessage());

                assertTrue(ex.getMessage().contains("Entity not found with: " + title));

                verify(surveyRepository).findByTitle(title);
                verifyNoInteractions(surveyMapper);
            }

            @Test
            void updateSurvey_shouldThrowTitleValidExceptionForUppercase(){
                var title = "Test survey 2";

                surveyRequestDTO = new SurveyRequestDTO("test survey 2", "survey for test 2", List.of());

                TitleNotValidException ex =
                        assertThrows(TitleNotValidException.class, () -> surveyService.updateSurvey(surveyRequestDTO, title));

                System.out.println(ex.getMessage());

                assertEquals("Title must start with an uppercase letter: " + surveyRequestDTO.title(), ex.getMessage());

                verifyNoInteractions(surveyRepository);
                verifyNoInteractions(surveyMapper);
            }

            @Test
            void updateSurvey_shouldThrowTitleValidExceptionForTitleLength(){
                var title = "Test survey 2";

                surveyRequestDTO = new SurveyRequestDTO("Test survey 2, test survey 2", "survey for test 2", List.of());

                TitleNotValidException ex =
                        assertThrows(TitleNotValidException.class, () -> surveyService.updateSurvey(surveyRequestDTO, title));

                System.out.println(ex.getMessage());

                assertEquals("Title must be shorter than 20 characters: " + surveyRequestDTO.title(), ex.getMessage());

                verifyNoInteractions(surveyRepository);
                verifyNoInteractions(surveyMapper);
            }

            @Test
            void deleteSurvey_shouldThrowExceptionIfIdDoesNotExist() {
                var surveyId = 2L;

                when(surveyRepository.findById(surveyId)).thenReturn(Optional.empty());

                IllegalArgumentException ex =
                        assertThrows(IllegalArgumentException.class, () -> surveyService.deleteSurveyById(surveyId));

                System.out.println(ex.getMessage());

                assertTrue(ex.getMessage().contains("Entity not found with: " + surveyId));

                verify(surveyRepository).findById(surveyId);
            }
        }
    }


    @Nested
    @DisplayName("All related question tests")
    class QuestionTests{

        @Nested
        @DisplayName("All question happy path tests")
        class QuestionHappyPathTests{

            @Test
            void updateQuestion_shouldUpdateQuestionIfExists() {
                var questionId = 1L;
                var title = "Test survey 1";

                var updatedQuestion =
                        new Question(1L, questionRequestDTO.description(), questionRequestDTO.correctAnswer(), questionRequestDTO.options(), survey);

                when(surveyRepository.findByTitle(title)).thenReturn(Optional.of(survey));
                when(questionRepository.save(question)).thenReturn(updatedQuestion);

                questionResponseDTO = QuestionResponseDTO.builder()
                        .id(updatedQuestion.getId())
                        .description(updatedQuestion.getDescription())
                        .correctAnswer(updatedQuestion.getCorrectAnswer())
                        .options(List.of())
                        .build();

                System.out.println(questionResponseDTO);

                when(questionMapper.toDto(updatedQuestion)).thenReturn(questionResponseDTO);

                surveyService.updateQuestion(questionRequestDTO, title, questionId);

                assertAll(
                        () -> assertNotNull(questionResponseDTO),
                        () -> assertEquals(questionResponseDTO.description(), questionRequestDTO.description()),
                        () -> assertEquals(questionResponseDTO.correctAnswer(), questionRequestDTO.correctAnswer()),
                        () -> assertEquals(questionResponseDTO.options(), questionRequestDTO.options())
                );

                verify(surveyRepository).findByTitle(title);
                verify(questionRepository).save(question);
                verify(questionMapper).toDto(updatedQuestion);
            }

            @Test
            void deleteQuestion_shouldDeleteQuestionIfExists() {
                var questionId = 1L;
                var title = "Test survey 1";

                when(surveyRepository.findByTitle(title)).thenReturn(Optional.of(survey));

                doNothing().when(questionRepository).delete(question);

                surveyService.deleteQuestionById(title, questionId);

                assertTrue(survey.getQuestions().isEmpty());

                verify(surveyRepository).findByTitle(title);
                verify(questionRepository).delete(question);
            }
        }

        @Nested
        @DisplayName("All question unhappy path tests")
        class QuestionUnhappyPathTests{

            @Test
            void retrieveAllQuestions_shouldThrownExceptionIfTitleDoesNotExists() {
                var title = "Test survey 2";

                when(questionRepository.findAllBySurveyTitle(title)).thenReturn(Optional.empty());

                IllegalArgumentException ex =
                        assertThrows(IllegalArgumentException.class, () -> surveyService.retrieveAllSurveyQuestions(title));

                System.out.println(ex.getMessage());

                assertTrue(ex.getMessage().contains("Entity not found with: " + title));

                verify(questionRepository).findAllBySurveyTitle(title);
            }

            @Test
            void retrieveQuestionById_shouldThrowExceptionIfTitleDoesNotExist(){
                var questionId = 1L;
                var title = "Test survey 2";

                when(surveyRepository.findByTitle(title)).thenReturn(Optional.empty());

                IllegalArgumentException ex =
                        assertThrows(IllegalArgumentException.class, () -> surveyService.readQuestionById(title, questionId));

                System.out.println(ex.getMessage());

                assertTrue(ex.getMessage().contains("Entity not found with: " + title));

                verify(surveyRepository).findByTitle(title);
            }

            @Test
            void retrieveQuestionById_shouldThrowExceptionIfIdDoesNotExist(){
                var questionId = 2L;
                var title = "Test survey 1";

                when(surveyRepository.findByTitle(title)).thenReturn(Optional.of(survey));

                IllegalArgumentException ex =
                        assertThrows(IllegalArgumentException.class, () -> surveyService.readQuestionById(title, questionId));

                System.out.println(ex.getMessage());

                assertTrue(ex.getMessage().contains("Entity not found with: " + questionId));

                verify(surveyRepository).findByTitle(title);
            }

            @Test
            void updateQuestion_shouldThrowExceptionIfTitleDoesNotExist() {
                var questionId = 1L;
                var title = "Test survey 2";

                when(surveyRepository.findByTitle(title)).thenReturn(Optional.empty());

                IllegalArgumentException ex =
                        assertThrows(IllegalArgumentException.class, () -> surveyService.updateQuestion(questionRequestDTO, title, questionId));

                System.out.println(ex.getMessage());

                assertTrue(ex.getMessage().contains("Entity not found with: " + title));

                verify(surveyRepository).findByTitle(title);
                verifyNoInteractions(questionRepository);
                verifyNoInteractions(questionMapper);
            }

            @Test
            void updateQuestion_shouldThrowExceptionIfIdDoesNotExist() {
                var questionId = 2L;
                var title = "Test survey 1";

                when(surveyRepository.findByTitle(title)).thenReturn(Optional.of(survey));

                IllegalArgumentException ex =
                        assertThrows(IllegalArgumentException.class, () -> surveyService.updateQuestion(questionRequestDTO, title, questionId));

                System.out.println(ex.getMessage());

                assertTrue(ex.getMessage().contains("Entity not found with: " + questionId));

                verify(surveyRepository).findByTitle(title);
                verifyNoInteractions(questionRepository);
                verifyNoInteractions(questionMapper);
            }

            @Test
            void updateQuestion_shouldThrowIllegalArgumentException() {
                var questionId = 1L;
                var title = "Test survey 1";

                questionRequestDTO = new QuestionRequestDTO("test question req", "question req for test", List.of());

                IllegalArgumentException ex =
                        assertThrows(IllegalArgumentException.class, () -> surveyService.updateQuestion(questionRequestDTO, title, questionId));

                System.out.println(ex.getMessage());

                assertEquals("Question description must end with a question mark (?): " +
                        questionRequestDTO.description(), ex.getMessage());

                verifyNoInteractions(questionMapper);
            }

            @Test
            void deleteQuestion_shouldThrowExceptionIfTitleDoesNotExist() {
                var questionId = 1L;
                var title = "Test survey 2";

                when(surveyRepository.findByTitle(title)).thenReturn(Optional.empty());

                IllegalArgumentException ex =
                        assertThrows(IllegalArgumentException.class, () -> surveyService.deleteQuestionById(title, questionId));

                System.out.println(ex.getMessage());

                assertTrue(ex.getMessage().contains("Entity not found with: " + title));

                verify(surveyRepository).findByTitle(title);
                verifyNoInteractions(questionRepository);
            }

            @Test
            void deleteQuestion_shouldThrowExceptionIfIdDoesNotExist() {
                var questionId = 2L;
                var title = "Test survey 1";

                when(surveyRepository.findByTitle(title)).thenReturn(Optional.of(survey));

                IllegalArgumentException ex =
                        assertThrows(IllegalArgumentException.class, () -> surveyService.deleteQuestionById(title, questionId));

                System.out.println(ex.getMessage());

                assertTrue(ex.getMessage().contains("Entity not found with: " + questionId));

                verify(surveyRepository).findByTitle(title);
                verifyNoInteractions(questionRepository);
            }
        }
    }

}
