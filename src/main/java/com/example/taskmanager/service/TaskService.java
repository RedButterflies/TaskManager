package com.example.taskmanager.service;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    //Konstruktor wstrzykujacy TaskRepository- umozliwia dostep do warstwy danych
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Pobiera liste wszystkich zadan z bazy danych
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Zapisuje zadanie w bazie danych i sprawdza, czy obiekt zadania nie jest pusty
    public Task saveTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        return taskRepository.save(task);
    }

    // Usuwa zadanie na podstawie jego id
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
