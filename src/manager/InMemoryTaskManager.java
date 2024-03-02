package manager;

import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager  {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private static int idSequence = 0;


    // Методы Task
    @Override
    public ArrayList<Task> getTasks() {
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
        return tasks.remove(id);
    }


    // Методы SubTasks
    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public HashMap<Integer, SubTask> deleteAllSubTasks() {
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
        return newSubTask;
    }

    @Override
    public void updateSubTask(SubTask updateSubTask) {
        subTasks.put(updateSubTask.getId(), updateSubTask);
        updateEpic(updateSubTask.getEpic());
    }

    @Override
    public SubTask deleteSubTask(int id) {
        SubTask subTask = subTasks.remove(id);
        subTask.getEpic().getSubTasks().remove(subTask);
        return subTask;
    }


    //Методы Epic
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
    }


    @Override
    public Task getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Epic addEpic(Epic newEpic) {
        newEpic.setId(generateId());
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        epics.put(updateEpic.getId(), updateEpic);

        int counterDone = 0; // счётчик для подзачад со статусом Done
        int counterNew = 0; // счётчик для подзадач со статусом New
        if (updateEpic.getSubTasks() == null) {
            updateEpic.setStatus(Status.NEW);
        } else {
            for (SubTask sub : updateEpic.getSubTasks()) {
                if (sub.getStatus() == Status.DONE) {
                    counterDone++;
                } else if (sub.getStatus() == Status.NEW) {
                    counterNew++;
                } else {
                    updateEpic.setStatus(Status.IN_PROGRESS);
                }
            }
            if (counterDone == updateEpic.getSubTasks().size()) {
                updateEpic.setStatus(Status.DONE);
            }
            if (counterNew == updateEpic.getSubTasks().size()) {
                updateEpic.setStatus(Status.NEW);
            }
        }

    }


    @Override
    public Epic deleteEpic(int id) {
        Epic epic = epics.get(id);
        ArrayList<SubTask> subTasksToDelete = new ArrayList<>();

        //удаляем сабтаски удалённого эпика
        for (SubTask sb : epic.getSubTasks()) {
            subTasksToDelete.add(sb);
        }
        for (SubTask sb : subTasksToDelete) {
            subTasks.remove(sb.getId());
        }

        epics.remove(epic.getId());
        return epic;
    }

    @Override
    public ArrayList<SubTask> getEpicSubTasks(Epic epic) {
        return epic.getSubTasks();
    }

    @Override
    public ArrayList<Task> getHistory(){
        return historyManager.getHistory();
    }

    // метод для генерации ID
    private static int generateId() {
        return idSequence++;
    }


}
