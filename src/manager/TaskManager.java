package manager;

import models.Epic;
import models.SubTask;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {
    // Методы Task
    ArrayList<Task> getTasks();

    void deleteAllTasks();

    Task getTask(int id);

    Task addTask(Task newTask);

    Task updateTask(Task updateTask);

    Task deleteTask(int id);

    // Методы SubTasks
    ArrayList<SubTask> getSubTasks();

    HashMap<Integer, SubTask> deleteAllSubTasks();

    SubTask getSubTask(int id);

    SubTask addSubTask(SubTask newSubTask);

    void updateSubTask(SubTask updateSubTask);

    SubTask deleteSubTask(int id);

    //Методы Epic
    ArrayList<Epic> getEpics();

    void deleteAllEpics();

    Epic getEpic(int id);

    Epic addEpic(Epic newEpic);

    void updateEpic(Epic updateEpic);

    Epic deleteEpic(int id);

    ArrayList<SubTask> getEpicSubTasks(Epic epic);

    ArrayList<Task> getHistory();
}
