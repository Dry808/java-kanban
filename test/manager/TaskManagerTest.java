package manager;

import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @BeforeEach
    public void init() {
        taskManager = createTaskManager();
    }

    protected abstract T createTaskManager();

    @Test
    void getTasksAndDeleteAllTasks() {
        Task task = new Task("Задача 1", "Описание 1", Status.IN_PROGRESS);
        Task task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS);
        taskManager.addTask(task);
        taskManager.addTask(task2);

        taskManager.deleteAllTasks();

        Assertions.assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void addAndGetTask() {
        Task task = new Task("Задача 1", "Описание 1", Status.IN_PROGRESS);
        taskManager.addTask(task);

        Assertions.assertEquals(task, taskManager.getTask(task.getId()));
    }

    @Test
    void updateTask() {
        Task task1 = new Task("Задача 1", "Описание 1", Status.IN_PROGRESS);
        Task newTask1 = new Task("Задача 1", "Другое писание 2", Status.IN_PROGRESS);

        taskManager.addTask(task1);
        taskManager.updateTask(newTask1);
        Task updatedTask1 = taskManager.getTask(task1.getId());

        Assertions.assertEquals(newTask1,updatedTask1);
    }

    @Test
    void deleteTask() {
        Task task = new Task("Задача 1", "Описание 1", Status.IN_PROGRESS);
        taskManager.addTask(task);

        taskManager.deleteTask(0);

        Assertions.assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void getAndDeleteAllSubTasks() {
        Epic epic = new Epic("epic", "newEPic", Status.IN_PROGRESS);
        SubTask subTask = new SubTask("SubTask1", "Descr", Status.IN_PROGRESS, epic);
        SubTask subTask2 = new SubTask("SubTask2", "Descr", Status.IN_PROGRESS, epic);

        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);
        taskManager.deleteAllSubTasks();

        Assertions.assertTrue(taskManager.getSubTasks().isEmpty());
    }

    @Test
    void addAndGetSubTask(){
        Epic epic = new Epic("epic", "newEPic", Status.IN_PROGRESS);
        SubTask subTask = new SubTask("SubTask1", "Descr", Status.IN_PROGRESS, epic);

        taskManager.addSubTask(subTask);

        Assertions.assertEquals(subTask, taskManager.getSubTask(0));
    }

    @Test
    void updateSubTask() {
        Epic epic = new Epic("epic", "newEPic", Status.IN_PROGRESS);
        SubTask subTask = new SubTask("SubTask1", "Descr", Status.IN_PROGRESS, epic);
        SubTask newSubTask = new SubTask("newSubTask1", "new Descr", Status.IN_PROGRESS, epic);

        taskManager.addSubTask(subTask);
        taskManager.updateSubTask(newSubTask);
        SubTask updSubt = taskManager.getSubTask(newSubTask.getId());

                Assertions.assertEquals(newSubTask,updSubt);
    }

    @Test
    void deleteSubTask() {
        Epic epic2 = new Epic("epic", "newEPic", Status.IN_PROGRESS);
        SubTask newSubTask = new SubTask("SubTask231", "Desc23", Status.IN_PROGRESS, epic2);

       // taskManager.addEpic(epic2);
        taskManager.addSubTask(newSubTask);
        taskManager.deleteSubTask(0);

        Assertions.assertEquals(0, taskManager.getSubTasks().size());
    }

    @Test
    void getAndDeleteAllEpics() {
        Epic epic = new Epic("epic", "newEPic", Status.IN_PROGRESS);
        Epic epic2 = new Epic("epic2", "newEPic2", Status.IN_PROGRESS);

        taskManager.addEpic(epic);
        taskManager.addEpic(epic2);
        taskManager.deleteAllEpics();

        Assertions.assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    void addAndGetEpic() {
        Epic epic = new Epic("epic", "newEPic", Status.IN_PROGRESS);

        taskManager.addEpic(epic);

        Assertions.assertEquals(epic, taskManager.getEpic(0));
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("epic", "Epic", Status.IN_PROGRESS);
        Epic newEpic = new Epic("newEpic", "Epic", Status.IN_PROGRESS);

        taskManager.addEpic(epic);
        taskManager.updateEpic(newEpic);
        Epic updatedEpic = taskManager.getEpic(epic.getId());

        Assertions.assertEquals(updatedEpic,newEpic);
    }

    @Test
    void deleteEpic() {
        Epic epic = new Epic("epic", "Epic", Status.IN_PROGRESS);

        taskManager.addEpic(epic);
        taskManager.deleteEpic(epic.getId());

        Assertions.assertTrue(taskManager.getEpics().isEmpty());
    }



}

