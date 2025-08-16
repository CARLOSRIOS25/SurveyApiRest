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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(SecurityTestConfig.class)
class JsonAssertTest {

    private static final String SPECIFIC_URL1 = "/api/surveys/Encuesta sobre Spring Framework/questions/1";

    private static final String SPECIFIC_URL2 = "/api/surveys/Encuesta sobre Spring Framework/questions";

    @Autowired //automatically: http://localhost:RANDOM_PORT
    private TestRestTemplate restTemplate = new TestRestTemplate(); //to fire a rest request

    @Test
    void jsonAssert_readQuestionById() throws JSONException {
        ResponseEntity<String> resEntity = restTemplate.getForEntity(SPECIFIC_URL1, String.class);

        String expectedRes = """   
                            {
                                "id": 1,
                                "description": "¿Cuál es el propósito principal de Spring Framework?",
                                "correctAnswer": "Facilitar el desarrollo de aplicaciones Java",
                                "options": [
                                    "Facilitar el desarrollo de aplicaciones Java",
                                    "Proveer un servidor web",
                                    "Gestionar bases de datos"
                                ]
                            }
                            """;

        //status response is it 200
        assertTrue(resEntity.getStatusCode().is2xxSuccessful());

        //[content-type:"application/json", date:"Tue, 12 Aug 2025 20:05:04 GMT", transfer-encoding:"chunked"]
        assertEquals("application/json",resEntity.getHeaders().get("content-type").get(0));

        //json ignore the spaces automatically
        JSONAssert.assertEquals(expectedRes, resEntity.getBody(), true); // with false you can personalize the expected response
    }

    @Test
    void retrieveAllSurveyQuestionsIT_jsonTest() throws JSONException {
        ResponseEntity<String> resEntity = restTemplate.getForEntity(SPECIFIC_URL2, String.class);

        String expectedRes = """
                            [
                                {
                                    "id": 1,
                                    "description": "¿Cuál es el propósito principal de Spring Framework?",
                                    "correctAnswer": "Facilitar el desarrollo de aplicaciones Java",
                                    "options": [
                                        "Facilitar el desarrollo de aplicaciones Java",
                                        "Proveer un servidor web",
                                        "Gestionar bases de datos"
                                    ]
                                },
                                {
                                    "id": 2,
                                    "description": "¿Qué anotación se usa para inyección de dependencias en Spring?",
                                    "correctAnswer": "@Autowired",
                                    "options": [
                                        "@Autowired",
                                        "@Inject",
                                        "@Resource"
                                    ]
                                }
                            ]
                            """;

        //status response is it 200
        assertTrue(resEntity.getStatusCode().is2xxSuccessful());

        //[content-type:"application/json", date:"Tue, 12 Aug 2025 20:05:04 GMT", transfer-encoding:"chunked"]
        assertEquals("application/json",resEntity.getHeaders().get("content-type").get(0));

        //json ignore the spaces automatically
        JSONAssert.assertEquals(expectedRes, resEntity.getBody(), false); // with false you can personalize the expected response
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

        ResponseEntity<String> responseEntity = restTemplate.exchange("/", HttpMethod.POST, httpEntity, String.class);

        //status response is it 201
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());

        String locationHeader = responseEntity.getHeaders().get("Location").get(0);
        assertTrue(locationHeader.contains("/api/surveys/Encuesta%20de%20test03"));
        //clean up the post
        restTemplate.delete(locationHeader);

    }

}
