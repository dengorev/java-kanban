package com.yandex.model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        this.typeTasks = TypeTasks.EPIC;
    }

    public Epic(int id, String name, String description, TaskStatus taskStatus, TypeTasks typeTasks) {
        super(id, name, description, taskStatus, typeTasks);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void addSubtaskId(int id) {
        if (this.id != id) {
            this.subtasksId.add(id);
        }
    }

    public void deleteAllSubtaskId() {
        subtasksId.clear();
    }

    public void deleteSubtaskId(Integer id) {
        this.subtasksId.remove(id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", subtaskId=" + getSubtasksId() +
                '}';
    }
}
