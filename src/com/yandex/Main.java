package com.yandex;

import com.yandex.model.Epic;
import com.yandex.model.Subtask;
import com.yandex.model.Task;
import com.yandex.model.TaskStatus;
import com.yandex.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();
        Task task = new Task("task1", "zadacha1");
        Task task1 = new Task("task2", "zadacha2");
        Epic epic = new Epic("epic1", "epic1");
        Epic epic2 = new Epic("epic2", "epic2");
        Subtask subtask = new Subtask("subtask1", "subtask1", 3);
        Subtask subtask2 = new Subtask("subtask2", "subtask2", 3);
        taskManager.createTask(task);
        taskManager.createTask(task1);
        taskManager.createEpic(epic);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask2);

        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        taskManager.updateTask(task);
        taskManager.updateEpic(epic);
        taskManager.updateSubtask(subtask);

        System.out.println();

        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        subtask.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask);

        System.out.println();

        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());

        taskManager.removeTaskById(1);
        taskManager.removeEpicById(3);

        System.out.println();

        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubtask());


    }
}

