package manager;

public class Managers {

    private Managers() {

    }

    public static TaskManager getDefault(String fileName) {
        return new FileBackedTaskManager(fileName);
    }

    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }



}
