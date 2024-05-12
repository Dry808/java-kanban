package models;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static models.Type.EPIC;

public class Epic extends Task {
    private List<Integer> subTasksId;
    private LocalDateTime endTime;


    public Epic(String taskName, String description, Status status) {
        super(taskName, description, status);
        type = EPIC;
        this.subTasksId = new ArrayList<Integer>();
    }

    @Override
    public String toString() {
        return "models.Epic{" +
                "subTasks=" + subTasksId.size() +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public List<Integer> getSubTasksId() {
        return subTasksId;
    }

    public void setSubTasks(List<Integer> subTasks) {
        this.subTasksId = subTasks;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

}
