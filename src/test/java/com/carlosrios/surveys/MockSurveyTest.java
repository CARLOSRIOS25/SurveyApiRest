package com.carlosrios.surveys;

import com.carlosrios.surveys.controllers.SurveyController;
import com.carlosrios.surveys.dto.QuestionResponseDTO;
import com.carlosrios.surveys.entities.Question;
import com.carlosrios.surveys.entities.Survey;
import com.carlosrios.surveys.security.config.SecurityTestConfig;
import com.carlosrios.surveys.services.SurveyService;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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
import static org.mockito.Mockito.when;


@WebMvcTest(controllers = SurveyController.class)
@ActiveProfiles("test")
@Import(SecurityTestConfig.class)
//@AutoConfigureMockMvc(addFilters = false) // works with spring security, instead use a test profile
class MockSurveyTest {

    @MockitoBean
    private SurveyService surveyService;

    @Autowired
    private MockMvc mockMvc;

    // mock -> surveyService.readQuestionById(String title, Long id)

    // fire a request
    // http://localhost:8080/api/surveys/Encuesta de testeo/questions/4  GET
    private static final String SPECIFIC_QUESTION_URL = "http://localhost:8080/api/surveys/Encuesta de testeo/questions/4";

    private static final String SPECIFIC_QUESTION_URL2 = "http://localhost:8080/api/surveys/testSurvey/questions/1";


    @Test
    void readQuestionById_404Scenario() throws Exception {
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(SPECIFIC_QUESTION_URL).accept(MediaType.APPLICATION_JSON);

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
}
