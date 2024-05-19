package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.adapter.DurationTypeAdapter;
import http.adapter.LocalDateTimeTypeAdapter;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import models.Status;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTasksTest {
    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson;


    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gson = gsonBuilder.create();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }


    @Test
    void getTaskTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/0");
        Task task = new Task("Задача 1", "Описание", Status.IN_PROGRESS);
        manager.addTask(task);
        String thisTask = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseBody = response.body();

        assertEquals(200, statusCode, "Статус-код ответа не 200");
        assertEquals(responseBody, thisTask);
    }

    @Test
    void getTaskTest_404() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/1");
        Task task = new Task("Задача 1", "Описание", Status.IN_PROGRESS);
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();

        assertEquals(404, statusCode);
    }


    @Test
    void getTasksTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks");
        Task task = new Task("Задача 1", "Описание", Status.IN_PROGRESS);
        Task task2 = new Task("Задача 2", "Описание2", Status.NEW);
        manager.addTask(task);
        manager.addTask(task2);
        String thisTask = gson.toJson(task);
        String thisTask2 = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseBody = response.body();

        assertEquals(200, statusCode, "Статус-код не 200");
        assertTrue(responseBody.contains(thisTask));
        assertTrue(responseBody.contains(thisTask2));
    }

    @Test
    void createTaskTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks");
        Task task = new Task("Задача 1", "Описание", Status.IN_PROGRESS);
        String thisTask = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(thisTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        assertEquals(201,statusCode);

        int size = manager.getTasks().size();
        assertEquals(1, size);
    }


    @Test
    void updateTaskTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/0");
        Task task = new Task("Задача 1", "Описание", Status.IN_PROGRESS);
        manager.addTask(task);
        Task task2 = new Task("Задача 1", "Новое Описание", Status.IN_PROGRESS);
        String thisTask = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(thisTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        assertEquals(201, statusCode);

        int size = manager.getTasks().size();
        assertEquals(1,size);

        assertEquals(manager.getTask(0).getDescription(), task2.getDescription());

    }

    @Test
    void createTaskTest_406() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks");
        Task task = new Task("Задача 1", "Описание", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(40));
        manager.addTask(task);
        String thisTask = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(thisTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        assertEquals(406, statusCode);
    }

    @Test
    void deleteTaskTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/0");
        Task task = new Task("Задача 1", "Описание", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(40));
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        assertEquals(200, statusCode);

        assertEquals(0, manager.getTasks().size());
    }
}

