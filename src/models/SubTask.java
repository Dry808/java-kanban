package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static models.Type.SUBTASK;

public class SubTask extends Task {
    private Epic epic;


    public SubTask(String taskName, String description, Status status, Epic epic) {
        super(taskName, description, status);
        type = SUBTASK;
        this.epic = epic;
        if (epic != null) {
            epic.getSubTasksId().add(this.getId());
        }
    }

    public SubTask(String taskName, String description, Status status, Epic epic, String startTime, long duration) {
        super(taskName, description, status);
        type = SUBTASK;
        this.epic = epic;
        if (epic != null) {
            epic.getSubTasksId().add(this.getId());
        }
        this.duration = Duration.ofMinutes(duration);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        this.startTime = LocalDateTime.parse(startTime, formatter);
    }

    public SubTask(String taskName, String description, Status status, Epic epic,
                   LocalDateTime startTime, Duration dur) {
        super(taskName, description, status);
        type = SUBTASK;
        this.epic = epic;
        if (epic != null) {
            epic.getSubTasksId().add(this.getId());
        }
        this.duration = dur;
        this.startTime = startTime;
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
