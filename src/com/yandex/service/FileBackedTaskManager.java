package com.yandex.service;

import com.yandex.model.Epic;
import com.yandex.model.Subtask;
import com.yandex.model.Task;
import com.yandex.model.TypeTasks;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static Path PATH_TO_FILE = Path.of("src/resources/tasks.csv");

    private static File file = new File(String.valueOf(PATH_TO_FILE));

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            if (!br.readLine().isBlank()) {
                String line;
                while ((line = br.readLine()) != null) {

                    if (!line.isEmpty()) {
                        Task task = CSVFormatter.fromString(line);
                        if (task.getTypeTasks().equals(TypeTasks.TASK)) {
                            fileBackedTaskManager.taskStorage.put(task.getId(), task);
                            fileBackedTaskManager.createTask(task);
                        } else if (task.getTypeTasks().equals(TypeTasks.EPIC)) {
                            Epic epic = (Epic) task;
                            fileBackedTaskManager.createEpic(epic);
                        } else if (task.getTypeTasks().equals(TypeTasks.SUBTASK)) {
                            Subtask subtask = (Subtask) task;
                            fileBackedTaskManager.createSubtask(subtask);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw ManagerSaveException.loadException(e);
        }
        return fileBackedTaskManager;
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
    public ArrayList<Task> getAllTask() {
        return super.getAllTask();
    }

    @Override
    public ArrayList<Epic> getAllEpic() {
        return super.getAllEpic();
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return super.getAllSubtask();
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
    public Task getTaskById(int id) {
         return super.getTaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return super.getEpicById(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return super.getSubtaskById(id);
    }

    @Override
    public ArrayList<Subtask> getSubtaskByEpicId(int epicId) {
        return super.getSubtaskByEpicId(epicId);
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
}


