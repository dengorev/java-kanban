package com.yandex.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected void sendText(HttpExchange h, String text, int status) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(status, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendError500(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 500);
    }

    protected void sendError404(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 404);
    }

    protected void sendError406(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, 406);
    }
}
