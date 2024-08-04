package com.yandex.service;

import com.yandex.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class InMemoryHistoryManagerTest {
    private final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    private final TaskManager inMemoryTaskManager = Managers.getDefault();
    private Task task;
    private Task task2;

    @BeforeEach
    void init() {
        task = new Task("задача1", "описание1",  LocalDateTime.of(2024,1,5,12,2), Duration.ofMinutes(60));
        task2 = new Task("задача2", "описание2",  LocalDateTime.of(2024,1,15,12,2), Duration.ofMinutes(60));
    }

    @Test
    void shouldAddTasksInHistoryWithoutDuplicates() {
        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.createTask(task2);

        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task2);

        List<Task> tasks = inMemoryHistoryManager.getHistory();

        Assertions.assertEquals(2, tasks.size());

        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task);

        Assertions.assertEquals(2, tasks.size());
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        Task task3 = new Task("задача3", "описание3", LocalDateTime.of(2024,3,5,12,2), Duration.ofMinutes(60));
        Task task4 = new Task("задача4", "описание4", LocalDateTime.of(2024,4,5,12,2), Duration.ofMinutes(60));
        Task task5 = new Task("задача5", "описание5", LocalDateTime.of(2024,5,5,12,2), Duration.ofMinutes(60));

        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.createTask(task2);
        inMemoryTaskManager.createTask(task3);
        inMemoryTaskManager.createTask(task4);
        inMemoryTaskManager.createTask(task5);

        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task2);
        inMemoryHistoryManager.add(task3);
        inMemoryHistoryManager.add(task4);
        inMemoryHistoryManager.add(task5);

        List<Task> tasks = inMemoryHistoryManager.getHistory();

        Assertions.assertEquals(5, tasks.size());
        tasks.removeFirst();
        Assertions.assertEquals(4, tasks.size());
        tasks.remove(task3.getId());
        Assertions.assertEquals(3, tasks.size());
        tasks.removeLast();
        Assertions.assertEquals(2, tasks.size());
    }

    @Test
    void shouldEmptyHistory() {
        List<Task> tasks = inMemoryHistoryManager.getHistory();

        Assertions.assertEquals(0, tasks.size());
    }

}