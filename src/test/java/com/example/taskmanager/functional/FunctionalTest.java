package com.example.taskmanager.functional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FunctionalTest {
    private WebDriver driver;

    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:8080/tasks");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    void createTask() {
        driver.findElement(By.linkText("Add New Task")).click();
        driver.findElement(By.name("title")).sendKeys("Functional Test Task");
        driver.findElement(By.name("category")).sendKeys("Test Category");
        driver.findElement(By.name("dueDate")).sendKeys("2024-01-01");
        driver.findElement(By.name("completed")).click();
        driver.findElement(By.tagName("button")).click();

        WebElement task = driver.findElement(By.xpath("//td[text()='Functional Test Task']"));
        assertEquals("Functional Test Task", task.getText());
    }

    @Test
    void deleteTask() {
        WebElement deleteLink = driver.findElement(By.linkText("Delete"));
        deleteLink.click();

        WebElement body = driver.findElement(By.tagName("body"));
        assertEquals(false, body.getText().contains("Functional Test Task"));
    }

    @Test
    void verifyTaskListDisplaysAllTasks() {
        driver.findElement(By.linkText("Add New Task")).click();
        driver.findElement(By.name("title")).sendKeys("Task 1");
        driver.findElement(By.name("category")).sendKeys("Category 1");
        driver.findElement(By.name("dueDate")).sendKeys("2024-01-01");
        driver.findElement(By.tagName("button")).click();

        driver.findElement(By.linkText("Add New Task")).click();
        driver.findElement(By.name("title")).sendKeys("Task 2");
        driver.findElement(By.name("category")).sendKeys("Category 2");
        driver.findElement(By.name("dueDate")).sendKeys("2024-01-02");
        driver.findElement(By.tagName("button")).click();

        WebElement body = driver.findElement(By.tagName("body"));
        assertEquals(true, body.getText().contains("Task 1"));
        assertEquals(true, body.getText().contains("Task 2"));
    }

    @Test
    void markTaskAsCompleted() {

        driver.findElement(By.linkText("Add New Task")).click();
        driver.findElement(By.name("title")).sendKeys("Mark Completed Test Task");
        driver.findElement(By.name("category")).sendKeys("Test");
        driver.findElement(By.name("dueDate")).sendKeys("2024-01-03");
        driver.findElement(By.tagName("button")).click();

        WebElement taskRow = driver.findElement(By.xpath("//tr[td[text()='Mark Completed Test Task']]"));
        WebElement completedCheckbox = taskRow.findElement(By.xpath(".//input[@type='checkbox']"));
        completedCheckbox.click();
        assertEquals(true, completedCheckbox.isSelected());
    }
    @Test
    void validateTaskDetailsBeforeAdding() {

        driver.findElement(By.linkText("Add New Task")).click();
        driver.findElement(By.name("title")).sendKeys("");
        driver.findElement(By.name("category")).sendKeys("");
        driver.findElement(By.name("dueDate")).sendKeys("");
        driver.findElement(By.tagName("button")).click();

        WebElement errorMessage = driver.findElement(By.className("error-message"));
        assertEquals(true, errorMessage.isDisplayed());
        assertEquals("All fields are required.", errorMessage.getText());
    }


}
