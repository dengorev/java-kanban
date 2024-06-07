package com.yandex.service;

import com.yandex.model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_SIZE = 9;
    private final List<Task> tasks = new LinkedList<>();

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(tasks);
    }

    @Override
    public void add(Task task) {
        if (tasks.size() <= MAX_SIZE) {
            tasks.add(task);
        } else {
            tasks.removeFirst();
            tasks.add(task);
        }
    }
}
