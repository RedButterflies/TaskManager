package com.example.taskmanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor // Automatyczne wygenerowanie konstruktora bezargumentowego
public class Task {
    // Pole kluczowe z automatycznym generowaniem unikalnego identyfikatora
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Tytul zadania -wymagane pole, walidowane
    @NotBlank(message = "Title is required")
    private String title;

    // Kategoria zadania- wymagane pole, walidowane
    @NotBlank(message = "Category is required")
    private String category;

    // Termin zadania -wymagane pole, walidowane
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    // Status ukonczenia zadania(domyslnie false)
    private boolean completed;

}
