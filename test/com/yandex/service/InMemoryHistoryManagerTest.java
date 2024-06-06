package com.yandex.service;

import com.yandex.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class InMemoryHistoryManagerTest {
    private final InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    @Test
    void saveTaskInHistoryManager() {
        Task task = new Task("задача1", "описание1");
        Task task2 = new Task("задача2", "описание2");
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task2);
        ArrayList<Task> tasks = inMemoryHistoryManager.getHistory();

        Assertions.assertEquals(task, tasks.getFirst());
    }
}