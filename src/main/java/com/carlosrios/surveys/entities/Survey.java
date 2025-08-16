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
@Table(name = "surveys", uniqueConstraints = @UniqueConstraint(columnNames = "title"))
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    @Size(min = 10, max = 255)
    private String description;
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Question> questions;

    public void addQuestion(Question question) {
        questions.add(question);
        question.setSurvey(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setSurvey(null);
    }

}
