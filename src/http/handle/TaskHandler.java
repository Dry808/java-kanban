package http.handle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotAcceptableException;
import exceptions.NotFoundException;
import manager.TaskManager;
import models.Task;
import models.Type;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();

        switch (method) {
            case "GET":
                if (Pattern.matches("/tasks/\\d", path)) {
                    getTaskById(httpExchange);
                } else {
                    getTasks(httpExchange);
                }
                break;
            case "POST":
                postTask(httpExchange);
                break;
            case "DELETE":
                deleteTask(httpExchange);
                break;
        }
    }

    public void getTaskById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String idString = path.substring(path.lastIndexOf("/")+ 1);
        int id = Integer.parseInt(idString);
        Task tsk = taskManager.getTask(id);

        try{
             String respo = gson.toJson(tsk);
            sendText(exchange,respo);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    // GET (получение всех задач)
    public void getTasks(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getTasks()));
    }

    // POST (создание новой задачи или обновление существующей)
    public void postTask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            try {
                Task task = gson.fromJson(body, Task.class);
                task.setType(Type.TASK);
                if (task.getId() > 0) {
                    taskManager.updateTask(task);
                } else {
                    taskManager.addTask(task);
                }
                sendCodeCreated(exchange);
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            } catch (IllegalArgumentException e) {
                sendHttpCode(exchange, 406); // если задача пересекаются по времени
            }
        }

    // DELETE (удаление задачи по ID)
    public void deleteTask(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String idString = path.substring(path.lastIndexOf("/")+ 1);
        int id = Integer.parseInt(idString);

        taskManager.deleteTask(id);
        sendText(exchange, "Задача с id-" + id + " удалена успешно!");
    }
}

