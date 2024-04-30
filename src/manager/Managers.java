package manager;

public class Managers {

    private Managers() {

    }

    public static FileBackedTaskManager getDefault(String fileName) {
        return new FileBackedTaskManager(fileName);
    }

    public static TaskManager getInMemoryTaskManager() {   // для тестов
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }



}
