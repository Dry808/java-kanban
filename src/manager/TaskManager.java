package manager;

import models.Epic;
import models.SubTask;
import models.Task;


import java.util.List;
import java.util.Map;

public interface TaskManager {
    // Методы Task
    List<Task> getTasks();

    void deleteAllTasks();

    Task getTask(int id);

    Task addTask(Task newTask);

    Task updateTask(Task updateTask);

    Task deleteTask(int id);

    // Методы SubTasks
    List<SubTask> getSubTasks();

    Map<Integer, SubTask> deleteAllSubTasks();

    SubTask getSubTask(int id);

    SubTask addSubTask(SubTask newSubTask);

    void updateSubTask(SubTask updateSubTask);

    SubTask deleteSubTask(int id);

    //Методы Epic
    List<Epic> getEpics();

    void deleteAllEpics();

    Epic getEpic(int id);

    Epic addEpic(Epic newEpic);

    void updateEpic(Epic updateEpic);

    Epic deleteEpic(int id);

    List<Integer> getEpicSubTasks(Epic epic);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
