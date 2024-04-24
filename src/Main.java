import manager.FileBackedTaskManager;
import manager.TaskManager;
import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;


public class Main {

    public static void main(String[] args) {
       // TaskManager taskManager = Managers.getDefault();
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile("file.csv");




                // Создаём 2 задачи, эпик с 3 подзадачами и эпик без подзадач
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS);
        taskManager.addTask(task);
        taskManager.addTask(task2);


        Epic epic = new Epic("Эпик 1", "Описание эпика 1", Status.NEW);
        SubTask subTask = new SubTask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic);
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", Status.NEW, epic);
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", Status.NEW, epic);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);


        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", Status.NEW);
        taskManager.addEpic(epic2);


        String str = taskManager.taskToString(epic2);
        Epic episs = (Epic) taskManager.taskFromString(str);
        // Запрашиваем задачи
        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getTask(0);
        taskManager.getEpic(6);
        taskManager.getTask(1);
        taskManager.getSubTask(4);
        taskManager.getTask(1);



        taskManager.getHistory();
        taskManager.deleteEpic(2);
        taskManager.getHistory();









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