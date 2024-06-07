package com.yandex.model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
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
