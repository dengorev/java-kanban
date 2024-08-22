package com.yandex.http.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.exception.DurationException;
import com.yandex.http.adapter.DurationAdapter;
import com.yandex.http.adapter.LocalDateTimeAdapter;
import com.yandex.model.Task;
import com.yandex.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

public class TasksHttpHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public TasksHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
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
            Task task2 = taskManager.getTaskById(id);

            if (task2 != null) {
                taskManager.removeTaskById(id);
                String response = "Задача удалена";
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

            InputStream inputStream = exchange.getRequestBody();
            String taskJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Gson gson = getGson();
            Task task = gson.fromJson(taskJson, Task.class);
            if (pathLength == 3) {
                String idStr = pathParts[2];
                try {
                    int id = Integer.parseInt(idStr);
                    Task task2 = taskManager.getTaskById(id);
                    if (task2 != null) {
                        taskManager.updateTask(task2);
                        String response = gson.toJson(task2);
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
                taskManager.createTask(task);
                String response = gson.toJson(task);
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
            int pathLength = pathParts.length;

            if (pathLength == 2) {
                List<Task> tasks = taskManager.getAllTask();
                Gson gson = getGson();
                String response = gson.toJson(tasks);
                sendText(exchange, response, 200);
            } else {
                int id = Integer.parseInt(pathParts[2]);
                Task task = taskManager.getTaskById(id);
                if (task != null) {
                    Gson gson = getGson();
                    String response = gson.toJson(task);
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
