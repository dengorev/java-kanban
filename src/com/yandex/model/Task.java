package com.yandex.model;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected TypeTasks typeTasks;

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

    public int getId() {
        return id;
    }

    public Integer getEpicId() {
        return null;
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
                '}';
    }
}
