package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static models.Type.TASK;

public class Task {
    protected String taskName;
    protected Type type;
    protected String description;
    protected int id;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime  startTime;


    public Task(String taskName, String description, Status status) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
        type = TASK;
    }

    public Task(String taskName, String description, Status status, Duration dur, LocalDateTime startTime) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
        type = TASK;
        this.duration = dur;
        this.startTime = startTime;
    }

    public Task(String taskName, String description, Status status, long duration, String startTime) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
        type = TASK;
        this.duration = Duration.ofMinutes(duration);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        this.startTime = LocalDateTime.parse(startTime, formatter);
    }

    @Override
    public String toString() {
        return "models.Task{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    //getter and setter
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Type getType() {
        return type;
    }

    // метод для получения времени окончания таски
    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }


}