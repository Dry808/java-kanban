import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    static int idSequence = 0;


    // Методы Task
    public ArrayList<Task> getTasks(){
        ArrayList<Task> task = new ArrayList<>(tasks.values());
        return task;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTask(int id) {
        Task task = tasks.get(id);
        return task;
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
        Task task = tasks.get(id);
        tasks.remove(id);
        return task;
    }


    // Методы SubTasks
    public ArrayList<SubTask> getSubTasks(){
        ArrayList<SubTask> subTask = new ArrayList<>(subTasks.values());
        return subTask;
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

    }

    public SubTask deleteSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        subTasks.remove(id);
        return subTask;
    }


    //Методы Epic
    public ArrayList<Epic> getEpics(){
        ArrayList<Epic> epic = new ArrayList<>(epics.values());
        return epic;
    }

    public void deleteAllEpics() {
        epics.clear();
    }


    public Task getEpic(int id) {
        Epic epic = epics.get(id);
        return epic;
    }

    public Epic addEpic(Epic newEpic) {
        newEpic.setId(generateId());
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    public void updateEpic(Epic updateEpic) {
        epics.put(updateEpic.getId(), updateEpic);
    }

    public Epic deleteEpic(int id) {
        Epic epic = epics.get(id);
        epics.remove(id);
        return epic;
    }

    public ArrayList<SubTask> getEpicSubTasks(Epic epic) {
        return epic.getSubTasks();
    }



    static int generateId() {
        return idSequence++;
    }




}
