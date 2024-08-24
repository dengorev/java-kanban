package com.yandex.http.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.exception.TaskValidateException;
import com.yandex.http.adapter.DurationAdapter;
import com.yandex.http.adapter.LocalDateTimeAdapter;
import com.yandex.model.Subtask;
import com.yandex.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

public class SubtasksHttpHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public SubtasksHttpHandler(TaskManager taskManager) {
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
                sendError500(exchange, "Недопустимый метод");
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            int id = Integer.parseInt(pathParts[2]);
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask != null) {
                taskManager.removeSubtaskById(id);
                String response = "Подзадача удалена";
                sendText(exchange, response, 200);
            } else {
                sendError404(exchange, "Не найдена подзадача");
            }
        } catch (Exception e) {
            sendError500(exchange, e.getMessage());
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            int pathLength = pathParts.length;

            InputStream requestBody = exchange.getRequestBody();
            String requestBodyStr = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
            if (requestBodyStr.isEmpty()) {
                return;
            }
            Gson gson = getGson();
            Subtask subtask = gson.fromJson(requestBodyStr, Subtask.class);

            if (pathLength == 3) {
                if (subtask != null) {
                    taskManager.updateSubtask(subtask);
                    String response = gson.toJson(subtask);
                    sendText(exchange, response, 201);
                } else {
                    sendError404(exchange, "Несуществующая подзадача");
                }
            } else if (pathLength == 2) {
                taskManager.createSubtask(subtask);
                String response = gson.toJson(subtask);
                sendText(exchange, response, 201);
            }
        } catch (TaskValidateException e) {
            sendError406(exchange, e.getMessage());
        } catch (NoSuchElementException e) {
            sendError404(exchange, e.getMessage());
        } catch (Exception e) {
            sendError500(exchange, e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            int pathLength = pathParts.length;

            if (pathLength == 2) {
                List<Subtask> subtasks = taskManager.getAllSubtask();
                String response = getGson().toJson(subtasks);
                sendText(exchange, response, 200);
            } else {
                int id = Integer.parseInt(pathParts[2]);
                Subtask subtask = taskManager.getSubtaskById(id);
                String response = getGson().toJson(subtask);
                if (subtask != null) {
                    sendText(exchange, response, 200);
                } else {
                    sendError404(exchange, "Несуществующая подзадача");
                }
            }
        } catch (Exception e) {
            sendError500(exchange, e.getMessage());
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
