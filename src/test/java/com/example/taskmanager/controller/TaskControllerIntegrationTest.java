package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;//Sluzy do symulacji żądan HTTP w testach

    @Autowired
    private TaskRepository taskRepository; //Repozytorium do obsługi danych w testach

    //Test: Pobiera liste zadan i sprawdza,czy zadanie jest widoczne w odpowiedzi
    @Test
    void getTasks_ReturnsTasks() throws Exception {
        Task task = new Task();
        task.setTitle("Sample Task");
        task.setCategory("Work");
        task.setDueDate(LocalDate.now().plusDays(1));
        taskRepository.save(task);
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk()) //Oczekiwany status HTTP: 200 (OK)
                .andExpect(content().string(containsString("Sample Task"))); // Sprawdza,czy zadanie jest w odpowiedzi
    }

    //Test: Tworzy nowe zadanie za pomocą żądania POST i sprawdza przekierowanie
    @Test
    void postTask_CreatesTask() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Test Task")
                        .param("category", "Test Category")
                        .param("dueDate", "2024-11-30")
                        .param("completed", "false"))
                .andExpect(status().is3xxRedirection()); // Oczekiwane przekierowanie po sukcesie
    }

    // Test: Usuwa zadanie za pomocą żądania GET i sprawdza przekierowanie
    @Test
    void deleteTask_RemovesTask() throws Exception {
        Task task = new Task();
        task.setTitle("Task to Delete");
        task.setCategory("Work");
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setCompleted(false);
        task = taskRepository.save(task); //Zapisuje zadanie w bazie
        mockMvc.perform(get("/tasks/delete/" + task.getId()))
                .andExpect(status().is3xxRedirection()); //Oczekiwane przekierowanie po usunięciu
    }

    //Test: Sprawdza, czy odpowiedz jest pusta, gdy brak zadan
    @Test
    void getTasks_ReturnsEmptyWhenNoTasks() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk()) // Oczekiwany status HTTP: 200 (OK)
                .andExpect(content().string(not(containsString("<tr><td")))); //Sprawdza, czy lista zadan jest pusta
    }

    //Test:Sprawdza walidacje przy tworzeniu zadania(niepełne dane)
    @Test
    void postTask_ValidationFailure() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "") //Puste pola
                        .param("category", "")
                        .param("dueDate", "")
                        .param("completed", "false"))
                .andExpect(status().isOk()) // Oczekiwany status HTTP: 200 (OK)
                .andExpect(view().name("error-page"))//Widok strony bledu
                .andExpect(model().attributeExists("errorMessage"));//Sprawdza obecnosc komunikatu bledu
    }

    // Test:Sprawdza poprawne utworzenie zadania z prawidlowymi danymi
    @Test
    void postTask_Success() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Valid Title")//Poprawne dane
                        .param("category", "Valid Category")
                        .param("dueDate", "2024-12-01")
                        .param("completed", "false"))
                .andExpect(status().is3xxRedirection()) //Oczekiwane przekierowanie po sukcesie
                .andExpect(redirectedUrl("/tasks"));// Sprawdza, czy przekierowanie jest poprawne
    }
}
