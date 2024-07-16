package com.yandex.service;

import com.yandex.model.Epic;
import com.yandex.model.Subtask;
import com.yandex.model.Task;
import com.yandex.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private final TaskManager inMemoryTaskManager = Managers.getDefault();
    private Task task;
    private Epic epic;

    @BeforeEach
    void init() {
        task = new Task("задача1", "описание1");
        epic = new Epic("эпик1", "описаниеЭпика1");
    }

    @Test
    void createTask_shouldCreateNewTask_whenCalled() {
        inMemoryTaskManager.createTask(task);
        int taskId = task.getId();

        Task savedTask = inMemoryTaskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void createSubtask_shouldCreateNewSubtask_whenCalled() {
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        int subtaskId = subtask.getId();
        Subtask savedSubtask = inMemoryTaskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");
    }

    @Test
    void createEpic_shouldCreateNewEpic_whenCalled() {
        inMemoryTaskManager.createEpic(epic);

        int epicId = epic.getId();
        Epic savedEpic = inMemoryTaskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");
    }

    @Test
    void addSubtaskId_shouldNotAddSubtaskInEpic_whenSubtaskIsEpic() {
        inMemoryTaskManager.createEpic(epic);
        epic.addSubtaskId(epic.getId());
        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());

        assertTrue(epic.getSubtasksId().isEmpty());

        inMemoryTaskManager.createSubtask(subtask);
        epic.addSubtaskId(subtask.getId());

        assertFalse(epic.getSubtasksId().isEmpty());
    }

    @Test
    void createSubtask_shouldNotCreateSubtask_whenSubtaskEpicIdEqualSubtaskId() {
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        inMemoryTaskManager.createSubtask(subtask);
        Subtask badSubtask = new Subtask("подзадача2", "описаниеПодзадачи2", subtask.getId());
        inMemoryTaskManager.createSubtask(badSubtask);

        assertEquals(1, inMemoryTaskManager.getAllSubtask().size());
        assertEquals(subtask, inMemoryTaskManager.getAllSubtask().getFirst());
    }

    @Test
    void getDefault_shouldReturnInMemoryTaskManager_whenCalled() {
        TaskManager taskManager = Managers.getDefault();
        assertInstanceOf(InMemoryTaskManager.class, taskManager);
    }

    @Test
    void getDefaultHistory_shouldReturnInMemoryHistoryManager_whenCalled() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertInstanceOf(InMemoryHistoryManager.class, historyManager);
    }

    @Test
    void getTaskById_shouldReturnCreatedTask_whenTaskExist() {
        inMemoryTaskManager.createTask(task);

        assertEquals(task, inMemoryTaskManager.getTaskById(1));
    }

    @Test
    void getEpicById_shouldReturnCreatedEpic_whenEpicExist() {
        inMemoryTaskManager.createEpic(epic);

        assertEquals(epic, inMemoryTaskManager.getEpicById(1));
    }

    @Test
    void getSubtaskById_shouldReturnCreatedSubtask_whenSubtaskExist() {
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        assertEquals(subtask, inMemoryTaskManager.getSubtaskById(2));
    }

    @Test
    void getTaskById_shouldReturnInitialTask_whenTaskIdModifiedBeforeCreate() {
        task.setId(99);
        inMemoryTaskManager.createTask(task);

        assertEquals(task, inMemoryTaskManager.getTaskById(1));
    }

    @Test
    void createTask_shouldCreateTaskWithoutChanges_whenCalled() {
        inMemoryTaskManager.createTask(task);

        assertEquals(1, task.getId());
        assertEquals("задача1", task.getName());
        assertEquals("описание1", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void createEpic_shouldCreateEpicWithoutChanges_whenCalled() {
        inMemoryTaskManager.createTask(epic);

        assertEquals(1, epic.getId());
        assertEquals("эпик1", epic.getName());
        assertEquals("описаниеЭпика1", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void createSubtask_shouldCreateSubtaskWithoutChanges_whenCalled() {
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        assertEquals(2, subtask.getId());
        assertEquals("подзадача1", subtask.getName());
        assertEquals("описаниеПодзадачи1", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
    }

    @Test
    void createSubtask_shouldCreateSubtaskWithIdNotEqualsDeleted_whenCalled() {
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        int taskId = subtask.getId();
        inMemoryTaskManager.removeSubtaskById(subtask.getId());

        Subtask subtask2 = new Subtask("подзадача2", "описаниеПодзадачи2", epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        assertNotEquals(taskId, subtask2.getId());
    }

    @Test
    void removeSubtaskById_shouldRemoveSubtaskIdFromEpicSubtasks_whenSubtaskIsExistAndSubtaskIsEpicSubtask() {
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        inMemoryTaskManager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("подзадача2", "описаниеПодзадачи2", epic.getId());
        inMemoryTaskManager.createSubtask(subtask2);

        assertEquals(subtask.getEpicId(), epic.getId());
        assertEquals(subtask2.getEpicId(), epic.getId());

        inMemoryTaskManager.removeSubtaskById(subtask2.getId());

        assertEquals(1, epic.getSubtasksId().size());
        assertEquals(subtask.getId(), epic.getSubtasksId().getFirst());
    }

    @Test
    void setName_shouldChangeName_whenCalled() {
        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        task.setName("задача5");
        epic.setName("задача6");
        subtask.setName("задача7");

        assertEquals("задача5", inMemoryTaskManager.getTaskById(task.getId()).getName());
        assertEquals("задача6", inMemoryTaskManager.getEpicById(epic.getId()).getName());
        assertEquals("задача7", inMemoryTaskManager.getSubtaskById(subtask.getId()).getName());
    }

    @Test
    void setDescription_shouldChangeName_whenCalled() {
        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        task.setDescription("описание5");
        epic.setDescription("описание6");
        subtask.setDescription("описание7");

        assertEquals("описание5", inMemoryTaskManager.getTaskById(task.getId()).getDescription());
        assertEquals("описание6", inMemoryTaskManager.getEpicById(epic.getId()).getDescription());
        assertEquals("описание7", inMemoryTaskManager.getSubtaskById(subtask.getId()).getDescription());
    }

    @Test
    void setStatus_shouldChangeName_whenCalled() {
        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        task.setStatus(TaskStatus.IN_PROGRESS);
        epic.setStatus(TaskStatus.IN_PROGRESS);
        subtask.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, inMemoryTaskManager.getTaskById(task.getId()).getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, inMemoryTaskManager.getEpicById(epic.getId()).getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, inMemoryTaskManager.getSubtaskById(subtask.getId()).getStatus());
    }
}

