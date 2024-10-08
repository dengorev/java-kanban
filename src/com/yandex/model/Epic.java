package com.yandex.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId;

    public Epic(String name, String description) {
        super(name, description);
        this.typeTasks = TypeTasks.EPIC;
    }

    public Epic(int id, String name, String description, TaskStatus taskStatus, TypeTasks typeTasks,
                LocalDateTime startDataTime, Duration duration, LocalDateTime endDataTime) {
        super(id, name, description, taskStatus, typeTasks, startDataTime, duration, endDataTime);
    }

    public void addDuration(Duration duration) {
        if (this.duration != null) {
            this.duration = this.duration.plus(duration);
        }
    }

    public LocalDateTime getEndDataTime() {
        return startDateTime.plus(duration);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void addSubtaskId(int id) {
        if (subtasksId == null) {
            subtasksId = new ArrayList<>();
        }
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
                ", typeTasks=" + getTypeTasks() +
                ", startDateTime=" + getStartDateTime() +
                ", duration=" + getDuration() +
                '}';
    }
}

