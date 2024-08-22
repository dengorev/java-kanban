package com.yandex.http.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.http.adapter.DurationAdapter;
import com.yandex.http.adapter.LocalDateTimeAdapter;
import com.yandex.model.Task;
import com.yandex.service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HistoryHttpHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public HistoryHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Gson gson = getGson();
        if ("GET".equals(exchange.getRequestMethod())) {
            List<Task> history = taskManager.getHistory();
            String historyJson = gson.toJson(history);
            sendText(exchange, historyJson, 200);
        } else {
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
