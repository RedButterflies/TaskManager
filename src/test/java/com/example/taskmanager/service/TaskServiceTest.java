package com.example.taskmanager.service;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    private TaskRepository taskRepository;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskRepository = Mockito.mock(TaskRepository.class);
        taskService = new TaskService(taskRepository);
    }

    @Test
    void getAllTasks_ReturnsAllTasks() {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setCategory("Category 1");
        task1.setDueDate(LocalDate.of(2024, 1, 1));
        task1.setCompleted(false);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setCategory("Category 2");
        task2.setDueDate(LocalDate.of(2024, 1, 2));
        task2.setCompleted(true);

        when(taskRepository.findAll()).thenReturn(List.of(task1, task2));

        List<Task> tasks = taskService.getAllTasks();

        assertEquals(2, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitle());
        assertEquals("Task 2", tasks.get(1).getTitle());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void saveTask_SavesTask() {
        Task task = new Task();
        task.setTitle("New Task");
        task.setCategory("Category 1");
        task.setDueDate(LocalDate.of(2024, 1, 1));
        task.setCompleted(false);

        when(taskRepository.save(task)).thenReturn(task);

        Task savedTask = taskService.saveTask(task);

        assertNotNull(savedTask);
        assertEquals("New Task", savedTask.getTitle());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void deleteTask_DeletesTask() {

        Long taskId = 1L;

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void getAllTasks_ReturnsEmptyListIfNoTasks() {
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        List<Task> tasks = taskService.getAllTasks();

        assertTrue(tasks.isEmpty());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void saveTask_NullTask_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> taskService.saveTask(null));
    }
}
