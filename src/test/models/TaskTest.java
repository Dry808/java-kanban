package models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import models.Task;
import models.Status;


public class TaskTest {

    @Test
    public void taskEqualityById() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);

        // Установка одинаковых id
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи не равны друг другу");
    }
}
