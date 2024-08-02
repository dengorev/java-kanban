package com.yandex.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected TypeTasks typeTasks;
    protected LocalDateTime startDateTime;
    protected Duration duration;
    protected LocalDateTime endTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.typeTasks = TypeTasks.TASK;
    }

    public Task(int id, String name, String description, TaskStatus taskStatus, TypeTasks typeTasks) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = taskStatus;
        this.typeTasks = typeTasks;
    }

    public Task(int id, String name, String description, TaskStatus taskStatus, TypeTasks typeTasks,
                LocalDateTime startDateTime, Duration duration, LocalDateTime endTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = taskStatus;
        this.typeTasks = typeTasks;
        this.startDateTime = startDateTime;
        this.duration = duration;
        this.endTime = endTime;
    }

    public Task(String name, String description, LocalDateTime startDateTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.startDateTime = startDateTime;
        this.duration = duration;
        this.typeTasks = TypeTasks.TASK;
        this.status = TaskStatus.NEW;
    }

    public LocalDateTime getEndDataTime() {
        return startDateTime.plusMinutes(duration.toMinutes());
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getId() {
        return id;
    }

    public Integer getEpicId() {
        return null;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public TypeTasks getTypeTasks() {
        return typeTasks;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
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
