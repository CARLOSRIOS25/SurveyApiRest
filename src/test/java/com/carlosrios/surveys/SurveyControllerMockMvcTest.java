package com.carlosrios.surveys;

import com.carlosrios.surveys.controllers.SurveyController;
import com.carlosrios.surveys.dto.QuestionRequestDTO;
import com.carlosrios.surveys.dto.QuestionResponseDTO;
import com.carlosrios.surveys.dto.SurveyRequestDTO;
import com.carlosrios.surveys.dto.SurveyResponseDTO;
import com.carlosrios.surveys.entities.Question;
import com.carlosrios.surveys.entities.Survey;
import com.carlosrios.surveys.security.config.SecurityTestConfig;
import com.carlosrios.surveys.services.SurveyService;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@WebMvcTest(controllers = SurveyController.class)
@ActiveProfiles("test")
@Import(SecurityTestConfig.class)
//@AutoConfigureMockMvc(addFilters = false) // works with spring security, instead use a test profile
class SurveyControllerMockMvcTest {

    @MockitoBean
    private SurveyService surveyService;

    @Autowired
    private MockMvc mockMvc;

    // mock -> surveyService.readQuestionById(String title, Long id)

    // fire a request
    // http://localhost:8080/api/surveys/Encuesta de testeo/questions/4  GET
    private static final String SPECIFIC_QUESTION_404 = "http://localhost:8080/api/surveys/Encuesta de testeo/questions/4";

    private static final String SPECIFIC_QUESTION_URL2 = "http://localhost:8080/api/surveys/testSurvey/questions/1";

    private static final String SPECIFIC_SURVEY_URL = "http://localhost:8080/api/surveys/testSurvey";

    private static final String SPECIFIC_SURVEY_URL2 = "http://localhost:8080/api/surveys/1";

    private static final String GENERIC_SURVEY_URL = "http://localhost:8080/api/surveys";

    private static final String GENERIC_QUESTION_URL = "http://localhost:8080/api/surveys/testSurvey/questions";


    @Test
    void readQuestionById_404Scenario() throws Exception {
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(SPECIFIC_QUESTION_404).accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(404, mvcResult.getResponse().getStatus());

        System.out.println(mvcResult.getResponse().getContentAsString());
        System.out.println(mvcResult.getResponse().getStatus());
    }

    @Test
    void readQuestionById_basicScenario() throws Exception {
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(SPECIFIC_QUESTION_URL2).accept(MediaType.APPLICATION_JSON);

        Survey survey = new Survey(1L, "testSurvey", "temporal survey for test with mock",
                List.of(new Question
                        (1L, "QuestionTest", "answer1", Arrays.asList("answer1", "answer2", "answer2"), null)));

        var question = survey.getQuestions().getFirst();

        when(surveyService.readQuestionById("testSurvey", 1L)).thenReturn(
                QuestionResponseDTO.builder()
                        .id(question.getId())
                        .description(question.getDescription())
                        .correctAnswer(question.getCorrectAnswer())
                        .options(question.getOptions())
                        .build());

        String expectedRes = """
                            {"id":1,"description":"QuestionTest","correctAnswer":"answer1","options":["answer1","answer2","answer2"]}
                            """;

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        JSONAssert.assertEquals(expectedRes, mvcResult.getResponse().getContentAsString(), true);

        System.out.println(mvcResult.getResponse().getContentAsString());
        System.out.println(mvcResult.getResponse().getStatus());
    }

    @Test
    void readSurveyByTitle_basicScenario() throws Exception {
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(SPECIFIC_SURVEY_URL).accept(MediaType.APPLICATION_JSON);

        Survey survey = new Survey(1L, "testSurvey", "temporal survey for test with mock",
                List.of(new Question
                        (1L, "QuestionTest", "answer1", Arrays.asList("answer1", "answer2", "answer2"), null)));

        System.out.println(surveyService.readSurveyByTitle("testSurvey"));

        when(surveyService.readSurveyByTitle("testSurvey")).thenReturn(
                SurveyResponseDTO.builder()
                        .id(survey.getId())
                        .title(survey.getTitle())
                        .description(survey.getDescription())
                        .questions(survey.getQuestions().stream().map(q -> QuestionResponseDTO.builder()
                                .id(q.getId())
                                .description(q.getDescription())
                                .correctAnswer(q.getCorrectAnswer())
                                .options(q.getOptions())
                                .build()).toList())
                        .build()
        );

        String expectedRes = """
                            {"id":1,"title":"testSurvey","description":"temporal survey for test with mock","questions":[{"id":1,"description":"QuestionTest","correctAnswer":"answer1","options":["answer1","answer2","answer2"]}]}
                            """;

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertEquals(200, mvcResult.getResponse().getStatus());
        JSONAssert.assertEquals(expectedRes, mvcResult.getResponse().getContentAsString(), true);

        System.out.println(mvcResult.getResponse().getContentAsString());
        System.out.println(mvcResult.getResponse().getStatus());
    }


    //addNewSurvey
    //POST
    //http://localhost:8080/api/surveys
    //201
    //location -> /api/surveys/{title}

