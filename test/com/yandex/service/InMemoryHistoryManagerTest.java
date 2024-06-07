package com.yandex.service;

import com.yandex.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class InMemoryHistoryManagerTest {
    private final InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    private final InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    @Test
    void saveTaskInHistoryManager() {
        Task task = new Task("задача1", "описание1");
        Task task2 = new Task("задача2", "описание2");
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task2);
        List<Task> tasks = inMemoryHistoryManager.getHistory();

        Assertions.assertEquals(task, tasks.getFirst());
    }

    @Test
    void historySize10Control() {
        Task task = new Task("задача1", "описание1");
        inMemoryTaskManager.createTask(task);
        Task task2 = new Task("задача2", "описание2");
        inMemoryTaskManager.createTask(task2);

        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getTaskById(task.getId());
        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getTaskById(task.getId());
        inMemoryTaskManager.getTaskById(task.getId());
        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getTaskById(task.getId());
        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getTaskById(task.getId());
        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getTaskById(task.getId());

        List<Task> history = inMemoryTaskManager.getHistory();

        Assertions.assertEquals(10, history.size());
    }
}