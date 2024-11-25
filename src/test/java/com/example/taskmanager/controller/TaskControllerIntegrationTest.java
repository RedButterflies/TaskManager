package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void getTasks_ReturnsTasks() throws Exception {
        Task task = new Task();
        task.setTitle("Sample Task");
        taskRepository.save(task);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Sample Task")));
    }

    @Test
    void postTask_CreatesTask() throws Exception {
        String taskJson = """
            {
                "title": "New Task",
                "category": "Personal",
                "dueDate": "2024-01-01",
                "completed": false
            }
            """;

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void deleteTask_RemovesTask() throws Exception {
        Task task = new Task();
        task.setTitle("Task to Delete");
        task = taskRepository.save(task);

        mockMvc.perform(get("/tasks/delete/" + task.getId()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void getTasks_ReturnsEmptyWhenNoTasks() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("<tr>"))));
    }


    @Test
    void postTask_ValidationFailure() throws Exception {
        String invalidTaskJson = """
            {
                "category": "Invalid Task"
            }
            """;

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTaskJson))
                .andExpect(status().isBadRequest());
    }
}
