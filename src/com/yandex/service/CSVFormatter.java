package com.yandex.service;

import com.yandex.model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class CSVFormatter {

    private CSVFormatter() {

    }

    public static String toString(Task task) {
        StringBuilder result = new StringBuilder()
                .append(task.getId()).append(",")
                .append(task.getName()).append(",")
                .append(task.getTypeTasks()).append(",")
                .append(task.getDescription()).append(",")
                .append(task.getStatus()).append(",")
                .append(task.getStartDateTime()).append(",")
                .append(task.getDuration().toMinutes()).append(",")
                .append(task.getEndDataTime());

        if (task.getTypeTasks().equals(TypeTasks.SUBTASK)) {
            result.append(",").append(task.getEpicId());
        }

        return result.toString();
    }

    public static Task fromString(String value) {
        String[] taskLine = value.split(",");
        int id = Integer.parseInt(taskLine[0]);
        String name = taskLine[1];
        TypeTasks type = TypeTasks.valueOf(taskLine[2]);
        String description = taskLine[3];
        TaskStatus taskStatus = TaskStatus.valueOf(taskLine[4]);
        LocalDateTime startDataTime = LocalDateTime.parse(taskLine[5]);
        Duration duration = Duration.ofSeconds(Long.parseLong(taskLine[6]));
        LocalDateTime endTime = LocalDateTime.parse(taskLine[7]);

        if (type == TypeTasks.TASK) {
            Task task = new Task(id, name, description, taskStatus, type, startDataTime, duration, endTime);
            return task;
        } else if (type == TypeTasks.EPIC) {
            Epic epic = new Epic(id, name, description, taskStatus, type, startDataTime, duration, endTime);
            return epic;
        } else if (type == TypeTasks.SUBTASK) {
            int epicId = Integer.valueOf(taskLine[5]);
            Subtask subtask = new Subtask(id, name, description, taskStatus, type, startDataTime, duration, endTime, epicId);
            return subtask;
        }
        return null;
    }

    public static String getHeader() {
        return "id,name,type,description,status,startDataTime,duration,endDataTime,epic";
    }
}
