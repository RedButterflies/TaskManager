package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
@Validated
public class TaskController {
    private final TaskService taskService;

    //Konstruktor wstrzykujacy TaskService, co umozliwia obsługe logiki biznesowej
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Wyswietla liste wszystkich zadan na stronie task-list
    @GetMapping
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "task-list";
    }

    // Udostepnia liste zadan w formacie JSON (dla API)
    @GetMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Task> getTasksAsJson() {
        return taskService.getAllTasks();
    }

    //Wyswietla formularz do dodania nowego zadania
    @GetMapping("/new")
    public String newTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "task-form";
    }

    // Obsluguje zapis nowego lub edytowanego zadania
    @PostMapping(consumes = "application/x-www-form-urlencoded")
    public String saveTask(@Valid @ModelAttribute("task") Task task, BindingResult bindingResult, Model model) {
        // Walidacja formularza; jesli wystapią bledy, ponownie wyswietla formularz
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            model.addAttribute("task", task);
            return "task-form";
        }

        //Zapisuje zadanie w bazie danych
        taskService.saveTask(task);
        return "redirect:/tasks";
    }

    //Obsluguje usuwanie zadania na podstawie jego id
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }
}

