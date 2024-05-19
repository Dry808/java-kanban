package http;

import com.sun.net.httpserver.HttpServer;
import http.handle.*;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;
    protected TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    // главный метод, создаём экземпляр класса и запускаем сервер
    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault("file.csv"));
        httpTaskServer.start();
        httpTaskServer.stop();
    }

    // метод настраивает контексты обработчиков и запускет сервер
    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
            server.createContext("/tasks", new TaskHandler(taskManager));
            server.createContext("/subtasks", new SubTaskHandler(taskManager));
            server.createContext("/epics", new EpicHandler(taskManager));
            server.createContext("/history", new HistoryHandler(taskManager));
            server.createContext("/prioritized", new PrioritizedHandler(taskManager));
            server.start();
            System.out.println("HTTP-cервер запущен на порту " + PORT);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // метод для остановки сервера
    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен");
    }

}
