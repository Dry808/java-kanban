package http.handle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (Pattern.matches("/tasks/\\d+$", path)) {
                        getTaskById(httpExchange);
                    } else {
                        getTasks(httpExchange);
                    }
                    break;
                case "POST":
                    postTask(httpExchange,path);
                    break;
                case "DELETE":
                    deleteTask(httpExchange);
                    break;
                default:
                    sendHttpCode(httpExchange,"Method Not Allowed", 405);
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendHasInteractions(httpExchange, e.getMessage()); // если задача пересекаются по времени
        } catch (IOException e) {
            sendServerError(httpExchange, e.getMessage());
        }
    }

    // GET (получение задачи по ID)
    public void getTaskById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String idString = path.substring(path.lastIndexOf("/") + 1);
        int id = Integer.parseInt(idString);
        Task tsk = taskManager.getTask(id);
        String respo = gson.toJson(tsk);
        sendText(exchange,respo);
    }

    // GET (получение всех задач)
    public void getTasks(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getTasks()));
    }

    // POST (создание новой задачи или обновление существующей)
    public void postTask(HttpExchange exchange, String path) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            try {
                Task task = gson.fromJson(body, Task.class);
                task.setType(Type.TASK);
                if (Pattern.matches("/tasks/\\d+$", path)) {
                    taskManager.updateTask(task);
                } else {
                    taskManager.addTask(task);
                }
                sendCodeCreated(exchange);
            } catch (NullPointerException e) {
                throw new NotFoundException(e.getMessage());
            }
        }

    // DELETE (удаление задачи по ID)
    public void deleteTask(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String idString = path.substring(path.lastIndexOf("/") + 1);
        int id = Integer.parseInt(idString);

        taskManager.deleteTask(id);
        sendText(exchange, "Задача с id-" + id + " удалена успешно!");
    }
}

