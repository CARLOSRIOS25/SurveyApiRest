package com.carlosrios.surveys.controllers;

import com.carlosrios.surveys.dto.QuestionRequestDTO;
import com.carlosrios.surveys.dto.QuestionResponseDTO;
import com.carlosrios.surveys.dto.SurveyRequestDTO;
import com.carlosrios.surveys.dto.SurveyResponseDTO;
import com.carlosrios.surveys.services.SurveyService;
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
    public ResponseEntity<SurveyResponseDTO> createSurvey(@Valid @RequestBody SurveyRequestDTO requestDTO,
                                                          UriComponentsBuilder uriComponentsBuilder) {
        var response = surveyService.createSurvey(requestDTO);
        final URI url = uriComponentsBuilder.path("/{title}").buildAndExpand(requestDTO.title()).toUri();
        return ResponseEntity.created(url).body(response);
    }

    @GetMapping()
    public ResponseEntity<Page<SurveyResponseDTO>> getAllSurveys(@PageableDefault(size = 12, sort = "id")Pageable pageable) {
        return ResponseEntity.ok(this.surveyService.retrieveAllSurveys(pageable));
    }

    @GetMapping(path = "/{surveyTitle}")
    public ResponseEntity<SurveyResponseDTO> getSurvey(@Valid @PathVariable String surveyTitle) {
        return ResponseEntity.ok(this.surveyService.readSurveyByTitle(surveyTitle));
    }

    @PutMapping(path = "/{surveyTitle}")
    public ResponseEntity<SurveyResponseDTO> updateSurvey(@Valid @RequestBody SurveyRequestDTO requestDTO,@Valid @PathVariable String surveyTitle){
        var response = surveyService.updateSurvey(requestDTO, surveyTitle);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
        surveyService.deleteSurveyById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{surveyTitle}/questions")
    public ResponseEntity<QuestionResponseDTO> createQuestion(@Valid @RequestBody QuestionRequestDTO questionRequestDTO,@Valid @PathVariable String surveyTitle,
                                                              UriComponentsBuilder uriComponentsBuilder){
        var response = surveyService.addQuestion(questionRequestDTO, surveyTitle);
        final URI url = uriComponentsBuilder.path("survey/{title}/questions/{id}").buildAndExpand(surveyTitle, response.id()).toUri();
        return ResponseEntity.created(url).body(response);
    }

    @GetMapping(path = "/{surveyTitle}/questions")
    public ResponseEntity<List<QuestionResponseDTO>> getAllSurveyQuestions(@Valid @PathVariable String surveyTitle) {
        return ResponseEntity.ok(this.surveyService.retrieveAllSurveyQuestions(surveyTitle));
    }

    @GetMapping(path = "/{surveyTitle}/questions/{id}")
    public ResponseEntity<QuestionResponseDTO> getQuestion(@Valid @PathVariable String surveyTitle, @PathVariable Long id) {

        QuestionResponseDTO questionResponseDTO = surveyService.readQuestionById(surveyTitle, id);
        if (questionResponseDTO == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(questionResponseDTO);
    }

    @PutMapping(path = "/{surveyTitle}/questions/{id}")
    public ResponseEntity<QuestionResponseDTO> updateQuestion(@Valid @RequestBody QuestionRequestDTO question
            ,@Valid @PathVariable String surveyTitle, @PathVariable Long id) {
        var response = surveyService.updateQuestion(question, surveyTitle, id);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(path = "/{surveyTitle}/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(@Valid @PathVariable String surveyTitle, @PathVariable Long id) {
        surveyService.deleteQuestionById(surveyTitle, id);
        return ResponseEntity.noContent().build();
    }
}
