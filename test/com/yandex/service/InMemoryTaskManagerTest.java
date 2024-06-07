package com.yandex.service;

import com.yandex.model.Epic;
import com.yandex.model.Subtask;
import com.yandex.model.Task;
import com.yandex.model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private final InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    @Test
    void addNewTask() {
        Task task = new Task("задача1", "описание1");
        inMemoryTaskManager.createTask(task);
        int taskId = task.getId();

        Task savedTask = inMemoryTaskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        int subtaskId = subtask.getId();
        Subtask savedSubtask = inMemoryTaskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
        inMemoryTaskManager.createEpic(epic);

        int epicId = epic.getId();
        Epic savedEpic = inMemoryTaskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");
    }

    @Test
    void addEpicInEpicAsSubtask() {
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
        inMemoryTaskManager.createEpic(epic);
        epic.addSubtaskId(epic.getId());

        assertTrue(epic.getSubtasksId().isEmpty());

        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        epic.addSubtaskId(subtask.getId());

        assertFalse(epic.getSubtasksId().isEmpty());
    }

    @Test
    public void SubtaskDoEpic() {
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        Subtask badSubtask = new Subtask("подзадача2", "описаниеПодзадачи2", subtask.getId());
        inMemoryTaskManager.createSubtask(badSubtask);

        assertTrue(inMemoryTaskManager.getAllSubtask().isEmpty());
        inMemoryTaskManager.createSubtask(subtask);
        assertEquals(1, inMemoryTaskManager.getAllSubtask().size());
    }

    @Test
    void utilClassReturnTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertTrue(taskManager instanceof InMemoryTaskManager);
    }

    @Test
    void utilClassgReturnHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertTrue(historyManager instanceof InMemoryHistoryManager);
    }

    @Test
    void addAllTypeTasksAndFind() {
        Task task = new Task("задача1", "описание1");
        inMemoryTaskManager.createTask(task);
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        assertEquals(task, inMemoryTaskManager.getTaskById(1));
        assertEquals(epic, inMemoryTaskManager.getEpicById(2));
        assertEquals(subtask, inMemoryTaskManager.getSubtaskById(3));
    }

    @Test
    void idTest() {
        Task task = new Task("задача1", "описание1");
        task.setId(99);
        inMemoryTaskManager.createTask(task);

        assertEquals(task, inMemoryTaskManager.getTaskById(1));
    }

    @Test
    void unchangedTaskWhenAdding() {
        Task task = new Task("задача1", "описание1");
        inMemoryTaskManager.createTask(task);

        assertEquals(1, task.getId());
        assertEquals("задача1", task.getName());
        assertEquals("описание1", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void unchangedEpicWhenAdding() {
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
        inMemoryTaskManager.createTask(epic);

        assertEquals(1, epic.getId());
        assertEquals("эпик1", epic.getName());
        assertEquals("описаниеЭпика1", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void unchangedSubtaskWhenAdding() {
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        assertEquals(2, subtask.getId());
        assertEquals("подзадача1", subtask.getName());
        assertEquals("описаниеПодзадачи1", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
    }
}

