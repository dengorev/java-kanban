package com.yandex.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.http.adapter.DurationAdapter;
import com.yandex.http.adapter.LocalDateTimeAdapter;
import com.yandex.model.Epic;
import com.yandex.model.Subtask;
import com.yandex.model.Task;
import com.yandex.service.Managers;
import com.yandex.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public class PrioritizedHttpHandlerTest {
    TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson;

    public PrioritizedHttpHandlerTest() {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(taskManager);
        taskManager.deleteAllTask();
        taskManager.deleteAllSubtask();
        taskManager.deleteAllEpic();
        taskServer.start();

        LocalDateTimeAdapter localTimeTypeAdapter = new LocalDateTimeAdapter();
        DurationAdapter durationAdapter = new DurationAdapter();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, localTimeTypeAdapter);
        gsonBuilder.registerTypeAdapter(Duration.class, durationAdapter);
        gson = gsonBuilder.create();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void shouldGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task("task", "description1", LocalDateTime.of(2024,1,5,
                5,2), Duration.ofMinutes(5));
        Epic epic = new Epic("epic", "description2");
        Subtask subtask = new Subtask("subtask", "description3", LocalDateTime.of(2024,1,5,
                9,2), Duration.ofMinutes(5), 2);
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        Assertions.assertEquals(2, tasks.length);
    }
}