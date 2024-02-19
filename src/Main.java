import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        //2 ЗАДАЧИ
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Задача 1(Task)", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задачи 2(Task)", "Описание задачи 2", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        //ЭПИК с 1 подзадачей;
        ArrayList<SubTask> epicSubTasks = new ArrayList<>();
        Epic epic = new Epic("ЭПИК 1", "Описаине эпика 1", Status.NEW, epicSubTasks);
        SubTask subTask = new SubTask("Подзадача 1", "ЭПИК 1", Status.NEW,
                epic);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        //ЭПИК с 2 подзадачами
        ArrayList<SubTask> epicSubTasks2 = new ArrayList<>();
        Epic epic2 = new Epic("ЭПИК 2", "Описание эпика 2", Status.NEW, epicSubTasks2);
        SubTask subTask2 = new SubTask("Подзадача 1", "ЭПИК 2", Status.NEW,
                epic2);
        SubTask subTask3 = new SubTask("Подзадача 2", "ЭПИК 2", Status.NEW,
                epic2);
        taskManager.addEpic(epic2);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);

        //меняем статус task1
        task1.setStatus(Status.DONE);
        taskManager.updateTask(task1);

        //Меняем статус подзадач
        subTask.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);




        subTask2.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask2);

        subTask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask3);

        subTask3.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask3);

        taskManager.deleteSubTask(6);


    }
}
