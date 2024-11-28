# **TaskManager**

TaskManager is a simple Spring Boot application designed for managing tasks. It allows users to create, view, edit, and delete tasks with built-in form validation and error handling. The application also provides API endpoints for integrating with external systems.

---

## **Features**

- Add new tasks using a user-friendly form.
- View all tasks in a list with detailed information (title, category, due date, completion status).
- Delete tasks directly from the list.
- Built-in form validation for required fields (e.g., title, category, and due date).
- REST API to fetch all tasks in JSON format.
- Integrated error handling with user feedback on validation errors.

---

## **Technologies Used**

### **Backend**
- Spring Boot (3.4.0)
- Spring Data JPA (Hibernate)
- H2 Database (in-memory database for testing and development)

### **Frontend**
- Thymeleaf (HTML rendering)

### **Testing**
- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc

### **Build Tool**
- Maven

---

## **Requirements**

- Java 17 or later
- Maven 3.6 or later

---

## **How to Run the Application**

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd taskmanager
   ```
2. **Build the Application**
    ```bash
    mvn clean install
     ```
3. **Run the Application**
    ```bash
    mvn spring-boot:run
    ```
4. **Access the Application**
   - Open a browser and navigate to http://localhost:8080/tasks.
## **Available Endpoints**

### **Web Interface**
- `/tasks` - View the task list.
- `/tasks/new` - Add a new task.
- `/tasks/delete/{id}` - Delete a task.

### **API Endpoints**
- `/tasks/api` - Get all tasks in JSON format.

---

## **Testing the Application**

1. **Run All Tests**
   ```bash
   mvn test
### **Test Details**
- **Unit Tests:** Located in `UnitTests.java` for testing `TaskService`.
- **Integration Tests:** Located in `IntegrationTests.java` for testing `TaskController`.
- **Functional Tests:** Located in `FunctionalTests.java` for end-to-end testing of the application.

---

