package com.yandex.service;

import com.yandex.model.Epic;
import com.yandex.model.Subtask;
import com.yandex.model.Task;
import com.yandex.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private final Map<Integer, Task> taskStorage = new HashMap<>();
    private final Map<Integer, Epic> epicStorage = new HashMap<>();
    private final Map<Integer, Subtask> subtaskStorage = new HashMap<>();
    private int idGenerated = 0;

    public Task createTask(Task task) {
        int id = idGenerator();
        task.setId(id);
        taskStorage.put(id, task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        int id = idGenerator();
        epic.setId(id);
        epicStorage.put(id, epic);
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        int id = idGenerator();
        subtask.setId(id);
        subtaskStorage.put(id, subtask);
        Epic epic = epicStorage.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Epic с таким id не существует");
        }
        epic.addSubtaskId(id);
        updateEpic(epic);
        return subtask;
    }

    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(taskStorage.values());
    }

    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epicStorage.values());
    }

    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtaskStorage.values());
    }

    public void deleteAllTask() {
        taskStorage.clear();
        epicStorage.clear();
        subtaskStorage.clear();
    }

    public void deleteAllEpic() {
        epicStorage.clear();
        subtaskStorage.clear();
    }

    public void deleteAllSubtask() {
        subtaskStorage.clear();
        for (Epic epic : epicStorage.values()) {
            epic.deleteAllSubtaskId();
            updateEpic(epic);
        }
    }

    public Task getTaskById(int id) {
        if (taskStorage.containsKey(id)) {
            return taskStorage.get(id);
        }
        return null;
    }

    public Epic getEpicById(int id) {
        if (epicStorage.containsKey(id)) {
            return epicStorage.get(id);
        }
        return null;
    }

    public Subtask getSubtaskById(int id) {
        if (subtaskStorage.containsKey(id)) {
            return subtaskStorage.get(id);
        }
        return null;
    }

    public ArrayList<Subtask> getSubtaskByEpicId(int epicId) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Subtask subtask : subtaskStorage.values()) {
            if (subtask.getEpicId() == epicId) {
                subtasks.add(subtask);
            }
        }
        return subtasks;
    }

    public void removeTaskById(int id) {
        taskStorage.remove(id);
    }

    public void removeEpicById(int id) {
        Epic epic = epicStorage.get(id);
        for (Integer subtaskIds : epic.getSubtasksId()) {
            subtaskStorage.remove(subtaskIds);
            epic.deleteSubtaskId(subtaskIds);
        }
        epicStorage.remove(id);
    }

    public void removeSubtaskById(int id) {
        Epic epic = epicStorage.get(subtaskStorage.get(id).getEpicId());
        epic.deleteSubtaskId(id);
        subtaskStorage.remove(id);
        updateEpic(epic);
    }

    public Task updateTask(Task task) {
        if (taskStorage.get(task.getId()) == null) {
            return null;
        }
        return taskStorage.put(task.getId(), task);
    }

    public Epic updateEpic(Epic epic) {
        if (epicStorage.get(epic.getId()) == null) {
            return null;
        }
        return epicStorage.put(epic.getId(), epic);
    }

    public Subtask updateSubtask(Subtask subtask) {
        Subtask saved = subtaskStorage.get(subtask.getId());
        if (saved == null) {
            return null;
        } else if (saved.getStatus() != TaskStatus.DONE) {
            statusInProgress(saved);
        } else {
            statusToDone(saved);
        }
        return subtaskStorage.put(subtask.getId(), subtask);
    }

    private void statusInProgress(Subtask subtask) {
        Epic epic = epicStorage.get(subtask.getEpicId());
        Subtask saved = subtaskStorage.get(subtask.getId());
        saved.setStatus(TaskStatus.IN_PROGRESS);
        epic.setStatus(TaskStatus.IN_PROGRESS);
        subtaskStorage.put(subtask.getId(), saved);
        epicStorage.put(epic.getId(), epic);
    }

    private void statusToDone(Subtask subtask) {
        Epic epic = epicStorage.get(subtask.getEpicId());
        Subtask saved = subtaskStorage.get(subtask.getId());
        saved.setStatus(TaskStatus.DONE);
        subtaskStorage.put(subtask.getId(), saved);

        boolean allSubtaskDone = true;
        for (Integer id : epic.getSubtasksId()) {
            Subtask subtaskId = subtaskStorage.get(id);
            if (subtaskId.getStatus() != TaskStatus.DONE) {
                allSubtaskDone = false;
            }

            if (allSubtaskDone) {
                epic.setStatus(TaskStatus.DONE);
                epicStorage.put(epic.getId(), epic);
            }
        }

    }

    private int idGenerator() {
        return ++idGenerated;
    }

}
