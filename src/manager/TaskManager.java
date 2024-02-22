package manager;

import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private static int idSequence = 0;


    // Методы Task
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }


    public Task addTask(Task newTask) {
        newTask.setId(generateId());
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    public Task updateTask(Task updateTask) {
        tasks.put(updateTask.getId(), updateTask);
        return updateTask;
    }

    public Task deleteTask(int id) {
        return tasks.remove(id);
    }


    // Методы SubTasks
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public HashMap<Integer, SubTask> deleteAllSubTasks() {
        subTasks.clear();
        return subTasks;
    }

    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        return subTask;
    }

    public SubTask addSubTask(SubTask newSubTask) {
        newSubTask.setId(generateId());
        subTasks.put(newSubTask.getId(), newSubTask);
        return newSubTask;
    }

    public void updateSubTask(SubTask updateSubTask) {
        subTasks.put(updateSubTask.getId(), updateSubTask);
        updateEpic(updateSubTask.getEpic());
    }

    public SubTask deleteSubTask(int id) {
        SubTask subTask = subTasks.remove(id);
        subTask.getEpic().getSubTasks().remove(subTask);
        return subTask;
    }


    //Методы Epic
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        epics.clear();
    }


    public Task getEpic(int id) {
        return epics.get(id);
    }

    public Epic addEpic(Epic newEpic) {
        newEpic.setId(generateId());
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

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

    public ArrayList<SubTask> getEpicSubTasks(Epic epic) {
        return epic.getSubTasks();
    }


    // метод для генерации ID
    private static int generateId() {
        return idSequence++;
    }


}
