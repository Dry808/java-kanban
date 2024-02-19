import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<SubTask> subTasks;


    Epic(String taskName, String description, Status status, ArrayList<SubTask> subTasks) {
        super(taskName, description, status);
        this.subTasks = subTasks;
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
    }
}
