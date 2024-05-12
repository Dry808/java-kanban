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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskServerEpicsTest {
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
    void getEpicTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics/0");

        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);

        manager.addEpic(epic);
        String thisEpic = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();


        assertEquals(200,statusCode);
        assertEquals(thisEpic, response.body());
    }

    @Test
    void getEpicTest_404() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics/22");

        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);

        manager.addEpic(epic);


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
        URI uri = URI.create("http://localhost:8080/epics");

        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);
        Epic epic2 = new Epic("Эпик2", "Описание эпика2", Status.IN_PROGRESS);

        manager.addEpic(epic);
        manager.addEpic(epic2);
        String thisEpic = gson.toJson(epic);
        String thisEpic2 = gson.toJson(epic2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseBody = response.body();

        assertTrue(responseBody.contains(thisEpic));
        assertTrue(responseBody.contains(thisEpic2));
        assertEquals(200, statusCode);
    }

    @Test
    void getEpicsSubTasksTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics/0/subtasks");

        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);
        SubTask subTask= new SubTask("подЗадача 1", "Описание", Status.IN_PROGRESS, epic);
        SubTask subTask2= new SubTask("подЗадача 2", "Описание2", Status.IN_PROGRESS, epic);
        manager.addEpic(epic);
        manager.addSubTask(subTask);
        manager.addSubTask(subTask2);
        List<Integer> epicsSbt = manager.getEpic(0).getSubTasksId();
        String thisEpicSub = gson.toJson(epicsSbt);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseBody = response.body();

        assertEquals(responseBody, thisEpicSub);
        assertEquals(200, statusCode);
    }

    @Test
    void getEpicsSubTasksTest_404() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics/223434/subtasks");

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
    void createEpicTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics");
        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);
        String thisEpic = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(thisEpic))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        int size = manager.getEpics().size();

        assertEquals(201,statusCode);
        assertEquals(1, size);
    }

    @Test
    void deleteEpicTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics/0");
        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        assertEquals(200, statusCode);

        assertEquals(0, manager.getEpics().size());
    }


}
