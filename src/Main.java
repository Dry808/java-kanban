
import manager.FileBackedTaskManager;

import models.*;


public class Main {

    public static void main(String[] args) {
            FileBackedTaskManager taskManager = new FileBackedTaskManager("file.csv");

            Epic epic = new Epic("NameEpic", "Opisanie", Status.NEW);
            Epic epic2 = new Epic("NameEpic", "Opisanie", Status.NEW);
            SubTask subTask = new SubTask("Name", "Descr", Status.NEW, epic, "05.05.2024 11:00", 20);
            SubTask subTask2 = new SubTask("Name2", "Descr2", Status.NEW, epic, "03.05.2024 12:00", 34);
            SubTask subTask3 = new SubTask("Name3", "Descr3", Status.IN_PROGRESS, epic, "23.02.2023 21:33", 10);
            //Task task = new Task("Задача 1", "task 1", Status.IN_PROGRESS, "21.02.2024 21:33", 70);
            Task task = new Task("Задача 1", "task 1", Status.IN_PROGRESS);
            Task task2 = new Task("Задача 1", "task 1", Status.IN_PROGRESS, "24.02.2023 21:40", 10);

            taskManager.addEpic(epic);
            taskManager.addSubTask(subTask);
            taskManager.addSubTask(subTask2);
            taskManager.addSubTask(subTask3);
            taskManager.addTask(task);
           // taskManager.deleteSubTask(1);
            taskManager.addTask(task2);
            //taskManager.deleteTask(4);
            taskManager.getSubTask(subTask.getId());
            taskManager.getTask(task.getId());
            taskManager.addEpic(epic2);



            //  FileBackedTaskManager taskManager1 = FileBackedTaskManager.loadFromFile("file.csv");
    }
}