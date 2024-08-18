package com.yandex.service;

import com.yandex.exception.TaskValidateException;
import com.yandex.model.Epic;
import com.yandex.model.Subtask;
import com.yandex.model.Task;
import com.yandex.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private final TaskManager inMemoryTaskManager = Managers.getDefault();
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void init() {
        task = new Task("задача1", "описание1",
                LocalDateTime.of(2024,1,5,12,2), Duration.ofMinutes(60));
        inMemoryTaskManager.createTask(task);
        epic = new Epic("эпик1", "описаниеЭпика1");
        inMemoryTaskManager.createEpic(epic);
        subtask = new Subtask("подзадача1", "описаниеПодзадачи1",
                LocalDateTime.of(2024,1,5,12,2), Duration.ofMinutes(60), epic.getId());
        inMemoryTaskManager.createSubtask(subtask);
    }

    @Test
    void shouldCreateNewTask() {
        int taskId = task.getId();

        Task savedTask = inMemoryTaskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void shouldCreateNewSubtask() {
        int subtaskId = subtask.getId();
        Subtask savedSubtask = inMemoryTaskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");
    }

    @Test
    void shouldCreateNewEpic() {
        int epicId = epic.getId();
        Epic savedEpic = inMemoryTaskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");
    }

    @Test
    void shouldNotAddSubtaskInEpic() {
        Epic epic2 = new Epic("эпик2", "описаниеЭпика2");
        inMemoryTaskManager.createEpic(epic2);
        epic2.addSubtaskId(epic2.getId());

        assertTrue(epic2.getSubtasksId().isEmpty());

        inMemoryTaskManager.createSubtask(subtask);
        epic2.addSubtaskId(subtask.getId());

        assertFalse(epic2.getSubtasksId().isEmpty());
    }

    @Test
    void shouldNotCreateSubtask() {
        Subtask badSubtask = new Subtask("подзадача2", "описаниеПодзадачи2",
                LocalDateTime.of(2024,2,5,12,2), Duration.ofMinutes(60), subtask.getId());
        inMemoryTaskManager.createSubtask(badSubtask);

        assertEquals(1, inMemoryTaskManager.getAllSubtask().size());
        assertEquals(subtask, inMemoryTaskManager.getAllSubtask().getFirst());
    }

    @Test
    void shouldReturnInMemoryTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertInstanceOf(InMemoryTaskManager.class, taskManager);
    }

    @Test
    void shouldReturnInMemoryHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertInstanceOf(InMemoryHistoryManager.class, historyManager);
    }

    @Test
    void shouldReturnCreatedTask() {
        assertEquals(task, inMemoryTaskManager.getTaskById(1));
    }

    @Test
    void shouldReturnCreatedEpic() {
        assertEquals(epic, inMemoryTaskManager.getEpicById(2));
    }

    @Test
    void shouldReturnCreatedSubtask() {
        assertEquals(subtask, inMemoryTaskManager.getSubtaskById(3));
    }

    @Test
    void shouldReturnInitialTask() {
        task.setId(99);

        assertEquals(task, inMemoryTaskManager.getTaskById(1));
    }

    @Test
    void shouldCreateTaskWithoutChanges() {
        assertEquals(1, task.getId());
        assertEquals("задача1", task.getName());
        assertEquals("описание1", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void shouldCreateEpicWithoutChanges() {
        assertEquals(2, epic.getId());
        assertEquals("эпик1", epic.getName());
        assertEquals("описаниеЭпика1", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void shouldCreateSubtaskWithoutChanges() {
        assertEquals(3, subtask.getId());
        assertEquals("подзадача1", subtask.getName());
        assertEquals("описаниеПодзадачи1", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
    }

    @Test
    void shouldCreateSubtaskWithIdNotEqualsDeleted() {
        int taskId = subtask.getId();
        inMemoryTaskManager.removeSubtaskById(subtask.getId());

        Subtask subtask2 = new Subtask("подзадача2", "описаниеПодзадачи2",
                LocalDateTime.of(2024,2,5,12,2), Duration.ofMinutes(60), epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        assertNotEquals(taskId, subtask2.getId());
    }

    @Test
    void shouldRemoveSubtaskIdFromEpicSubtasks() {
        Subtask subtask2 = new Subtask("подзадача2", "описаниеПодзадачи2",
                LocalDateTime.of(2024,2,5,12,2), Duration.ofMinutes(60), epic.getId());
        inMemoryTaskManager.createSubtask(subtask2);

        assertEquals(subtask.getEpicId(), epic.getId());
        assertEquals(subtask2.getEpicId(), epic.getId());

        inMemoryTaskManager.removeSubtaskById(subtask2.getId());

        assertEquals(1, epic.getSubtasksId().size());
        assertEquals(subtask.getId(), epic.getSubtasksId().getFirst());
    }

    @Test
    void shouldChangeName() {
        task.setName("задача5");
        epic.setName("задача6");
        subtask.setName("задача7");

        assertEquals("задача5", inMemoryTaskManager.getTaskById(task.getId()).getName());
        assertEquals("задача6", inMemoryTaskManager.getEpicById(epic.getId()).getName());
        assertEquals("задача7", inMemoryTaskManager.getSubtaskById(subtask.getId()).getName());
    }

    @Test
    void shouldChangeDescription() {
        task.setDescription("описание5");
        epic.setDescription("описание6");
        subtask.setDescription("описание7");

        assertEquals("описание5", inMemoryTaskManager.getTaskById(task.getId()).getDescription());
        assertEquals("описание6", inMemoryTaskManager.getEpicById(epic.getId()).getDescription());
        assertEquals("описание7", inMemoryTaskManager.getSubtaskById(subtask.getId()).getDescription());
    }

    @Test
    void shouldChangeStatus() {
        task.setStatus(TaskStatus.IN_PROGRESS);
        epic.setStatus(TaskStatus.IN_PROGRESS);
        subtask.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, inMemoryTaskManager.getTaskById(task.getId()).getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, inMemoryTaskManager.getEpicById(epic.getId()).getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, inMemoryTaskManager.getSubtaskById(subtask.getId()).getStatus());
    }

    @Test
    void shouldCorrectlyDefineEpicStatus() {
        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        inMemoryTaskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask("подзадача1", "описаниеПодзадачи1",
                LocalDateTime.of(2024,1,5,12,2), Duration.ofMinutes(60), epic2.getId());
        Subtask subtask2 = new Subtask("подзадача2", "описаниеПодзадачи2",
                LocalDateTime.of(2024,2,5,12,2), Duration.ofMinutes(60), epic2.getId());
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.NEW, subtask1.getStatus());
        assertEquals(TaskStatus.NEW, subtask2.getStatus());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        subtask2.setStatus(TaskStatus.DONE);
        subtask1.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(subtask1);
        inMemoryTaskManager.updateSubtask(subtask2);

        assertEquals(TaskStatus.DONE, epic2.getStatus());

        subtask2.setStatus(TaskStatus.NEW);
        subtask1.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateSubtask(subtask1);
        inMemoryTaskManager.updateSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic2.getStatus());

        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask1);
        inMemoryTaskManager.updateSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic2.getStatus());
    }

    @Test
    void shouldCorrectlyCalculateIntersectionOfIntervals() {
        Subtask subtask2 = new Subtask("подзадача2", "описаниеПодзадачи2",
                LocalDateTime.of(2024,1,5,12,12), Duration.ofMinutes(60), epic.getId());

        TaskValidateException exc = assertThrows(TaskValidateException.class, () -> inMemoryTaskManager.createSubtask(subtask2));
        assertEquals("Задача пересекается", exc.getMessage());
    }
}

