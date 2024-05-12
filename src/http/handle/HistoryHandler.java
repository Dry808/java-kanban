package http.handle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            if (method.equals("GET")) {
                sendText(httpExchange, gson.toJson(taskManager.getHistory()));
            } else {
                sendHttpCode(httpExchange,"Method Not Allowed", 405);
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        } catch (IOException e) {
            sendServerError(httpExchange, e.getMessage());
        }
    }
}
