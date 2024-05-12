package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import http.adapter.DurationTypeAdapter;
import http.adapter.LocalDateTimeTypeAdapter;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import models.Epic;
import models.Status;
import models.SubTask;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskServerPrioritizedTest {
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
    void getPrioritizedTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Задача 1", "Описание эпика", Status.IN_PROGRESS);

        SubTask subTask = new SubTask("Name", "Descr", Status.NEW, epic,
                "05.05.2024 11:00", 20);
        Task task = new Task("Задача 1", "task 1", Status.IN_PROGRESS, "21.02.2024 21:33",
                70);

        Task task2 = new Task("Задача 2", "task de 2", Status.IN_PROGRESS, "21.03.2024 12:33", 10);
        manager.addTask(task);
        manager.addTask(task2);
        manager.addEpic(epic);
        manager.addSubTask(subTask);

        URI uri = URI.create("http://localhost:8080/prioritized");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseBody = response.body();

        List<Task> usersList = gson.fromJson(responseBody, new UserListTypeToken().getType());
        List<Task> prior = manager.getPrioritizedTasks();

        assertEquals(200,statusCode);
        assertEquals(usersList.get(0).getId(), prior.get(0).getId());
        assertEquals(usersList.get(1).getId(), prior.get(1).getId());
        assertEquals(usersList.get(2).getId(), prior.get(2).getId());

    }
}

class UserListTypeToken extends TypeToken<List<Task>> {
    // здесь ничего не нужно реализовывать
}
