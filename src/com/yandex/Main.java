package com.yandex;

import com.google.gson.Gson;
import com.yandex.model.Epic;
import com.yandex.model.Subtask;
import com.yandex.model.Task;
import com.yandex.model.TaskStatus;
import com.yandex.service.Managers;
import com.yandex.service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("task1", "zadacha1", LocalDateTime.of(2020,1,23,12,24), Duration.ofMinutes(60));
        Task task2 = new Task("task2", "zadacha2", LocalDateTime.of(2020,2,23,16,24), Duration.ofMinutes(60));

        taskManager.createTask(task);
        taskManager.createTask(task2);

        Epic epic = new Epic("epic1", "epic1");
        Epic epic2 = new Epic("epic2", "epic2");

        taskManager.createEpic(epic);
        taskManager.createEpic(epic2);

        Subtask subtask = new Subtask("subtask1", "subtask1", LocalDateTime.of(2020,3,23,12,24), Duration.ofMinutes(60), epic.getId());
        Subtask subtask2 = new Subtask("subtask2", "subtask2", LocalDateTime.of(2020,4,23,12,24), Duration.ofMinutes(60), epic.getId());
        Subtask subtask3 = new Subtask("subtask3", "subtask3", LocalDateTime.of(2020,5,23,12,24), Duration.ofMinutes(60), epic2.getId());
        Subtask subtask4 = new Subtask("subtask4", "subtask4", LocalDateTime.of(2020,6,23,12,24), Duration.ofMinutes(60), epic2.getId());
        Subtask subtask5 = new Subtask("subtask5", "subtask5", LocalDateTime.of(2020,7,23,12,24), Duration.ofMinutes(60), epic2.getId());

        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);

        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        task.setName("updateName");
        taskManager.updateTask(task);
        taskManager.updateEpic(epic);

        System.out.println();

        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        subtask.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask);

        System.out.println();

        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        System.out.println();

        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getTaskById(task2.getId());

        System.out.println();

        System.out.println(taskManager.getHistory());

        System.out.println(taskManager.getPrioritizedTasks());
    }
}