    @Test
    void createSurvey_basicScenario() throws Exception {

        String reqBody = """
                    {
                        "title": "Encuesta de testeo 2",
                        "description": "Descripción de la encuesta",
                        "questions": [
                            {
                                "description": "¿Pregunta 1?",
                                "correctAnswer": "Respuesta 1",
                                "options": [
                                    "Respuesta 1",
                                    "Respuesta 2"
                                ]
                            },
                            {
                                "description": "¿Pregunta 2?",
                                "correctAnswer": "Respuesta 2",
                                "options": [
                                    "Respuesta 1",
                                    "Respuesta 2"
                                ]
                            }
                        ]
                    }
                    """;

        SurveyResponseDTO responseDto = SurveyResponseDTO.builder()
                .id(9L)
                .title("Encuesta de testeo 2")
                .description("Descripción de la encuesta")
                .questions(List.of(
                        QuestionResponseDTO.builder()
                                .id(6L)
                                .description("¿Pregunta 1?")
                                .correctAnswer("Respuesta 1")
                                .options(List.of("Respuesta 1", "Respuesta 2"))
                                .build(),
                        QuestionResponseDTO.builder()
                                .id(7L)
                                .description("¿Pregunta 2?")
                                .correctAnswer("Respuesta 2")
                                .options(List.of("Respuesta 1", "Respuesta 2"))
                                .build()
                ))
                .build();

        when(surveyService.createSurvey(argThat(req ->
                req.title().equals("Encuesta de testeo 2") &&
                        req.questions().size() == 2
        ))).thenReturn(responseDto);

        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.post(GENERIC_SURVEY_URL)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(reqBody).contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        System.out.println("mvcResult: " + mvcResult.getResponse().getContentAsString());

        String expectedResult = """
                                {
                                    "id":9,
                                    "title":"Encuesta de testeo 2",
                                    "description":"Descripción de la encuesta",
                                    "questions":
                                      [
                                        {"id":6,"description":"¿Pregunta 1?","correctAnswer":"Respuesta 1","options":["Respuesta 1","Respuesta 2"]},
                                        {"id":7,"description":"¿Pregunta 2?","correctAnswer":"Respuesta 2","options":["Respuesta 1","Respuesta 2"]}
                                      ]
                                }
                                """;

        assertEquals(201, mvcResult.getResponse().getStatus());
        assertEquals("http://localhost:8080/api/surveys/Encuesta%20de%20testeo%202", mvcResult.getResponse().getHeader("Location"));

        JSONAssert.assertEquals(expectedResult, mvcResult.getResponse().getContentAsString(), true);

    }

    //add question to existing survey
    //POST
    //http://localhost:8080/api/surveys/{title}/questions
    //201
    //location -> /api/surveys/{title}/questions/{id}

    @Test
    void createQuestion_basicScenario() throws Exception {
        String reqBody = """
                    {
                        "description": "Pregunta test",
                        "correctAnswer": "Respuesta 1",
                        "options": [
                            "Respuesta 1",
                            "Respuesta 2",
                            "Respuesta 3"
                        ]
                    }
                    """;

        when(surveyService.addQuestion(any(QuestionRequestDTO.class), anyString())).thenReturn(QuestionResponseDTO.builder()
                .id(6L)
                .description("Pregunta test")
                .correctAnswer("Respuesta 1")
                .options(List.of("Respuesta 1", "Respuesta 2", "Respuesta 3"))
                .build());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(GENERIC_QUESTION_URL).accept(MediaType.APPLICATION_JSON)
                .content(reqBody).contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(201, mvcResult.getResponse().getStatus());
        String location = mvcResult.getResponse().getHeader("Location");
        System.out.println(location);
        assert location != null;
        assertTrue(location.contains("/api/surveys/testSurvey/questions/6"));

    }

    //retrieveAllSurveys
    //GET
    //http://localhost:8080/api/surveys
    //200
    //paginated response

    @Test
    void retrieveAllSurveys_basicScenario() throws Exception {
        RequestBuilder requestBuilder
                = MockMvcRequestBuilders.get(GENERIC_SURVEY_URL).accept(MediaType.APPLICATION_JSON);

        SurveyResponseDTO dto1 = SurveyResponseDTO.builder()
                .id(1L)
                .title("testSurvey1")
                .description("temporal survey for test with mock")
                .questions(List.of(
                        QuestionResponseDTO.builder()
                                .id(1L)
                                .description("QuestionTest1")
                                .correctAnswer("answer1")
                                .options(Arrays.asList("answer1", "answer2", "answer2"))
                                .build()))
                .build();

        SurveyResponseDTO dto2 = SurveyResponseDTO.builder()
                .id(2L)
                .title("testSurvey2")
                .description("temporal survey for test with mock")
                .questions(List.of(
                        QuestionResponseDTO.builder()
                                .id(2L)
                                .description("QuestionTest2")
                                .correctAnswer("answer1")
                                .options(Arrays.asList("answer1", "answer2", "answer2"))
                                .build()))
                .build();

        List<SurveyResponseDTO> dtos = List.of(dto1, dto2);

        when(surveyService.retrieveAllSurveys(any())).thenReturn(
                new PageImpl<>(dtos, PageRequest.of(0, 10), dtos.size()));

        String expectedRes = """
                            {
                              "content":
                              [{"id":1,"title":"testSurvey1","description":"temporal survey for test with mock","questions":[{"id":1,"description":"QuestionTest1","correctAnswer":"answer1","options":["answer1","answer2","answer2"]}]},
                              {"id":2,"title":"testSurvey2","description":"temporal survey for test with mock","questions":[{"id":2,"description":"QuestionTest2","correctAnswer":"answer1","options":["answer1","answer2","answer2"]}]}],
                              "pageable":{"pageNumber":0,"pageSize":10,"sort":{"empty":true,"sorted":false,"unsorted":true},"offset":0,"unpaged":false,"paged":true},"last":true,"totalPages":1,"totalElements":2,
                              "sort":{"empty":true,"sorted":false,"unsorted":true},"size":10,"number":0,"first":true,"numberOfElements":2,"empty":false
                            }
                            """;

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
        System.out.println(mvcResult.getResponse().getStatus());
        assertEquals(200, mvcResult.getResponse().getStatus());
        JSONAssert.assertEquals(expectedRes, mvcResult.getResponse().getContentAsString(), false);
    }

