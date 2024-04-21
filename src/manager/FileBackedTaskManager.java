package manager;


public class FileBackedTaskManager extends InMemoryTaskManager {
    String file;

    FileBackedTaskManager(String file){
        this.file = file;
    }

    public void save() {

    }

    // Метод сохранения задачи в строку
    //public String toString(Task task) {}



}
