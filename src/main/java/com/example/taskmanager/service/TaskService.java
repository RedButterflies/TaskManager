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

    // Pobiera zadanie na podstawie ID
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task with ID " + id + " not found"));
    }

    // Aktualizuje istniejące zadanie
    public Task updateTask(Long id, Task updatedTask) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setCategory(updatedTask.getCategory());
        existingTask.setDueDate(updatedTask.getDueDate());
        existingTask.setCompleted(updatedTask.isCompleted());
        return taskRepository.save(existingTask); // Save and return the updated task
    }



    // Pobiera zadania na podstawie statusu ukończenia
    public List<Task> getTasksByCompletionStatus(boolean completed) {
        return taskRepository.findAll().stream()
                .filter(task -> task.isCompleted() == completed)
                .toList();
    }

}
