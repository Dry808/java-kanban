package manager;


import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class InMemoryTaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();

    }


    @Test
    public void shouldSaveAllTypesOfTasksInTaskManager() {
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
    public void shouldFindTasksById() {
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
    public void tasksWithSetIdAndGeneratedIdNotConflictInManager() {

        Task task1 = new Task("Задача 1", "Описание", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS);

        task1.setId(1); // Установка явно указанного id

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // Проверка
        assertNotEquals(task1, task2);
    }

    @Test
    public void tasksFieldsShouldNotChangeAfterAddToManager() {
        Task task = new Task("Задача", "Описание", Status.NEW);

        taskManager.addTask(task);
        Task taskInManager = taskManager.getTask(task.getId());
        task.setTaskName("Новая задача");
        boolean fieldsChange;
        if (task.getTaskName().equals(taskInManager.getTaskName()) &&
                task.getDescription().equals(taskInManager.getDescription()) &&
                task.getStatus().equals(taskInManager.getStatus()) && task.getId() == taskInManager.getId()) {
            fieldsChange = false;
        }else {
            fieldsChange = true;
        }

        assertFalse(fieldsChange, "Поля объекта изменились при добавлении в taskManager");
    }

    @Test
    public void tasksAddedToHistorySavePreviousVersion() {
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
    public void shouldDeleteSubTasksIfDeleteHisEpic() {
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

}
