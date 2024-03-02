import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();




        // Создаём 2 задачи, эпик с одной подзадачей и эпик с 2 подзадачами
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описаине задачи 2", Status.NEW);
        taskManager.addTask(task);
        taskManager.addTask(task2);

        Epic epic = new Epic("Эпик 1", "Описание эпика", Status.NEW);
        SubTask subTask = new SubTask("Подзадача 1", "Подзадача эпика 1", Status.NEW, epic);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);


        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", Status.NEW);
        SubTask subTask2 = new SubTask("Подзадача 2", "Подзадача эпика 2", Status.NEW, epic2);
        SubTask subTask3 = new SubTask("Подзадача 3", "Подзадача эпика 2", Status.NEW, epic2);
        taskManager.addEpic(epic2);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);


        // печатаем списки задач, эпиков, подзадач
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getSubTasks());
        System.out.println(taskManager.getEpics());

        System.out.println(taskManager.getEpicSubTasks(epic2));
        taskManager.getTask(task.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task.getId());
        taskManager.getTask(task.getId());
        taskManager.getTask(task.getId());
        taskManager.getTask(task.getId());
        taskManager.getTask(task.getId());
        taskManager.getTask(task.getId());
        taskManager.getTask(task.getId());
        taskManager.getTask(task.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task2.getId());
        System.out.println(taskManager.getHistory());

        //Меняем статусы
        task.setStatus(Status.DONE);
        taskManager.updateTask(task);

        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask2);
        taskManager.updateSubTask(subTask3);

        //удаляем задачу и эпик
        taskManager.deleteTask(1);
        taskManager.deleteSubTask(5);
        taskManager.deleteEpic(2);

    }
}
