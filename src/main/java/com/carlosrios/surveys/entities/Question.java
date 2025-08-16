package com.carlosrios.surveys.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "questions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(min = 10, max = 255)
    private String description;
    @NotBlank
    private String correctAnswer;
    @ElementCollection
    private List<String> options;
    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)//foreign key
    private Survey survey;

}
