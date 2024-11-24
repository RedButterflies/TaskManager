package com.example.taskmanager.service;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.testng.annotations.Test;
import org.mockito.Mockito;

import java.util.Arrays;
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
        Task task2 = new Task();
        task2.setTitle("Task 2");
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        List<Task> tasks = taskService.getAllTasks();

        assertEquals(2, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void saveTask_SavesTask() {
        Task task = new Task();
        task.setTitle("New Task");
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
        when(taskRepository.findAll()).thenReturn(Arrays.asList());

        List<Task> tasks = taskService.getAllTasks();

        assertTrue(tasks.isEmpty());
    }

    @Test
    void saveTask_NullTask_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> taskService.saveTask(null));
    }
}