package com.yandex;

import com.yandex.model.Epic;
import com.yandex.model.Subtask;
import com.yandex.model.Task;
import com.yandex.model.TaskStatus;
import com.yandex.service.Managers;
import com.yandex.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("task1", "zadacha1");
        Task task1 = new Task("task2", "zadacha2");

        taskManager.createTask(task);
        taskManager.createTask(task1);

        Epic epic = new Epic("epic1", "epic1");
        Epic epic2 = new Epic("epic2", "epic2");

        taskManager.createEpic(epic);
        taskManager.createEpic(epic2);

        Subtask subtask = new Subtask("subtask1", "subtask1", epic.getId());
        Subtask subtask2 = new Subtask("subtask2", "subtask2", epic.getId());
        Subtask subtask3 = new Subtask("subtask3", "subtask3", epic2.getId());
        Subtask subtask4 = new Subtask("subtask4", "subtask4", epic2.getId());
        Subtask subtask5 = new Subtask("subtask5", "subtask5", epic2.getId());

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
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());

        System.out.println();

        System.out.println(taskManager.getHistory());
    }
}

