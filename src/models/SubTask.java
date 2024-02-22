package models;

public class SubTask extends Task {
    private Epic epic;


    public SubTask(String taskName, String description, Status status, Epic epic) {
        super(taskName, description, status);
        this.epic = epic;
        if (epic != null) {
            epic.getSubTasks().add(this);
        }
    }

    @Override
    public String toString() {
        return "models.SubTask{" +
                "epic=" + epic +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }
}
