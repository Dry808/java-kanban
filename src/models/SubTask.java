package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static models.Type.EPIC;
import static models.Type.SUBTASK;

public class SubTask extends Task {
    private int epicId;
    private Type type = SUBTASK;


    public SubTask(String taskName, String description, Status status, Epic epic) {
        super(taskName, description, status);
        type = SUBTASK;
        this.epicId = epic.getId();
    }

    public SubTask(String taskName, String description, Status status, Epic epic, String startTime, long duration) {
        super(taskName, description, status);
        type = SUBTASK;
        this.epicId = epic.getId();
        this.duration = Duration.ofMinutes(duration);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        this.startTime = LocalDateTime.parse(startTime, formatter);
    }

    public SubTask(String taskName, String description, Status status, Epic epic,
                   LocalDateTime startTime, Duration dur) {
        super(taskName, description, status);
        type = SUBTASK;
        this.epicId = epic.getId();
        this.duration = dur;
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "models.SubTask{" +
                "epic=" + epicId +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public int getEpic() {
        return epicId;
    }



    public void setEpic(Epic epic) {
        this.epicId = epic.getId();
    }
}
