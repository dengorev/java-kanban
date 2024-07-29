package com.yandex.model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.typeTasks = TypeTasks.SUBTASK;
    }

    public Subtask(int id, String name, String description, TaskStatus taskStatus, TypeTasks typeTasks, int epicId) {
        super(id, name, description, taskStatus, typeTasks);
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
                '}';
    }
}
