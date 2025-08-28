package com.carlosrios.surveys.controllers;

import com.carlosrios.surveys.dto.QuestionRequestDTO;
import com.carlosrios.surveys.dto.QuestionResponseDTO;
import com.carlosrios.surveys.dto.SurveyRequestDTO;
import com.carlosrios.surveys.dto.SurveyResponseDTO;
import com.carlosrios.surveys.infra.exceptions.ErrorResponse;
import com.carlosrios.surveys.services.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping()
    @Operation(summary = "create Survey", description = "creates a new survey with a list of questions", tags = {"Surveys"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurveyRequestDTO.class))),
            responses = @ApiResponse(responseCode = "201",
                    description = "returns a json of the created survey and the URL to get it",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurveyResponseDTO.class)))
    )
    public ResponseEntity<SurveyResponseDTO> createSurvey(@Valid @RequestBody SurveyRequestDTO requestDTO,
                                                          UriComponentsBuilder uriComponentsBuilder) {
        var response = surveyService.createSurvey(requestDTO);
        final URI url = uriComponentsBuilder.path("/{title}").buildAndExpand(requestDTO.title()).toUri();
        return ResponseEntity.created(url).body(response);
    }

    @GetMapping()
    @Operation(summary = "List All Surveys", description = "List all the surveys with their questions", tags = {"Surveys"},
            responses = @ApiResponse(responseCode = "200", description = "returns a pageable list of all the surveys",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    )
    public ResponseEntity<Page<SurveyResponseDTO>> getAllSurveys(@PageableDefault(size = 12, sort = "id")Pageable pageable) {
        return ResponseEntity.ok(this.surveyService.retrieveAllSurveys(pageable));
    }

    @GetMapping(path = "/{surveyTitle}")
    @Operation(summary = "Get specific Survey", description = "Get an specific survey by title", tags = {"Surveys"},
            responses = {
            @ApiResponse(responseCode = "200", description = "returns the survey with his questions",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurveyResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<SurveyResponseDTO> getSurvey(@Valid @PathVariable String surveyTitle) {
        return ResponseEntity.ok(this.surveyService.readSurveyByTitle(surveyTitle));
    }

    @PutMapping(path = "/{surveyTitle}")
    @Operation(summary = "Update Survey", description = "Updates a survey by title, just updates the survey attributes not the questions",
            tags = {"Surveys"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurveyRequestDTO.class))),
            responses = {
            @ApiResponse(responseCode = "200", description = "returns a json of the updated survey",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SurveyResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<SurveyResponseDTO> updateSurvey(@Valid @RequestBody SurveyRequestDTO requestDTO,@Valid @PathVariable String surveyTitle){
        var response = surveyService.updateSurvey(requestDTO, surveyTitle);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Delete Survey", description = "Deletes a survey by Id", tags = {"Surveys"},
            responses = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
        surveyService.deleteSurveyById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{surveyTitle}/questions")
    @Operation(summary = "create Question", description = "creates a question into a specific survey found by title", tags = {"Questions"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionRequestDTO.class))),
            responses = {
            @ApiResponse(responseCode = "201",
                            description = "returns a json of the created question and the URL to get it",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<QuestionResponseDTO> createQuestion(@Valid @RequestBody QuestionRequestDTO questionRequestDTO,
                                                              @Valid @PathVariable String surveyTitle,
                                                              UriComponentsBuilder uriComponentsBuilder){
        var response = surveyService.addQuestion(questionRequestDTO, surveyTitle);
        final URI url = uriComponentsBuilder.path("survey/{title}/questions/{id}").buildAndExpand(surveyTitle, response.id()).toUri();
        return ResponseEntity.created(url).body(response);
    }

    @GetMapping(path = "/{surveyTitle}/questions")
    @Operation(summary = "List All Questions From Survey", description = "List all the questions from survey specified by title",
            tags = {"Questions"},
            responses = {
            @ApiResponse(responseCode = "200", description = "returns a list of all the questions of a survey",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<List<QuestionResponseDTO>> getAllSurveyQuestions(@Valid @PathVariable String surveyTitle) {
        return ResponseEntity.ok(this.surveyService.retrieveAllSurveyQuestions(surveyTitle));
    }

    @GetMapping(path = "/{surveyTitle}/questions/{id}")
    @Operation(summary = "Get specific Question From Survey", description = "Get an specific question by Id from survey specified by title",
            tags = {"Questions"},
            responses = {
            @ApiResponse(responseCode = "200", description = "returns the question of a survey",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<QuestionResponseDTO> getQuestion(@Valid @PathVariable String surveyTitle, @PathVariable Long id) {
        QuestionResponseDTO questionResponseDTO = surveyService.readQuestionById(surveyTitle, id);
        if (questionResponseDTO == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(questionResponseDTO);
    }

    @PutMapping(path = "/{surveyTitle}/questions/{id}")
    @Operation(summary = "Update Question From Survey", description = "Updates a question by id from survey specified by title",
            tags = {"Questions"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionRequestDTO.class))),
            responses = {
            @ApiResponse(responseCode = "200", description = "returns a json of the updated question",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<QuestionResponseDTO> updateQuestion(@Valid @RequestBody QuestionRequestDTO question
            ,@Valid @PathVariable String surveyTitle, @PathVariable Long id) {
        var response = surveyService.updateQuestion(question, surveyTitle, id);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(path = "/{surveyTitle}/questions/{id}")
    @Operation(summary = "Delete Question", description = "Deletes a question by Id", tags = {"Questions"},
            responses = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<Void> deleteQuestion(@Valid @PathVariable String surveyTitle, @PathVariable Long id) {
        surveyService.deleteQuestionById(surveyTitle, id);
        return ResponseEntity.noContent().build();
    }
}
