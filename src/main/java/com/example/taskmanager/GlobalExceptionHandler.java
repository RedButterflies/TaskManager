package com.example.taskmanager;

import jakarta.validation.ConstraintViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    //Obsluguje wyjatki walidacyjne(ConstraintViolationException)
    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolationException(ConstraintViolationException ex, Model model) {
        //Dodaje komunikat o bledzie walidacji do modelu, aby mozna bylo wyświetlic go w widoku
        model.addAttribute("errorMessage", "Validation error: " + ex.getMessage());
        return "error-page"; // Przekierowuje uzytkownika na strone bledu
    }
}
