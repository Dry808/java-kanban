package models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;


public class SubTaskTest {

    @Test
    public void subTaskEqualityById() {
        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);

        SubTask subTask = new SubTask("Подзадача 1", "Описание подзадачи", Status.NEW, epic);
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", Status.NEW, epic);

        // Задаём одинаковые id
        subTask.setId(1);
        subTask2.setId(1);

        assertEquals(subTask, subTask2, "Задачи не равны друг другу");
    }
}
