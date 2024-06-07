package com.yandex.service;

import com.yandex.model.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void add(Task task);
}
