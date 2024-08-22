package com.yandex.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.http.adapter.DurationAdapter;
import com.yandex.http.adapter.LocalDateTimeAdapter;
import com.yandex.model.Epic;
import com.yandex.model.Subtask;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubtasksHttpHandlerTest {
    TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson;

    public SubtasksHttpHandlerTest() {
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
    public void shouldCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic1", "description1");
        Subtask subtask = new Subtask("subtask1", "description1", LocalDateTime.of(2024,1,5,
                12,2), Duration.ofMinutes(5), 1);
        taskManager.createEpic(epic);
        String epicJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getAllSubtask();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size());
        assertEquals("subtask1", subtasksFromManager.get(0).getName());
    }

    @Test
    public void shouldGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic1", "description1");
        Subtask subtask1 = new Subtask("subtask1", "description1", LocalDateTime.of(2023,1,5,
                10,2), Duration.ofMinutes(5), 1);
        Subtask subtask2 = new Subtask("subtask2", "description1", LocalDateTime.of(2024,1,5,
                12,2), Duration.ofMinutes(5), 1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        Assertions.assertEquals(2, subtasks.length);
    }

    @Test
    public void shouldDeleteSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("epic1", "description1");
        Subtask subtask = new Subtask("epic1", "description1", LocalDateTime.of(2024,1,5,
                12,2), Duration.ofMinutes(5), 1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ArrayList<Subtask> subtasksList = taskManager.getAllSubtask();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(subtasksList.isEmpty());
    }
}
