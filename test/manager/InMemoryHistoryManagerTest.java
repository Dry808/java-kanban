package manager;


import models.Status;
import models.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    Task task;
    Task task2;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS);
        task.setId(0);
        task.setId(1);
    }

    @Test
    void historyManagerDontSaveDuplicate() {

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task);

        Assertions.assertEquals(2, historyManager.getHistory().size());
        Assertions.assertTrue(historyManager.getHistory().contains(task));
        Assertions.assertTrue(historyManager.getHistory().contains(task2));

    }


    @Test
    void removeFromHistory() {
        Task taskNew = new Task("Задача ", "Описание задачи ", Status.NEW);
        taskNew.setId(2);
        Task taskNew2 = new Task("Задача ", "Описание задачи ", Status.NEW);
        taskNew.setId(3);

        historyManager.add(task);
        historyManager.add(taskNew);
        historyManager.add(task2);
        historyManager.add(taskNew2);

        historyManager.remove(task.getId());
        historyManager.remove(task2.getId());
        historyManager.remove(taskNew2.getId());

        Assertions.assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void addToHistory() {
        historyManager.add(task);
        Assertions.assertFalse(historyManager.getHistory().isEmpty());
    }
}

