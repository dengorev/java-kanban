package com.yandex.service;

import com.yandex.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileBackedTaskManagerTest {
    private static File file;
    private FileBackedTaskManager fileBackedTaskManager;
    private Task task1;
    private static final int TITLE_FROM_FILE = 1;

    @BeforeEach
    void init() throws IOException {
        file = File.createTempFile("test", ".csv", null);
        fileBackedTaskManager = Managers.getDefaultFileBackedTaskManager(file);
        task1 = new Task("zadacha1", "opisanieZadachi1");
    }

    @Test
    public void save_shouldSaveEmptyFile_whenCalled() throws IOException {
        fileBackedTaskManager.createTask(null);

        Assertions.assertEquals(Files.readAllLines(file.toPath()).size(),TITLE_FROM_FILE);
    }

    @Test
    public void load_shouldLoadEmptyFile_whenCalled() {
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        fileBackedTaskManager.createTask(null);

        Assertions.assertTrue(fileBackedTaskManager.taskStorage.isEmpty());
        Assertions.assertTrue(fileBackedTaskManager.epicStorage.isEmpty());
        Assertions.assertTrue(fileBackedTaskManager.subtaskStorage.isEmpty());
    }

    @Test
    public void save_shouldSaveSomeTasks_whenCalled() throws IOException {
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        fileBackedTaskManager.createTask(task1);

        Assertions.assertEquals(Files.readAllLines(file.toPath()).size(), fileBackedTaskManager.getAllTask().size() + TITLE_FROM_FILE);
    }

    @Test
    public void load_shouldLoadSomeTasks_whenCalled() {
        fileBackedTaskManager.createTask(task1);
        Task task2 = new Task("zadacha2", "opisanieZadachi2");
        fileBackedTaskManager.createTask(task2);
        ArrayList<Task> beforeList = fileBackedTaskManager.getAllTask();

        Assertions.assertEquals(2, beforeList.size());

        fileBackedTaskManager.taskStorage.remove(task1.getId());
        fileBackedTaskManager.taskStorage.remove(task2.getId());

        Assertions.assertTrue(fileBackedTaskManager.getAllTask().isEmpty());
        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(file);
        ArrayList<Task> afterList = fileBackedTaskManager1.getAllTask();

        Assertions.assertEquals(2, afterList.size());
    }
}
