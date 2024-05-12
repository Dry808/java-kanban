package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.adapter.DurationTypeAdapter;
import http.adapter.LocalDateTimeTypeAdapter;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import models.Epic;
import models.Status;
import models.SubTask;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskServerSubTasksTest {
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
    void getSubTaskTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/0");

        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);
        SubTask subTask= new SubTask("Задача 1", "Описание", Status.IN_PROGRESS, epic);

        manager.addSubTask(subTask);
        String thisSubTask = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();


        assertEquals(200,statusCode);
        assertEquals(thisSubTask, response.body());
    }

    @Test
    void getSubTaskTest_404() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/2");

        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);
        SubTask subTask= new SubTask("Задача 1", "Описание", Status.IN_PROGRESS, epic);

        manager.addSubTask(subTask);


        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();

        assertEquals(404,statusCode);
    }

    @Test
    void getSubTasksTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks");

        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);
        SubTask subTask= new SubTask("подЗадача 1", "Описание", Status.IN_PROGRESS, epic);
        SubTask subTask2= new SubTask("подЗадача 2", "Описание2", Status.IN_PROGRESS, epic);

        manager.addSubTask(subTask);
        manager.addSubTask(subTask2);
        String thisSubTask = gson.toJson(subTask);
        String thisSubTask2 = gson.toJson(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseBody = response.body();

        assertTrue(responseBody.contains(thisSubTask));
        assertTrue(responseBody.contains(thisSubTask2));
        assertEquals(200, statusCode);
    }

    @Test
    void createSubTaskTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks");
        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);
        SubTask subTask= new SubTask("подЗадача 1", "Описание", Status.IN_PROGRESS, epic);
        String thisSubTask = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(thisSubTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        int size = manager.getSubTasks().size();

        assertEquals(201,statusCode);
        assertEquals(1, size);
    }

    @Test
    void updateSubTask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/0");
        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);
        SubTask subTask= new SubTask("подЗадача 1", "Описание", Status.IN_PROGRESS, epic);
        manager.addSubTask(subTask);
        SubTask subTask2= new SubTask("подЗадача 1", "Новое Описание", Status.IN_PROGRESS, epic);
        String thisSubTask = gson.toJson(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(thisSubTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        int size = manager.getSubTasks().size();

        assertEquals(201, statusCode);
        assertEquals(1,size);
        assertEquals(manager.getSubTask(0).getDescription(), subTask2.getDescription());
    }

    @Test
    void createSubTaskTest_406() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks");
        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);
        SubTask subTask= new SubTask("подЗадача 1", "Описание", Status.IN_PROGRESS, epic,
                LocalDateTime.now(),Duration.ofMinutes(30));
        manager.addSubTask(subTask);
        String thisSubTask = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(thisSubTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        int size = manager.getSubTasks().size();

        assertEquals(406, statusCode);
        assertEquals(1,size);
    }

    @Test
    void deleteSubTaskTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/0");
        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);
        SubTask subTask= new SubTask("подЗадача 1", "Описание", Status.IN_PROGRESS, epic);
        manager.addSubTask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        assertEquals(200, statusCode);

        assertEquals(0, manager.getSubTasks().size());
    }
}
