package com.example.taskmanager.functional;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FunctionalTests {

    @Autowired
    private MockMvc mockMvc;//Sluzy do symulacji żądań HTTP w testach

    @Autowired
    private TaskRepository taskRepository;//Repozytorium do obslugi danych w testach

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();//Usuwa wszystkie dane z repozytorium przed kazdym testem
    }

    // Test:Wyswietla strone z lista zadan i sprawdza zawartosc listy
    @Test
    void shouldDisplayTaskListPage() throws Exception {

        //Tworzy i zapisuje dwa zadania testowe w repozytorium
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setCategory("Category 1");
        task1.setDueDate(LocalDate.of(2024, 1, 1));
        task1.setCompleted(false);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setCategory("Category 2");
        task2.setDueDate(LocalDate.of(2024, 1, 2));
        task2.setCompleted(true);
        taskRepository.save(task2);

        //Symuluje żądanie GET i weryfikuje odpowiedz
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk()) //Oczekiwany status: 200 (OK)
                .andExpect(view().name("task-list"))// Sprawdza nazwe widoku
                .andExpect(model().attributeExists("tasks"))//Sprawdza, czy model zawiera liste zadan
                .andExpect(model().attribute("tasks", hasSize(2)))//Sprawdza liczbe elementoe na liscie
                .andExpect(model().attribute("tasks", hasItem( //Weryfikuje wlaściwości jednego z elementów
                        allOf(
                                hasProperty("title", is("Task 1")),
                                hasProperty("category", is("Category 1")),
                                hasProperty("dueDate", is(LocalDate.of(2024, 1, 1))),
                                hasProperty("completed", is(false))
                        )
                )));
    }

    //Test: Zwraca liste zadan w formacie JSON
    @Test
    void shouldReturnTasksAsJson() throws Exception {

        //Tworzy i zapisuje zadanie testowe w repozytorium
        Task task = new Task();
        task.setTitle("Task 1");
        task.setCategory("Category 1");
        task.setDueDate(LocalDate.of(2024, 1, 1));
        task.setCompleted(false);
        taskRepository.save(task);

        //Symuluje zadanie GET do endpointu API i weryfikuje odpowiedz JSON
        mockMvc.perform(get("/tasks/api").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) //Oczekiwany status: 200 (OK)
                .andExpect(jsonPath("$", hasSize(1)))//Sprawdza, czy lista ma jeden element
                .andExpect(jsonPath("$[0].title", is("Task 1")))//Sprawdza wlasciwosci obiektu JSON
                .andExpect(jsonPath("$[0].category", is("Category 1")))
                .andExpect(jsonPath("$[0].dueDate", is("2024-01-01")))
                .andExpect(jsonPath("$[0].completed", is(false)));
    }

    //Test:Wyświetla formularz do dodania nowego zadania
    @Test
    void shouldDisplayNewTaskForm() throws Exception {
        mockMvc.perform(get("/tasks/new"))
                .andExpect(status().isOk())//Oczekiwany status: 200 (OK)
                .andExpect(view().name("task-form"))//Sprawdza nazwe widoku
                .andExpect(model().attributeExists("task"));//Sprawdza, czy model zawiera obiekt "task"
    }

    //Test: Tworzy nowe zadanie
    @Test
    void shouldCreateNewTask() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)//Dane formularza
                        .param("title", "New Task")
                        .param("category", "Category 1")
                        .param("dueDate", "2024-01-01")
                        .param("completed", "false"))
                .andExpect(status().is3xxRedirection()) //Oczekiwane przekierowanie po sukcesie
                .andExpect(redirectedUrl("/tasks"));//Sprawdza URL przekierowania

        //Weryfikuje, czy zadanie zostalo zapisane poprawnie
        Task savedTask = taskRepository.findAll().get(0);
        assert savedTask.getTitle().equals("New Task");
        assert savedTask.getCategory().equals("Category 1");
        assert savedTask.getDueDate().equals(LocalDate.of(2024, 1, 1));
        assert !savedTask.isCompleted();
    }

    //Test:Weryfikuje walidacje przy próbie utworzenia zadania
    @Test
    void shouldValidateTaskCreation() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "") //Puste pole "title"
                        .param("category", "Category 1")
                        .param("dueDate", "2024-01-01")
                        .param("completed", "false"))
                .andExpect(status().isOk())//Oczekiwany status: 200 (OK)
                .andExpect(view().name("error-page"))//Sprawdza nazwe widoku bledu
                .andExpect(model().attributeExists("errorMessage")) //Sprawdza obecność komunikatu bledu
                .andExpect(model().attribute("errorMessage", containsString("Title is required")));//Sprawdza tresc bledu
    }

    //Test: Usuwa zadanie
    @Test
    void shouldDeleteTask() throws Exception {

        //Tworzy i zapisuje zadanie testowe w repozytorium
        Task task = new Task();
        task.setTitle("Task to Delete");
        task.setCategory("Category 1");
        task.setDueDate(LocalDate.of(2024, 1, 1));
        task.setCompleted(false);
        Task savedTask = taskRepository.save(task);

        //Symuluje żądanie GET do usuniecia zadania
        mockMvc.perform(get("/tasks/delete/" + savedTask.getId()))
                .andExpect(status().is3xxRedirection())//Oczekiwane przekierowanie po usunięciu
                .andExpect(redirectedUrl("/tasks"));//Sprawdza URL przekierowania

        //Weryfikuje, czy zadanie zostało usuniete z repozytorium
        assert taskRepository.findById(savedTask.getId()).isEmpty();
    }
}
