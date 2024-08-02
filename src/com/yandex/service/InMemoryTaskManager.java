package com.yandex.service;

import com.yandex.exception.TaskValidateException;
import com.yandex.model.Epic;
import com.yandex.model.Subtask;
import com.yandex.model.Task;
import com.yandex.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HistoryManager historyManager;

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
    }

    protected final Map<Integer, Task> taskStorage = new HashMap<>();
    protected final Map<Integer, Epic> epicStorage = new HashMap<>();
    protected final Map<Integer, Subtask> subtaskStorage = new HashMap<>();
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartDateTime));

    private int idGenerated = 0;

    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private void validate(Task task) {
        LocalDateTime startDateTime = task.getStartDateTime();
        LocalDateTime endDateTime = task.getEndDataTime();

        Integer result = prioritizedTasks.stream()
                .map(currentTask -> {
                    if (startDateTime.isBefore(currentTask.getEndDataTime()) && endDateTime.isBefore(currentTask
                            .getStartDateTime())) {
                        return 1;
                    }
                    if (startDateTime.isBefore(currentTask.getEndDataTime()) && endDateTime.isAfter(currentTask
                            .getEndDataTime())) {
                        return 1;
                    }
                    return 0;
                })
                .reduce(Integer::sum)
                .orElse(0);
        if (result > 0) {
            throw new TaskValidateException("Задача пересекается");
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task createTask(Task task) {
        if (task == null) {
            return null;
        }
        int id = idGenerator();
        task.setId(id);
        validate(task);
        taskStorage.put(id, task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        int id = idGenerator();
        epic.setId(id);
        updateEpicStatus(epic);
        epicStorage.put(id, epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null) {
            return null;
        }
        int id = idGenerator();
        subtask.setId(id);
        Epic epic = epicStorage.get(subtask.getEpicId());
        if (epic != null) {
            if (epic.getStartDateTime() == null) {
                epic.setStartDateTime(subtask.getStartDateTime());
                epic.setDuration(subtask.getDuration());
            } else if (subtask.getStartDateTime().isBefore(epic.getStartDateTime())) {
                epic.setStartDateTime(subtask.getStartDateTime());
                epic.addDuration(subtask.getDuration());
            } else {
                epic.addDuration(subtask.getDuration());
            }
            validate(subtask);
            subtaskStorage.put(id, subtask);
            prioritizedTasks.add(subtask);
            epic.addSubtaskId(id);
            updateEpicStatus(epic);
            return subtask;
        }
        System.out.println("Epic с таким id не существует");
        return null;
    }

    @Override
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(taskStorage.values());
    }

    @Override
    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epicStorage.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtaskStorage.values());
    }

    @Override
    public void deleteAllTask() {
        taskStorage.clear();
    }

    @Override
    public void deleteAllEpic() {
        epicStorage.clear();
        subtaskStorage.clear();
    }

    @Override
    public void deleteAllSubtask() {
        subtaskStorage.clear();
        for (Epic epic : epicStorage.values()) {
            epic.deleteAllSubtaskId();
            updateEpicStatus(epic);
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskStorage.get(id);
        if (task != null) {
            historyManager.add(task);
            return task;
        }
        return null;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicStorage.get(id);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        }
        return null;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtaskStorage.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public List<Subtask> getSubtaskByEpicId(int epicId) {
        return subtaskStorage.values().stream()
                .filter(subtask -> epicId == subtask.getEpicId())
                .collect(Collectors.toList());
    }

    @Override
    public void removeTaskById(int id) {
        taskStorage.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epicStorage.get(id);
        epicStorage.remove(id);
        for (Integer subtaskIds : epic.getSubtasksId()) {
            epic.getSubtasksId().remove(subtaskStorage.get(subtaskIds));
            subtaskStorage.remove(subtaskIds);
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        Epic epic = epicStorage.get(subtaskStorage.get(id).getEpicId());
        epic.deleteSubtaskId(id);
        subtaskStorage.remove(id);
        updateEpicStatus(epic);
    }

    @Override
    public Task updateTask(Task task) {
        if (taskStorage.get(task.getId()) == null) {
            return null;
        }
        return taskStorage.put(task.getId(), task);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epicStorage.get(epic.getId()) == null) {
            return null;
        }
        return epicStorage.put(epic.getId(), epic);
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask saved = subtaskStorage.get(subtask.getId());
        Epic epic = epicStorage.get(subtask.getEpicId());
        if (saved == null) {
            return null;
        }
        updateEpicStatus(epic);
        return subtaskStorage.put(subtask.getId(), subtask);
    }

    private void updateEpicStatus(Epic epic) {
        boolean isInProgress = false;
        boolean isDone = false;
        boolean isNew = false;

        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        for (Integer id : epic.getSubtasksId()) {
            if (subtaskStorage.get(id).getStatus() == TaskStatus.IN_PROGRESS) {
                isInProgress = true;
            }
            if (subtaskStorage.get(id).getStatus() == TaskStatus.DONE) {
                isDone = true;
            }
            if (subtaskStorage.get(id).getStatus() == TaskStatus.NEW) {
                isNew = true;
            }
        }

        if (isNew && !isDone && !isInProgress) {
            epic.setStatus(TaskStatus.NEW);
        } else if (isDone && !isNew && !isInProgress) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private int idGenerator() {
        return ++idGenerated;
    }

    public void setIdGenerated(int idGenerated) {
        this.idGenerated = idGenerated;
    }
}
