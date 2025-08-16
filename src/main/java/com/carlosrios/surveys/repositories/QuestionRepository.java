package com.carlosrios.surveys.repositories;

import com.carlosrios.surveys.entities.Question;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Optional<List<Question>> findAllBySurveyTitle(@NotBlank String title);
}
