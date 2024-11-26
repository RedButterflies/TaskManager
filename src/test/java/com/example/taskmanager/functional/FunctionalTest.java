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
class FunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void shouldDisplayTaskListPage() throws Exception {
        // Arrange
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

        // Act and Assert
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(view().name("task-list"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attribute("tasks", hasSize(2)))
                .andExpect(model().attribute("tasks", hasItem(
                        allOf(
                                hasProperty("title", is("Task 1")),
                                hasProperty("category", is("Category 1")),
                                hasProperty("dueDate", is(LocalDate.of(2024, 1, 1))),
                                hasProperty("completed", is(false))
                        )
                )));
    }

    @Test
    void shouldReturnTasksAsJson() throws Exception {
        // Arrange
        Task task = new Task();
        task.setTitle("Task 1");
        task.setCategory("Category 1");
        task.setDueDate(LocalDate.of(2024, 1, 1));
        task.setCompleted(false);
        taskRepository.save(task);

        // Act and Assert
        mockMvc.perform(get("/tasks/api").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Task 1")))
                .andExpect(jsonPath("$[0].category", is("Category 1")))
                .andExpect(jsonPath("$[0].dueDate", is("2024-01-01")))
                .andExpect(jsonPath("$[0].completed", is(false)));
    }

    @Test
    void shouldDisplayNewTaskForm() throws Exception {
        mockMvc.perform(get("/tasks/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("task-form"))
                .andExpect(model().attributeExists("task"));
    }

    @Test
    void shouldCreateNewTask() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "New Task")
                        .param("category", "Category 1")
                        .param("dueDate", "2024-01-01")
                        .param("completed", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        // Verify task was saved in the database
        Task savedTask = taskRepository.findAll().get(0);
        assert savedTask.getTitle().equals("New Task");
        assert savedTask.getCategory().equals("Category 1");
        assert savedTask.getDueDate().equals(LocalDate.of(2024, 1, 1));
        assert !savedTask.isCompleted();
    }

    @Test
    void shouldValidateTaskCreation() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "") // Invalid title
                        .param("category", "Category 1")
                        .param("dueDate", "2024-01-01")
                        .param("completed", "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("error-page"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", containsString("Title is required")));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        // Arrange
        Task task = new Task();
        task.setTitle("Task to Delete");
        task.setCategory("Category 1");
        task.setDueDate(LocalDate.of(2024, 1, 1));
        task.setCompleted(false);
        Task savedTask = taskRepository.save(task);

        // Act and Assert
        mockMvc.perform(get("/tasks/delete/" + savedTask.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        // Verify task was deleted
        assert taskRepository.findById(savedTask.getId()).isEmpty();
    }
}
