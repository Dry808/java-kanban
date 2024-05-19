package http.handle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import manager.TaskManager;
import models.SubTask;
import models.Type;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;
    private static final String PATH_SUBTASKS_ID_PATTERN = "/subtasks/\\d+$";

    public SubTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (Pattern.matches(PATH_SUBTASKS_ID_PATTERN, path)) {
                        getSubTaskById(httpExchange);
                    } else {
                        getSubTasks(httpExchange);
                    }
                    break;
                case "POST":
                    postSubTask(httpExchange, path);
                    break;
                case "DELETE":
                    deleteSubTask(httpExchange);
                    break;
                default:
                    throw new NotFoundException("Такого ресурса не существует");
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendHasInteractions(httpExchange, e.getMessage()); // если задача пересекаются по времени
        } catch (IOException e) {
            sendServerError(httpExchange, e.getMessage());
        }
    }

    // GET (получение подзадачи по ID)
    public void getSubTaskById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String idString = path.substring(path.lastIndexOf("/") + 1);
        int id = Integer.parseInt(idString);
        SubTask tsk = taskManager.getSubTask(id);
        String respo = gson.toJson(tsk);
        sendText(exchange,respo);
    }

    // GET (получение всех подзадач)
    public void getSubTasks(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getSubTasks()));
    }

    // POST (создание новой подзадачи или обновление существующей)
    public void postSubTask(HttpExchange exchange, String path) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            SubTask subTask = gson.fromJson(body, SubTask.class);
            subTask.setType(Type.SUBTASK);
            if (Pattern.matches(PATH_SUBTASKS_ID_PATTERN, path)) {
                taskManager.updateSubTask(subTask);
            } else {
                taskManager.addSubTask(subTask);
            }
            sendCodeCreated(exchange);
        } catch (NullPointerException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    // DELETE (удаление подзадачи по ID)
    public void deleteSubTask(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String idString = path.substring(path.lastIndexOf("/") + 1);
        int id = Integer.parseInt(idString);

        taskManager.deleteSubTask(id);
        sendText(exchange, "Подзадача с id-" + id + " удалена успешно!");
    }
}

