package com.carlosrios.surveys.repositories;

import com.carlosrios.surveys.entities.Survey;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    Optional<Survey> findByTitle(@NotBlank String title);

}
