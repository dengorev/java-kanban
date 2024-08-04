package com.yandex.service;

import com.yandex.exception.ManagerSaveException;
import com.yandex.model.Epic;
import com.yandex.model.Subtask;
import com.yandex.model.Task;
import com.yandex.model.TypeTasks;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        int maxId = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    Task task = CSVFormatter.fromString(line);
                    if (task.getId() > maxId) {
                        maxId = task.getId();
                    }
                    loadTasks(task, fileBackedTaskManager);
                }
            }
        } catch (IOException e) {
            throw ManagerSaveException.loadException(e);
        }
        fileBackedTaskManager.setIdGenerated(maxId);
        return fileBackedTaskManager;
    }

    @Override
    public Task createTask(Task task) {
        Task createTask = super.createTask(task);
        save();
        return createTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createEpic = super.createEpic(epic);
        save();
        return createEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createSubtask = super.createSubtask(subtask);
        save();
        return createSubtask;
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public Task updateTask(Task task) {
        Task updateTask = super.updateTask(task);
        save();
        return updateTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updateEpic = super.updateEpic(epic);
        save();
        return updateEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updateSubtask = super.updateSubtask(subtask);
        save();
        return updateSubtask;
    }

    private static void loadTasks(Task task, FileBackedTaskManager fileBackedTaskManager) {
        if (task.getTypeTasks().equals(TypeTasks.TASK)) {
            fileBackedTaskManager.taskStorage.put(task.getId(), task);
        } else if (task.getTypeTasks().equals(TypeTasks.EPIC)) {
            Epic epic = (Epic) task;
            fileBackedTaskManager.epicStorage.put(epic.getId(), epic);
        } else if (task.getTypeTasks().equals(TypeTasks.SUBTASK)) {
            Subtask subtask = (Subtask) task;
            fileBackedTaskManager.subtaskStorage.put(subtask.getId(), subtask);
            Epic epic = fileBackedTaskManager.epicStorage.get(subtask.getEpicId());
            epic.addSubtaskId(subtask.getId());
        }
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(CSVFormatter.getHeader());
            bw.newLine();
            for (Task task : taskStorage.values()) {
                bw.write(CSVFormatter.toString(task));
                bw.newLine();
            }
            for (Epic epic : epicStorage.values()) {
                bw.write(CSVFormatter.toString(epic));
                bw.newLine();
            }
            for (Subtask subtask : subtaskStorage.values()) {
                bw.write(CSVFormatter.toString(subtask));
                bw.newLine();
            }
        } catch (IOException e) {
            throw ManagerSaveException.saveException(e);
        }
    }
}


