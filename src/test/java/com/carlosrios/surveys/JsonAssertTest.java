package com.carlosrios.surveys;

import com.carlosrios.surveys.security.config.SecurityTestConfig;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(SecurityTestConfig.class)
class JsonAssertTest {

    private static final String SPECIFIC_URL1 = "/api/surveys/survey test 1/questions/1";

    private static final String SPECIFIC_URL2 = "/api/surveys/survey test 2/questions";

    @Autowired //automatically: http://localhost:RANDOM_PORT
    private TestRestTemplate restTemplate = new TestRestTemplate(); //to fire a rest request

    @Test
    void jsonAssert_readQuestionById() throws JSONException {
        ResponseEntity<String> resEntity = restTemplate.getForEntity(SPECIFIC_URL1, String.class);

        String expectedRes = """
                            {
                                "id":1,
                                "description":"question 1 for survey 1",
                                "correctAnswer":"correct answer 1",
                                "options":
                                    ["correct answer 1","correct answer 2","correct answer 3"]
                            }
                            """;

        //status response is it 200
        assertTrue(resEntity.getStatusCode().is2xxSuccessful());

        //[content-type:"application/json", date:"Tue, 12 Aug 2025 20:05:04 GMT", transfer-encoding:"chunked"]
        assertEquals("application/json", Objects.requireNonNull(resEntity.getHeaders().get("content-type")).getFirst());

        //json ignore the spaces automatically
        JSONAssert.assertEquals(expectedRes, resEntity.getBody(), true);
    }

    @Test
    void retrieveAllSurveyQuestionsIT_jsonTest() throws JSONException {
        ResponseEntity<String> resEntity = restTemplate.getForEntity(SPECIFIC_URL2, String.class);


        System.out.println(resEntity.getBody());
        String expectedRes = """
                            [
                                {"id":4,"description":"question 1 for survey 2","correctAnswer":"correct answer 1","options":["correct answer 1","correct answer 2","correct answer 3"]},
                                {"id":5,"description":"question 2 for survey 2","correctAnswer":"correct answer 2","options":["correct answer 1","correct answer 2","correct answer 3"]},
                                {"id":6,"description":"question 3 for survey 2","correctAnswer":"correct answer 3","options":["correct answer 1","correct answer 2","correct answer 3"]}
                            ]
                            """;

        //status response is it 200
        assertTrue(resEntity.getStatusCode().is2xxSuccessful());

        //[content-type:"application/json", date:"Tue, 12 Aug 2025 20:05:04 GMT", transfer-encoding:"chunked"]
        assertEquals("application/json", Objects.requireNonNull(resEntity.getHeaders().get("content-type")).getFirst());

        //json ignore the spaces automatically
        JSONAssert.assertEquals(expectedRes, resEntity.getBody(), false); // with false you can personalize the expected response make it strict
    }

    @Test
    void createSurveyIT_jsonTest() {

        String reqBody = """
                        {
                          "title": "Encuesta de test03",
                          "description": "Descripción de la encuesta",
                          "questions": [
                            {
                              "description": "¿Pregunta 1?",
                              "correctAnswer": "Respuesta 1",
                              "options": ["Respuesta 1", "Respuesta 2"]
                            },
                            {
                              "description": "¿Pregunta 2?",
                              "correctAnswer": "Respuesta 2",
                              "options": ["Respuesta 1", "Respuesta 2"]
                            }
                          ]
                        }
                        """;

        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");

        HttpEntity<String> httpEntity = new HttpEntity<>(reqBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange("/api/surveys", HttpMethod.POST, httpEntity, String.class);

        //status response is it 201
        System.out.println(responseEntity.getBody());
        System.out.println(responseEntity.getStatusCode());
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());

        String locationHeader = Objects.requireNonNull(responseEntity.getHeaders().get("Location")).getFirst();
        assertTrue(locationHeader.contains("/api/surveys/Encuesta%20de%20test03"));
        //clean up the post
        restTemplate.delete(locationHeader);

    }


    @Test
    void createQuestionIT_jsonTest() {

        String reqBody = """
                            {
                              "description": "¿Pregunta 4?",
                              "correctAnswer": "Respuesta 4",
                              "options": ["Respuesta 1", "Respuesta 2", "Respuesta 3, Respuesta 4"]
                            }
                        """;

        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");

        HttpEntity<String> httpEntity = new HttpEntity<>(reqBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange("/api/surveys/survey test 1/questions", HttpMethod.POST, httpEntity, String.class);

        //status response is it 201
        System.out.println(responseEntity.getBody());
        System.out.println(responseEntity.getStatusCode());
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());

        String locationHeader = Objects.requireNonNull(responseEntity.getHeaders().get("Location")).getFirst();
        assertTrue(locationHeader.contains("/api/surveys/survey%20test%201/questions/10"));
        //clean up the post
        restTemplate.delete(locationHeader);

    }

}
