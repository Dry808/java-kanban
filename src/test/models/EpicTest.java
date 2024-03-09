package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;



public class EpicTest {

    @Test
    public void epicEqualityById() {

        Epic epic = new Epic("Эпик", "Описание эпика", Status.NEW);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", Status.IN_PROGRESS);


        // Установка одинаковых id
        epic.setId(1);
        epic2.setId(1);


        assertEquals(epic, epic2, "Задачи не равны друг другу");
    }
}