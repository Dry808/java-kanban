package manager;

import models.Epic;
import models.Status;
import models.Task;
import org.junit.jupiter.api.*;


import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


class FileBackedTaskManagerTest {
    FileBackedTaskManager fileBackedTaskManager;


    @BeforeEach
    public void beforeEach() {
        fileBackedTaskManager = Managers.getDefault("testFile.csv");
    }

    @BeforeEach
    public void clearFile() {
        try (FileWriter writer = new FileWriter("testFile.csv")) {

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterAll
    static void clearFileAfterAllTests() {
        try (FileWriter writer = new FileWriter("testFile.csv")) {

        }  catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldLoadEmptyFile() {
        FileBackedTaskManager fbtm = FileBackedTaskManager.loadFromFile("testFile.csv");

        Assertions.assertEquals(0, fbtm.getTasks().size());
        Assertions.assertEquals(0, fbtm.getEpics().size());
        Assertions.assertEquals(0, fbtm.getSubTasks().size());
    }

    @Test
    void shouldSaveEmptyFile() throws IOException {
        boolean fileIsEmpty;
        if (Files.size(Paths.get("testFile.csv")) == 0) {
            fileIsEmpty = true;
            fileBackedTaskManager.save();
        } else {
            fileIsEmpty = false;
        }

        Assertions.assertTrue(fileIsEmpty);
    }

    @Test
    void shouldSaveTasksAndLoad() {
        Task task = new Task("Задача", "Описание", Status.NEW);
        Epic epic = new Epic("Эпик", "Описание", Status.NEW);
        fileBackedTaskManager.addTask(task);
        fileBackedTaskManager.addEpic(epic);

        FileBackedTaskManager fbm = FileBackedTaskManager.loadFromFile("testFile.csv");

        Assertions.assertEquals(fileBackedTaskManager.getTasks(), fbm.getTasks());
        Assertions.assertEquals(fileBackedTaskManager.getEpics(), fbm.getEpics());

    }
}


