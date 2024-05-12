package http.handle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import manager.TaskManager;
import models.Epic;
import models.Type;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (Pattern.matches("/epics/\\d+$", path)) {
                        getEpicById(httpExchange);
                    } else {
                        getEpics(httpExchange,path);
                    }
                    break;
                case "POST":
                    postEpic(httpExchange, path);
                    break;
                case "DELETE":
                    deleteEpic(httpExchange);
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


    // GET (получение эпика по ID)
    public void getEpicById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String idString = path.substring(path.lastIndexOf("/") + 1);
        int id = Integer.parseInt(idString);
        Epic tsk = taskManager.getEpic(id);
        String respo = gson.toJson(tsk);
        sendText(exchange,respo);
    }




    // GET (получение всех эпиков)
    public void getEpics(HttpExchange exchange, String path) throws IOException {
        if (Pattern.matches("/epics/\\d+/subtasks", path)) {
            String idString = path.replaceAll("[^\\d]", "");
            int id = Integer.parseInt(idString);
            sendText(exchange, gson.toJson(taskManager.getEpic(id).getSubTasksId()));
        }
        sendText(exchange, gson.toJson(taskManager.getEpics()));
    }

    // POST (создание нового эпика или обновление существующего)
    public void postEpic(HttpExchange exchange, String path) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            Epic epic = gson.fromJson(body, Epic.class);
            epic.setType(Type.EPIC);
            if (Pattern.matches("/epics/\\d+$", path)) {
                taskManager.updateEpic(epic);
            } else {
                taskManager.addEpic(epic);
            }
            sendCodeCreated(exchange);
        } catch (NullPointerException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    // DELETE (удаление эпика по ID)
    public void deleteEpic(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String idString = path.substring(path.lastIndexOf("/") + 1);
        int id = Integer.parseInt(idString);

        taskManager.deleteEpic(id);
        sendText(exchange, "Эпик с id-" + id + " удален успешно!");
    }
}

