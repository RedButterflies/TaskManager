package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;//Sluzy do symulacji żądan HTTP w testach

    @Autowired
    private TaskRepository taskRepository; //Repozytorium do obsługi danych w testach
    @Autowired
    private TaskService taskService;
    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }


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



    // Test:Sprawdza, czy szczegoly zadania są poprawnie wyświetlane na stronie "task-details".
    @Test
    void getTaskDetails_ReturnsTaskDetailsPage() throws Exception {
        //Tworzymy zadanie i zapisujemy je w bazie danych
        Task task = new Task();
        task.setTitle("Sample Task");
        task.setCategory("Work");
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setCompleted(false);
        task = taskRepository.save(task); //Zapisanie zadania w bazie danych

        //Wysylamy żądanie GET, aby pobrać szczegóły zadania na podstawie id
        mockMvc.perform(get("/tasks/" + task.getId()))
                .andExpect(status().isOk()) // Sprawdzamy, czy odpowiedź ma status HTTP 200 (OK)
                .andExpect(view().name("task-details")) // Sprawdzamy, czy widok to "task-details"
                .andExpect(model().attributeExists("task")) // Sprawdzamy, czy model zawiera atrybut "task"
                .andExpect(model().attribute("task", hasProperty("title", is("Sample Task")))); // Sprawdzamy, czy zadanie ma poprawny tytuł
    }

    // Test: Sprawdza, czy zadanie jest poprawnie aktualizowane i czy następuje przekierowanie na strone z lista zadan
    @Test
    void updateTask_UpdatesTaskAndRedirects() throws Exception {
        //Tworzymy i zapisujemy zadanie w bazie danych
        Task existingTask = new Task();
        existingTask.setTitle("Original Title");
        existingTask.setCategory("Original Category");
        existingTask.setDueDate(LocalDate.of(2024, 1, 1));
        existingTask.setCompleted(false);
        existingTask = taskRepository.save(existingTask);

        // Wysyłamy żądanie POST, aby zaktualizować dane zadania
        mockMvc.perform(post("/tasks/update/" + existingTask.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED) //Typ treści dla danych formularza
                        .param("title", "Updated Title")
                        .param("category", "Updated Category")
                        .param("dueDate", "2024-02-01")
                        .param("completed", "true"))
                .andExpect(status().is3xxRedirection()) // Sprawdzamy, czy nastąpiło przekierowanie
                .andExpect(redirectedUrl("/tasks")); // Sprawdzamy, czy przekierowano na "/tasks"

        // Pobieramy zaktualizowane zadanie z bazy danych i sprawdzamy, czy dane zostały zmienione
        Task updatedTask = taskRepository.findById(existingTask.getId()).orElseThrow(); // Pobranie zadania na podstawie id
        assertEquals("Updated Title", updatedTask.getTitle()); // Sprawdzamy, czy tytuł został zaktualizowany
        assertEquals("Updated Category", updatedTask.getCategory()); // Sprawdzamy, czy kategoria została zaktualizowana
        assertEquals(LocalDate.of(2024, 2, 1), updatedTask.getDueDate()); //Sprawdzamy nowa date koncowa
        assertTrue(updatedTask.isCompleted()); //Sprawdzamy, czy zadanie jest ukonczone
    }

    //Test: Sprawdza, czy zadania ukonczone sa poprawnie filtrowane i wyswietlane na liscie.
    @Test
    void filterTasks_ReturnsFilteredTasks() throws Exception {
        //Tworzymy dwa zadania o roznych statusach ukończenia
        Task task1 = new Task();
        task1.setTitle("Completed Task");
        task1.setCategory("Work");
        task1.setDueDate(LocalDate.now());
        task1.setCompleted(true);

        Task task2 = new Task();
        task2.setTitle("Pending Task");
        task2.setCategory("Home");
        task2.setDueDate(LocalDate.now());
        task2.setCompleted(false);

        taskRepository.saveAll(List.of(task1, task2)); //Zapisanie obu zadan w bazie danych

        // Wysylamy żądanie GET, aby filtrowac zadania ukonczone
        mockMvc.perform(get("/tasks/filter?completed=true"))
                .andExpect(status().isOk()) // Sprawdzamy, czy odpowiedz HTTP ma status 200 (OK)
                .andExpect(view().name("task-list")) //Sprawdzamy, czy zwracany widok to "task-list"
                .andExpect(model().attributeExists("tasks")) //Sprawdzamy, czy model zawiera listę "tasks"
                .andExpect(model().attribute("tasks", hasSize(1))) //Sprawdzamy, czy lista zadan ma jeden element
                .andExpect(model().attribute("tasks", hasItem(
                        allOf(
                                hasProperty("title", is("Completed Task")), //Sprawdzamy, czy tytuł zadania to "Completed Task"
                                hasProperty("completed", is(true)) // Sprawdzamy, czy zadanie jest ukonczone
                        )
                )));
    }


}
