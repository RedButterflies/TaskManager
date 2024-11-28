package com.example.taskmanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
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

    //Gettery i settery do dostępu i modyfikacji pól
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
