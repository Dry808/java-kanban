package manager;


import models.Node;
import models.Status;
import models.Task;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryHistoryManagerTest {
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
    public void historyManagerDontSaveDuplicate() {

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task);

        Assertions.assertEquals(2, historyManager.getHistory().size());
        Assertions.assertTrue(historyManager.getHistory().contains(task));
        Assertions.assertTrue(historyManager.getHistory().contains(task2));

    }


    @Test
    public void removeFromHistory() {

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.remove(2);

        Assertions.assertEquals(historyManager.getHistory().get(0), task);
    }
}

