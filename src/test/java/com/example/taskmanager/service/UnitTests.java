package com.example.taskmanager.service;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UnitTests {

    private TaskRepository taskRepository;
    private TaskService taskService;

    //Przygotowanie srodowiska testowego przed kazdym testem
    @BeforeEach
    void setUp() {
        taskRepository = Mockito.mock(TaskRepository.class);//Mockowanie repozytorium
        taskService = new TaskService(taskRepository); //Inicjalizacja serwisu z mockiem
    }

    //Test:Pobieranie wszystkich zadań
    @Test
    void getAllTasks_ReturnsAllTasks() {
        //Symulowanie dwoch zadań w repozytorium
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

        when(taskRepository.findAll()).thenReturn(List.of(task1, task2)); //Mockowanie zachowania repozytorium

        List<Task> tasks = taskService.getAllTasks();//Wywolanie metody serwisu

        //Sprawdzenie zadan w wyniku
        assertEquals(2, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitle());
        assertEquals("Task 2", tasks.get(1).getTitle());
        verify(taskRepository, times(1)).findAll();//Upewnienie się, ze repozytorium zostało wywolane raz
    }

    //Test:Zapisywanie nowego zadania
    @Test
    void saveTask_SavesTask() {
        Task task = new Task();
        task.setTitle("New Task");
        task.setCategory("Category 1");
        task.setDueDate(LocalDate.of(2024, 1, 1));
        task.setCompleted(false);

        when(taskRepository.save(task)).thenReturn(task);//Mockowanie zapisu w repozytorium

        Task savedTask = taskService.saveTask(task);//Wywolanie metody serwisu

        assertNotNull(savedTask);//Sprawdzenie, czy zadanie zostalo zapisane
        assertEquals("New Task", savedTask.getTitle()); //Walidacja danych zapisanego zadania
        verify(taskRepository, times(1)).save(task);//weryfikuje,ze metoda save() z mockowanego obiektu taskRepository zostala wywołana dokladnie raz z argumentem task
    }

    //Test: Usuwanie zadania
    @Test
    void deleteTask_DeletesTask() {
        Long taskId = 1L;

        taskService.deleteTask(taskId); //Wywolanie metody serwisu

        verify(taskRepository, times(1)).deleteById(taskId);//Upewnienie sie, ze zadanie zostalo usuniete
    }

    //Test: Obsluga pustej listy zadan
    @Test
    void getAllTasks_ReturnsEmptyListIfNoTasks() {
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());//Mockowanie pustej listy

        List<Task> tasks = taskService.getAllTasks();//Wywolanie metody serwisu

        assertTrue(tasks.isEmpty());//Sprawdzenie,czy lista jest pusta
        verify(taskRepository, times(1)).findAll();//Upewnienie sie,ze repozytorium zostało wywolane
    }

    //Test:Obsluga bledu przy probie zapisu null jako zadania
    @Test
    void saveTask_NullTask_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> taskService.saveTask(null));//Walidacja rzucenia wyjątku
    }

    //Test:Sprawdza, czy metoda `getTaskById` w serwisie poprawnie zwraca zadanie na podstawie podanego id
    @Test
    void getTaskById_ReturnsTask() {
        //Tworzymy zadanie testowe i ustawiamy jego id
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Sample Task");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task)); //Mockujemy zwracanie zadania przez repozytorium

        // Wywołanie metody serwisu
        Task result = taskService.getTaskById(1L);

        // Walidacja: Sprawdzamy, czy metoda zwróciła poprawne zadanie
        assertNotNull(result);
        assertEquals("Sample Task", result.getTitle());
    }

    //Test:Sprawdza, czy metoda `updateTask` w serwisie poprawnie aktualizuje dane zadania
    @Test
    void updateTask_UpdatesTaskDetails() {
        //Tworzymy istniejące i zaktualizowane obiekty zadania
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Old Title");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask)); //Mockowanie istniejącego zadania

        Task updatedTask = new Task();
        updatedTask.setTitle("New Title");
        updatedTask.setCategory("New Category");
        updatedTask.setDueDate(LocalDate.now());
        updatedTask.setCompleted(true);

        when(taskRepository.save(existingTask)).thenReturn(existingTask); //Mockowanie zachowania metody save

        // Wywolanie metody serwisowej
        Task result = taskService.updateTask(1L, updatedTask);

        //Weryfikujemy, czy zadanie zostało zaktualizowane
        assertNotNull(result); // Sprawdzenie, czy wynik nie jest nullem
        assertEquals("New Title", result.getTitle());
        assertEquals("New Category", result.getCategory());
        assertTrue(result.isCompleted());

        //Upewnienie się, ze metoda zapisu została wywołana
        verify(taskRepository, times(1)).save(existingTask);
    }


    //Test:Sprawdza,czy metoda `getTasksByCompletionStatus` w serwisie poprawnie filtruje zadania,zwracając tylko te,ktore mają ustawiony status ukonczenia na zakonczone.
    @Test
    void getTasksByCompletionStatus_ReturnsFilteredTasks() {
        //Tworzymy listę zadan i ustawiamy ich status ukonczenia
        Task task1 = new Task();
        task1.setCompleted(true);
        Task task2 = new Task();
        task2.setCompleted(false);
        when(taskRepository.findAll()).thenReturn(List.of(task1, task2)); //Mockujemy zwracanie wszystkich zadań

        //Wywołanie metody serwisu z filtrowaniem
        List<Task> completedTasks = taskService.getTasksByCompletionStatus(true);

        // Walidacja: Sprawdzamy, czy metoda zwraca tylko ukonczone zadania
        assertEquals(1, completedTasks.size());
        assertTrue(completedTasks.get(0).isCompleted());
    }

}
