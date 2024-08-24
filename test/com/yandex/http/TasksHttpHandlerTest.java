package com.yandex.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.http.adapter.DurationAdapter;
import com.yandex.http.adapter.LocalDateTimeAdapter;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TasksHttpHandlerTest {
    TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson;
    Task task;

    public TasksHttpHandlerTest() {
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

        task = new Task("task1", "description1", LocalDateTime.of(2023,1,5,
                12,2), Duration.ofMinutes(5));
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void shouldCreateTask() throws IOException, InterruptedException {
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size());
        assertEquals("task1", tasksFromManager.get(0).getName());
    }

    @Test
    public void shouldGetTasks() throws IOException, InterruptedException {
        Task task2 = new Task("task2", "description2", LocalDateTime.of(2024,1,5,
                12,2), Duration.ofMinutes(5));
        taskManager.createTask(task);
        taskManager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        Assertions.assertEquals(2, tasks.length);
    }

    @Test
    public void shouldDeleteTask() throws IOException, InterruptedException {
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ArrayList<Task> tasksList = taskManager.getAllTask();

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(tasksList.isEmpty());
    }
}
