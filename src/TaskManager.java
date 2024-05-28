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
        return subtask;
    }

    public ArrayList<Task> getAllTask() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Task taskNum : taskStorage.values()) {
            tasks.add(taskNum);
        }
        return tasks;
    }

    public ArrayList<Epic> getAllEpic() {
        ArrayList<Epic> epics = new ArrayList<>();
        for (Epic taskNum : epicStorage.values()) {
            epics.add(taskNum);
        }
        return epics;
    }

    public ArrayList<Subtask> getAllSubtask() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Subtask taskNum : subtaskStorage.values()) {
            subtasks.add(taskNum);
        }
        return subtasks;
    }

    public void deleteAllTask() {
        taskStorage.clear();
        epicStorage.clear();
        subtaskStorage.clear();
        idGenerated = 0;
    }

    public void deleteAllEpic() {
        epicStorage.clear();
        subtaskStorage.clear();
        idGenerated = 0;
    }

    public void deleteAllSubtask() {
        subtaskStorage.clear();
        for (Epic epic : epicStorage.values()) {
            epic.deleteAllSubtaskId();
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
            if(subtask.getEpicId() == epicId) {
                subtasks.add(subtask);
            }
        }
        return subtasks;
    }

    public void removeTaskById(int id) {
        taskStorage.remove(id);
        subtaskStorage.remove(id);
        epicStorage.remove(id);
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
        subtaskStorage.remove(id);
    }

    public Task updateTask(Task task) {
        Task saved = taskStorage.get(task.getId());
        if(saved == null) {
            return null;
        }
        return taskStorage.put(task.getId(), task);
    }

    public Epic updateEpic(Epic epic) {
        Task saved = epicStorage.get(epic.getId());
        if(saved == null) {
            return null;
        }
        return epicStorage.put(epic.getId(), epic);
    }

    public Subtask updateSubtask(Subtask subtask) {
        Subtask saved = subtaskStorage.get(subtask.getId());
        Epic epic = epicStorage.get(subtask.getEpicId());
        if(saved == null) {
            return null;
        } else if(saved.getStatus() != TaskStatus.DONE) {
            saved.setStatus(TaskStatus.IN_PROGRESS);
            epic.setStatus(TaskStatus.IN_PROGRESS);
            subtaskStorage.put(subtask.getId(), saved);
            epicStorage.put(epic.getId(), epic);
        } else {
            saved.setStatus(TaskStatus.DONE);
            subtaskStorage.put(subtask.getId(), saved);

            boolean allSubtaskDone = true;
            for (Integer id : epic.getSubtasksId()) {
                Subtask subtaskId = subtaskStorage.get(id);
                if (subtaskId.getStatus() != TaskStatus.DONE) {
                    allSubtaskDone = false;
                }

                if(allSubtaskDone) {
                    epic.setStatus(TaskStatus.DONE);
                    epicStorage.put(epic.getId(), epic);
                }
            }


        }
        return subtaskStorage.put(subtask.getId(), subtask);
    }

    private int idGenerator() {
        return ++idGenerated;
    }

}
