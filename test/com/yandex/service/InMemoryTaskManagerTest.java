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
    void createTask_shouldCreateNewTask_whenCalled() {
        Task task = new Task("задача1", "описание1");
        inMemoryTaskManager.createTask(task);
        int taskId = task.getId();

        Task savedTask = inMemoryTaskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void createSubtask_shouldCreateNewSubtask_whenCalled() {
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
    void createEpic_shouldCreateNewEpic_whenCalled() {
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
        inMemoryTaskManager.createEpic(epic);

        int epicId = epic.getId();
        Epic savedEpic = inMemoryTaskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");
    }

    @Test
    void addSubtaskId_shouldNotAddSubtaskInEpic_whenSubtaskIsEpic() {
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
        inMemoryTaskManager.createEpic(epic);
        epic.addSubtaskId(epic.getId());

        assertTrue(epic.getSubtasksId().isEmpty());

        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        epic.addSubtaskId(subtask.getId());

        assertFalse(epic.getSubtasksId().isEmpty());
    }

    @Test
    void createSubtask_shouldNotCreateSubtask_whenSubtaskIdEqualEpicId() {
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
    void getDefault_shouldReturnInMemoryTaskManager_whenCalled() {
        TaskManager taskManager = Managers.getDefault();
        assertTrue(taskManager instanceof InMemoryTaskManager);
    }

    @Test
    void getDefaultHistory_shouldReturnInMemoryHistoryManager_whenCalled() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertTrue(historyManager instanceof InMemoryHistoryManager);
    }

    @Test
    void getTaskById_shouldReturnCreatedTask_whenTaskExist() {
        Task task = new Task("задача1", "описание1");
        inMemoryTaskManager.createTask(task);

        assertEquals(task, inMemoryTaskManager.getTaskById(1));
    }

    @Test
    void getEpicById_shouldReturnCreatedEpic_whenEpicExist() {
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
        inMemoryTaskManager.createEpic(epic);

        assertEquals(epic, inMemoryTaskManager.getEpicById(1));
    }

    @Test
    void getSubtaskById_shouldReturnCreatedSubtask_whenSubtaskExist() {
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        inMemoryTaskManager.createSubtask(subtask);

        assertEquals(subtask, inMemoryTaskManager.getSubtaskById(2));
    }

    @Test
    void getTaskById_shouldReturnInitialTask_whenTaskIdModifiedBeforeCreate() {
        Task task = new Task("задача1", "описание1");
        task.setId(99);
        inMemoryTaskManager.createTask(task);

        assertEquals(task, inMemoryTaskManager.getTaskById(1));
    }

    @Test
    void createTask_shouldCreateTaskWithoutChanges_whenCalled() {
        Task task = new Task("задача1", "описание1");
        inMemoryTaskManager.createTask(task);

        assertEquals(1, task.getId());
        assertEquals("задача1", task.getName());
        assertEquals("описание1", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void createEpic_shouldCreateEpicWithoutChanges_whenCalled() {
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
        inMemoryTaskManager.createTask(epic);

        assertEquals(1, epic.getId());
        assertEquals("эпик1", epic.getName());
        assertEquals("описаниеЭпика1", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void createSubtask_shouldCreateSubtaskWithoutChanges_whenCalled() {
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
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
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
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
    void removeSubtaskById_shouldRemoveSubtaskIdFromEpicSubtasks_whenSubtaskIsExistAndSubtaskIsEpicSubtask () {
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
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
        Task task = new Task("задача1", "описание1");
        inMemoryTaskManager.createTask(task);
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
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
        Task task = new Task("задача1", "описание1");
        inMemoryTaskManager.createTask(task);
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
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
        Task task = new Task("задача1", "описание1");
        inMemoryTaskManager.createTask(task);
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
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

   /* @Test
    void setId_shouldChangeName_whenCalled() {
        Task task = new Task("задача1", "описание1");
        inMemoryTaskManager.createTask(task);
        int taskId = task.getId();
        Epic epic = new Epic("эпик1", "описаниеЭпика1");
        inMemoryTaskManager.createEpic(epic);
        int epicId = epic.getId();
        Subtask subtask = new Subtask("подзадача1", "описаниеПодзадачи1", epic.getId());
        inMemoryTaskManager.createSubtask(subtask);
        int subtaskId = subtask.getId();

        task.setId(5);
        epic.setId(6);
        subtask.setId(7);
        subtask.setEpicId(6);

        inMemoryTaskManager.removeTaskById(taskId);
        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.removeEpicById(epicId);
        inMemoryTaskManager.createEpic(epic);

        assertEquals(5, inMemoryTaskManager.getTaskById(task.getId()).getId());
        assertEquals(6, inMemoryTaskManager.getEpicById(epic.getId()).getId());
        assertEquals(7, inMemoryTaskManager.getSubtaskById(subtask.getId()).getId());

        Метод проверки SetId не отрабатывает т.к. в HashMap ключ еще старый id. Я предполагаю, что SetId должен быть
         приватным методом и методы createTask и др. не должны принимать в себя экземляры создаваемых классов,
          а должны принимать в себя, что сейчас принимают конструкторы этих классов т.е. Name Desc Id и др.
    } */
}

