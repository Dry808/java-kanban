import manager.Managers;
import manager.TaskManager;
import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;

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



        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", Status.DONE);
        SubTask subTask2 = new SubTask("Подзадача 2", "Подзадача эпика 2", Status.DONE, epic2);
        SubTask subTask3 = new SubTask("Подзадача 3", "Подзадача эпика 2", Status.IN_PROGRESS, epic2);
        taskManager.addEpic(epic2);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);

        //удаляем задачу и эпик
        taskManager.deleteTask(1);
        taskManager.deleteSubTask(6);
        taskManager.deleteEpic(2);

        taskManager.getTask(0);
        taskManager.getEpic(4);

        printAllTasks(taskManager);

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubTasks((Epic) epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}