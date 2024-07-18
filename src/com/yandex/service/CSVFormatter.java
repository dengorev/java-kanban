package com.yandex.service;

import com.yandex.model.*;

public class CSVFormatter {

    private CSVFormatter() {

    }
    public static String toString(Task task) {
        String result = new StringBuilder()
                .append(task.getId()).append(",")
                .append(task.getName()).append(",")
                .append(task.getTypeTasks()).append(",")
                .append(task.getDescription()).append(",")
                .append(task.getStatus()).append(",")
                .toString();

        if (task.getTypeTasks().equals(TypeTasks.SUBTASK)) {
            result = result +  task.getEpicId();
        }

        return result;
    }

    public static Task fromString(String value) {
        String[] taskLine = value.split(",");
        int id = Integer.valueOf(taskLine[0]);
        String name = taskLine[1];
        TypeTasks type = TypeTasks.valueOf(taskLine[2]);
        String description = taskLine[3];
        TaskStatus taskStatus = TaskStatus.valueOf(taskLine[4]);

        if(type == TypeTasks.TASK) {
            Task task = new Task(id, name, description, taskStatus, type);
            return task;
        } else if(type == TypeTasks.EPIC) {
            Epic epic = new Epic(id, name, description, taskStatus, type);
            return epic;
        } else if(type == TypeTasks.SUBTASK) {
            int epicId = Integer.valueOf(taskLine[5]);
            Subtask subtask = new Subtask(id, name, description, taskStatus, type, epicId);
            return subtask;
        }
        return null;
    }

    public static String getHeader() {
        return "id,name,type,description,status,epic";
    }
}
