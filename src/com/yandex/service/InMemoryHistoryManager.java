package com.yandex.service;

import com.yandex.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> tasks = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory() {
        return tasks;
    }
    @Override
    public void add(Task task) {
        if (tasks.size() < 9) {
            tasks.add(task);
        } else {
            tasks.removeFirst();
            tasks.add(task);
        }
    }
}