    @Test
    void updateSurvey_basicScenario() throws Exception {

        var reqBody = """
                    {
                        "title": "updatedTestSurvey",
                        "description": "Updated Survey Test"
                    }
                    """;

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(SPECIFIC_SURVEY_URL).accept(MediaType.APPLICATION_JSON).content(reqBody).contentType(MediaType.APPLICATION_JSON);

        when(surveyService.updateSurvey(any(SurveyRequestDTO.class), anyString())).thenReturn(SurveyResponseDTO.builder()
                        .id(1L)
                        .title("updatedTestSurvey")
                        .description("Updated Survey Test")
                        .questions(List.of()).build());

        var expectedRes = """
                        {
                            "id":1,
                            "title":"updatedTestSurvey",
                            "description":"Updated Survey Test",
                            "questions": []
                        }
                        """;

        MvcResult mvcResult =  mockMvc.perform(requestBuilder).andReturn();

        System.out.println(mvcResult.getResponse().getStatus());
        System.out.println(mvcResult.getResponse().getContentAsString());

        assertEquals(200, mvcResult.getResponse().getStatus());
        JSONAssert.assertEquals(expectedRes, mvcResult.getResponse().getContentAsString(), false);
    }

    @Test
    void updateQuestion_basicScenario() throws Exception {

        var reqBody = """
                    {
                        "description": "Updated QuestionTest",
                        "correctAnswer": "updatedAnswer1",
                        "options": [
                            "updatedAnswer1",
                            "answer2",
                            "answer3"
                        ]
                    }
                    """;

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(SPECIFIC_QUESTION_URL2).accept(MediaType.APPLICATION_JSON).content(reqBody).contentType(MediaType.APPLICATION_JSON);

        when(surveyService.updateQuestion(
                new QuestionRequestDTO("Updated QuestionTest", "updatedAnswer1",
                        List.of("updatedAnswer1", "answer2", "answer3")), "testSurvey", 1L))
                .thenReturn(QuestionResponseDTO.builder()
                .id(1L)
                .description("Updated QuestionTest")
                .correctAnswer("updatedAnswer1")
                .options(List.of("updatedAnswer1", "answer2","answer3"))
                .build());

        var expectedRes = """
                        {
                            "id":1,
                            "description":"Updated QuestionTest",
                            "correctAnswer":"updatedAnswer1",
                            "options": [
                                "updatedAnswer1",
                                "answer2",
                                "answer3"
                            ]
                        }
                        """;

        MvcResult mvcResult =  mockMvc.perform(requestBuilder).andReturn();

        System.out.println(mvcResult.getResponse().getStatus());
        System.out.println(mvcResult.getResponse().getContentAsString());

        assertEquals(200, mvcResult.getResponse().getStatus());
        JSONAssert.assertEquals(expectedRes, mvcResult.getResponse().getContentAsString(), true);
    }

    @Test
    void deleteSurvey_basicScenario() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(SPECIFIC_SURVEY_URL2).accept(MediaType.APPLICATION_JSON);

        doNothing().when(surveyService).deleteSurveyById(1L);

        MvcResult mvcResult =  mockMvc.perform(requestBuilder).andReturn();

        System.out.println(mvcResult.getResponse().getStatus());
        assertEquals(204, mvcResult.getResponse().getStatus());

        verify(surveyService, times(1)).deleteSurveyById(1L);
    }

    @Test
    void deleteQuestion_basicScenario() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(SPECIFIC_QUESTION_URL2).accept(MediaType.APPLICATION_JSON);

        doNothing().when(surveyService).deleteQuestionById("testSurvey", 1L);

        MvcResult mvcResult =  mockMvc.perform(requestBuilder).andReturn();

        System.out.println(mvcResult.getResponse().getStatus());
        assertEquals(204, mvcResult.getResponse().getStatus());

        verify(surveyService, times(1)).deleteQuestionById("testSurvey", 1L);
    }
}
