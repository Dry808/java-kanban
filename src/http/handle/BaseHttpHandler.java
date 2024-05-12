package http.handle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import http.DurationTypeAdapter;
import http.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;


public class BaseHttpHandler {
    protected Gson gson;

    public BaseHttpHandler() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gson = gsonBuilder.create();
    }



    protected void sendHttpCode(HttpExchange h, String text, int httpCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(httpCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    // код 200 - ОК
    protected void sendText(HttpExchange h, String text) throws IOException {
        sendHttpCode(h,text,200);
    }

    // код 201 - ОК(модификация)
    protected void  sendCodeCreated(HttpExchange h) throws IOException {
        h.sendResponseHeaders(201,0);
        h.close();
    }

    // код 404 - NotFound
    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        sendHttpCode(h,text,404);
    }

    // код 406 - Пересечения по времени
    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        sendHttpCode(h,text,406);
    }

    // 500 - ошибка на сервере
    protected void sendServerError(HttpExchange h, String text) throws IOException {
        sendHttpCode(h, text, 500);
    }
}
