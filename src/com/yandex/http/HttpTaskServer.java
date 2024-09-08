package com.yandex.http;

import com.sun.net.httpserver.HttpServer;
import com.yandex.http.handler.*;
import com.yandex.service.Managers;
import com.yandex.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public static final int PORT = 8080;
    public static final String HOST = "localhost";
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(HOST, PORT), 0);
        this.httpServer.createContext("/tasks", new TasksHttpHandler(taskManager));
        this.httpServer.createContext("/subtasks", new SubtasksHttpHandler(taskManager));
        this.httpServer.createContext("/epics", new EpicsHttpHandler(taskManager));
        this.httpServer.createContext("/history", new HistoryHttpHandler(taskManager));
        this.httpServer.createContext("/prioritized", new PrioritizedHttpHandler(taskManager));
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    public void start() {
        httpServer.start();
        System.out.println("Сервер на порте " + PORT + " запущен.");
    }

    public void stop() {
        httpServer.stop(0);
    }
}
