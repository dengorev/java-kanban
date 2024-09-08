package com.yandex.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.typeTasks = TypeTasks.SUBTASK;
    }

    public Subtask(String name, String description, LocalDateTime localDateTime, Duration duration, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.typeTasks = TypeTasks.SUBTASK;
        this.startDateTime = localDateTime;
        this.duration = duration;
        this.status = TaskStatus.NEW;
    }

    public Subtask(int id, String name, String description, TaskStatus taskStatus, TypeTasks typeTasks, int epicId) {
        super(id, name, description, taskStatus, typeTasks);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, TaskStatus taskStatus, TypeTasks typeTasks,
                   LocalDateTime startDateTime, Duration duration, LocalDateTime endTime,int epicId) {
        super(id, name, description, taskStatus, typeTasks, startDateTime, duration, endTime);
        this.epicId = epicId;
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Epic id " + getEpicId() +
                ": Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", typeTasks=" + getTypeTasks() +
                ", startDateTime=" + getStartDateTime() +
                ", duration=" + getDuration() +
                '}';
    }
}
