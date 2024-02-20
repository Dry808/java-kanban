import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<SubTask> subTasks;


    Epic(String taskName, String description, Status status) {
        super(taskName, description, status);
        this.subTasks = new ArrayList<SubTask>();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks.size() +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
    }
}
