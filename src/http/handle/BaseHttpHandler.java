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


    // для отправки ответа 200 с текстом в теле
    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    // для отправки ответа 201(модификация)
    protected void  sendCodeCreated(HttpExchange h) throws IOException {
        h.sendResponseHeaders(201,0);
        h.close();
    }

    protected void sendHttpCode(HttpExchange h, int code) throws IOException {
        h.sendResponseHeaders(code,0);
        h.close();
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(404,resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(406,resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }
}
