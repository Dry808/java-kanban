package http;

import com.sun.net.httpserver.HttpServer;
import http.handle.TaskHandler;
import manager.Managers;
import manager.TaskManager;
import models.Status;
import models.Task;

import javax.imageio.IIOException;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private HttpServer server;
    protected TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static void main(String[] args) {

        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault("file.csv"));
        httpTaskServer.start();
        //Task taskss = new Task("Задача 1", "task 1", Status.IN_PROGRESS);
        Task taskss = new Task("Задача 1", "task 1", Status.IN_PROGRESS, "21.02.2024 21:33", 70);
        httpTaskServer.taskManager.addTask(taskss);
    }

    public void start()  {
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
            server.createContext("/tasks", new TaskHandler(taskManager));
//            server.createContext("/subtask", new SubTaskHanlder(taskManager));
//            server.createContext("/epics", new EpicHandler(taskManager));
//            server.createContext("/history", new HistoryHandler(taskManager));
//            server.createContext("/prioritized", new PrioritizedHandler(taskManager));
            server.start();
            System.out.println("HTTP-cервер запущен на порту " + PORT);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен");
    }

}
