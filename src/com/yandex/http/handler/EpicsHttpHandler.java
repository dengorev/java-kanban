package com.yandex.http.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.exception.DurationException;
import com.yandex.http.adapter.DurationAdapter;
import com.yandex.http.adapter.LocalDateTimeAdapter;
import com.yandex.model.Epic;
import com.yandex.model.Subtask;
import com.yandex.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

public class EpicsHttpHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicsHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            default:
                sendError500(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            int id = Integer.parseInt(pathParts[2]);
            Epic epic = taskManager.getEpicById(id);
            if (epic != null) {
                taskManager.removeEpicById(id);
                String response = "Епик удален";
                sendText(exchange, response, 200);
            } else {
                sendError404(exchange);
            }
        } catch (Exception e) {
            sendError500(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            int pathLength = pathParts.length;

            InputStream requestBody = exchange.getRequestBody();
            String requestBodyStr = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
            Gson gson = getGson();
            Epic epic = gson.fromJson(requestBodyStr, Epic.class);

            if (pathLength == 3) {
                try {
                    if (epic != null) {
                        taskManager.updateEpic(epic);
                        String response = gson.toJson(epic);
                        sendText(exchange, response, 201);
                    } else {
                        sendError404(exchange);
                    }
                } catch (DurationException e) {
                    sendError406(exchange);
                } catch (NoSuchElementException e) {
                    sendError404(exchange);
                }
            } else if (pathParts.length == 2) {
                taskManager.createEpic(epic);
                String response = gson.toJson(epic);
                sendText(exchange, response, 201);
            } else {
                sendError406(exchange);
            }
            exchange.getResponseBody().close();
        } catch (Exception e) {
            sendError500(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            Gson gson = getGson();
            int pathLength = pathParts.length;

            if (pathLength == 2) {
                List<Epic> epics = taskManager.getAllEpic();
                String response = gson.toJson(epics);
                sendText(exchange, response, 200);
            } else if (pathLength == 3) {
                int id = Integer.parseInt(pathParts[2]);
                Epic epic = taskManager.getEpicById(id);
                String response = gson.toJson(epic);
                if (epic != null) {
                    sendText(exchange, response, 200);
                } else {
                    sendError404(exchange);
                }
            } else {
                int id = Integer.parseInt(pathParts[2]);
                List<Subtask> subtasks = taskManager.getSubtaskByEpicId(id);
                String response = gson.toJson(subtasks);
                if (subtasks != null) {
                    sendText(exchange, response, 200);
                } else {
                    sendError404(exchange);
                }
            }
        } catch (Exception e) {
            sendError500(exchange);
        }
    }

    private static Gson getGson() {
        LocalDateTimeAdapter localTimeTypeAdapter = new LocalDateTimeAdapter();
        DurationAdapter durationAdapter = new DurationAdapter();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, localTimeTypeAdapter);
        gsonBuilder.registerTypeAdapter(Duration.class, durationAdapter);
        return gsonBuilder.create();
    }
}
