package com.yandex.service;

import com.yandex.model.Epic;
import com.yandex.model.Subtask;
import com.yandex.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    ArrayList<Task> getAllTask();

    ArrayList<Epic> getAllEpic();

    ArrayList<Subtask> getAllSubtask();

    void deleteAllTask();

    void deleteAllEpic();

    void deleteAllSubtask();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    ArrayList<Subtask> getSubtaskByEpicId(int epicId);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    List<Task> getHistory();
}

