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

public class HttpTaskServerHistory {
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
    void getHistoryTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/history");
        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);
        SubTask subTask= new SubTask("подЗадача 1", "Описание", Status.IN_PROGRESS, epic);
        SubTask subTask2= new SubTask("подЗадача 1", "Новое Описание", Status.IN_PROGRESS, epic);

        manager.addEpic(epic);
        manager.addSubTask(subTask);
        manager.addSubTask(subTask2);

        manager.getEpic(0);
        manager.getSubTask(1);
        manager.getSubTask(2);
        String thisSubTask2 = gson.toJson(subTask2);
        String thisSubTask = gson.toJson(subTask);
        String thisEpic = gson.toJson(subTask2);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseBody = response.body();

        assertEquals(200,statusCode);
        assertEquals(manager.getHistory().contains(subTask), responseBody.contains(thisSubTask));
        assertEquals(manager.getHistory().contains(subTask2), responseBody.contains(thisSubTask2));
        assertEquals(manager.getHistory().contains(epic), responseBody.contains(thisEpic));

    }
}
