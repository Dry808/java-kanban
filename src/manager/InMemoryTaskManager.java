package manager;

import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager  {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private int idSequence = 0;


    // Методы Task
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);

    }


    @Override
    public Task addTask(Task newTask) {
        newTask.setId(generateId());
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    @Override
    public Task updateTask(Task updateTask) {
        tasks.put(updateTask.getId(), updateTask);
        return updateTask;
    }

    @Override
    public Task deleteTask(int id) {
        historyManager.remove(id);
        return tasks.remove(id);

    }


    // Методы SubTasks
    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public Map<Integer, SubTask> deleteAllSubTasks() {
        for (Epic epic : epics.values()) {
            getEpicSubTasks(epic).clear();
            changeEpicStatus(epic);
        }
        subTasks.clear();
        return subTasks;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public SubTask addSubTask(SubTask newSubTask) {
        newSubTask.setId(generateId());
        subTasks.put(newSubTask.getId(), newSubTask);
        changeEpicStatus(newSubTask.getEpic());
        return newSubTask;
    }

    @Override
    public void updateSubTask(SubTask updateSubTask) {
        subTasks.put(updateSubTask.getId(), updateSubTask);
        changeEpicStatus(updateSubTask.getEpic());
    }

    @Override
    public SubTask deleteSubTask(int id) {
        SubTask subTask = subTasks.remove(id);
        subTask.getEpic().getSubTasks().remove(subTask);
        changeEpicStatus(subTask.getEpic());
        historyManager.remove(id);
        return subTask;
    }


    //Методы Epic
    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
    }


    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Epic addEpic(Epic newEpic) {
        newEpic.setId(generateId());
        changeEpicStatus(newEpic);
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }


    @Override
    public void updateEpic(Epic updateEpic) {
        epics.put(updateEpic.getId(), updateEpic);
    }


    @Override
    public Epic deleteEpic(int id) {
        Epic epic = epics.get(id);
        List<SubTask> subTasksToDelete = new ArrayList<>();

        //удаляем сабтаски удалённого эпика
        subTasksToDelete.addAll(epic.getSubTasks());
        for (SubTask sb : subTasksToDelete) {
            historyManager.remove(sb.getId());
            subTasks.remove(sb.getId());


        }

        epics.remove(epic.getId());
        historyManager.remove(id);
        return epic;
    }

    @Override
    public List<SubTask> getEpicSubTasks(Epic epic) {
        return epic.getSubTasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void changeEpicStatus(Epic epic) {
        int counterDone = 0; // счётчик для подзачад со статусом Done
        int counterNew = 0; // счётчик для подзадач со статусом New
        if (epic.getSubTasks() == null) {
            epic.setStatus(Status.NEW);
        } else {
            for (SubTask sub : epic.getSubTasks()) {
                if (sub.getStatus() == Status.DONE) {
                    counterDone++;
                } else if (sub.getStatus() == Status.NEW) {
                    counterNew++;
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
            if (counterDone == epic.getSubTasks().size()) {
                epic.setStatus(Status.DONE);
            }
            if (counterNew == epic.getSubTasks().size()) {
                epic.setStatus(Status.NEW);
            }
        }
    }

    // метод для генерации ID
    private int generateId() {
        return idSequence++;
    }


}
