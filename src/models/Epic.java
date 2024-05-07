package models;


import java.time.LocalDateTime;
import java.util.ArrayList;

import static models.Type.EPIC;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks;
    LocalDateTime endTime;


    public Epic(String taskName, String description, Status status) {
        super(taskName, description, status);
        type = EPIC;
        this.subTasks = new ArrayList<SubTask>();
    }

    @Override
    public String toString() {
        return "models.Epic{" +
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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

}
