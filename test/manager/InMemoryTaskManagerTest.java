package manager;


import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    TaskManager taskManager;


    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }


    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getInMemoryTaskManager();

    }


    @Test
    void shouldSaveAllTypesOfTasksInTaskManager() {
        Task task = new Task("Задача", "Описание", Status.NEW);
        Epic epic = new Epic("Эпик", "Описание эпика", Status.NEW);
        SubTask subTask = new SubTask("Подзадача", "Описание", Status.NEW, epic);

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        assertTrue(taskManager.getTasks().contains(task), "Не добавился task");
        assertTrue(taskManager.getEpics().contains(epic), "Не добавился epic");
        assertTrue(taskManager.getSubTasks().contains(subTask), "Не добавился SubTask");
    }

    @Test
    void shouldFindTasksById() {
        Task task = new Task("Задача", "Описание", Status.NEW);
        Epic epic = new Epic("Эпик", "Описание эпика", Status.NEW);
        SubTask subTask = new SubTask("Подзадача", "Описание", Status.NEW, epic);
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);

        Task taskInManager = taskManager.getTask(task.getId());
        SubTask subTaskInManager = taskManager.getSubTask((subTask.getId()));
        Epic epicInManager = taskManager.getEpic(epic.getId());

        assertEquals(task, taskInManager, "Не получилось найти task по ID");
        assertEquals(subTask, subTaskInManager, "Не получилось найти subTask по ID");
        assertEquals(epic, epicInManager, "Не получилось найти epic по ID");

    }

    @Test
    void tasksWithSetIdAndGeneratedIdNotConflictInManager() {

        Task task1 = new Task("Задача 1", "Описание", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS);

        task1.setId(1); // Установка явно указанного id

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // Проверка
        assertNotEquals(task1, task2);
    }

    @Test
    void tasksFieldsShouldNotChangeAfterAddToManager() {
        Task task = new Task("Задача", "Описание", Status.NEW);

        taskManager.addTask(task);
        Task taskInManager = taskManager.getTask(task.getId());
        task.setTaskName("Новая задача");
        boolean fieldsChange;
        if (task.getTaskName().equals(taskInManager.getTaskName()) &&
                task.getDescription().equals(taskInManager.getDescription()) &&
                task.getStatus().equals(taskInManager.getStatus()) && task.getId() == taskInManager.getId()) {
            fieldsChange = false;
        } else {
            fieldsChange = true;
        }

        assertFalse(fieldsChange, "Поля объекта изменились при добавлении в taskManager");
    }

    @Test
    void tasksAddedToHistorySavePreviousVersion() {
        Task task = new Task("Задача 1", "Описание 1", Status.NEW); // id 0
        taskManager.addTask(task);
        taskManager.getTask(0);
        Task taskInHistory = taskManager.getHistory().get(0);

        Task taskNewVersion = new Task("Новая задача 1", "Новое описание", Status.DONE);
        taskNewVersion.setId(0);
        taskManager.updateTask(taskNewVersion);
        taskManager.getTask(taskNewVersion.getId());

        assertEquals(task, taskInHistory, "В истории не сохраняется предыдущий вариант задачи");

    }

    @Test
    void shouldDeleteSubTasksIfDeleteHisEpic() {
        Epic epic = new Epic("Эпик", "Описание эпика", Status.NEW);
        SubTask subTask = new SubTask("Сабтаск", "Сабтаск эпика", Status.NEW, epic);
        SubTask subTask2 = new SubTask("Сабтаск 2", "Второй сабтаск эпика ", Status.NEW, epic);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);

        taskManager.deleteEpic(0);

        assertFalse(taskManager.getSubTasks().contains(subTask));
        assertFalse(taskManager.getSubTasks().contains(subTask2));
    }

    // ТЕСТЫ ТЗ-8

    @Test
    void epicStatusCalculation_new() {
        //все подзадачи со статусом NEW
        Epic epic = new Epic("epic", "Epic", Status.IN_PROGRESS);
        SubTask subTask = new SubTask("SubTask1", "Descr", Status.NEW, epic);
        SubTask subTask2 = new SubTask("SubTask2", "Descr", Status.NEW, epic);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);

        assertEquals(Status.NEW, taskManager.getEpic(0).getStatus(), "Статус не New");
    }

    @Test
    void epicStatusCalculation_done() {
        //все подзадачи со статусом done
        Epic epic = new Epic("epic", "Epic", Status.IN_PROGRESS);
        SubTask subTask = new SubTask("SubTask1", "Descr", Status.DONE, epic);
        SubTask subTask2 = new SubTask("SubTask2", "Descr", Status.DONE, epic);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);

        assertEquals(Status.DONE, taskManager.getEpic(0).getStatus(), "Статус не Done");
    }

    @Test
    void epicStatusCalculation_newAndDone() {
        //все подзадачи со статусом new & done
        Epic epic = new Epic("epic", "Epic", Status.DONE);
        SubTask subTask = new SubTask("SubTask1", "Descr", Status.NEW, epic);
        SubTask subTask2 = new SubTask("SubTask2", "Descr", Status.DONE, epic);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(0).getStatus(), "Статус не IN PROGRESS");
    }

    @Test
    void epicStatusCalculation_inProgress() {
        //все подзадачи со статусом IN PROGRESS
        Epic epic = new Epic("epic", "Epic", Status.NEW);
        SubTask subTask = new SubTask("SubTask1", "Descr", Status.IN_PROGRESS, epic);
        SubTask subTask2 = new SubTask("SubTask2", "Descr", Status.IN_PROGRESS, epic);

        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(0).getStatus(), "Статус не IN PROGRESS");
    }

    @Test
    void correctIntersectionCalculation() {
        // не пересекаются
        Task task = new Task("Задача 1", "Описание 1", Status.NEW,
                "01.01.2024 12:00", 20);

        Task task2 = new Task("Задача 1", "task 1", Status.IN_PROGRESS,
                "02.01.2024 12:00", 10);

        taskManager.addTask(task);
        taskManager.addTask(task2);

        assertFalse(taskManager.getTasks().isEmpty());

        //пересекаются
        Task task3 = new Task("Задача 1", "Описание 1", Status.NEW,
                "12.12.2023 12:12", 20);

        Task task4 = new Task("Задача 1", "task 1", Status.IN_PROGRESS,
                "12.12.2023 12:10", 10);

        taskManager.addTask(task3);

        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task4));


    }

}
