package manager;


import exceptions.ManagerSaveException;
import models.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private String file;
    private Path path;
    private int maxId = 0;  // максимальное значение id среди загруженных тасок из файла

    public FileBackedTaskManager(String file) {
        this.file = file;
        this.path = Paths.get(file);
    }


    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Task addTask(Task newTask) {
        super.addTask(newTask);
        save();
        return newTask;
    }

    @Override
    public Task updateTask(Task updateTask) {
        Task task = super.updateTask(updateTask);
        save();
        return task;
    }

    @Override
    public Task deleteTask(int id) {
        Task task = super.deleteTask(id);
        save();
        return task;
    }

    @Override
    public Map<Integer, SubTask> deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
        return subTasks;
    }

    @Override
    public SubTask addSubTask(SubTask newSubTask) {
        SubTask sbt = super.addSubTask(newSubTask);
        save();
        return sbt;
    }

    @Override
    public void updateSubTask(SubTask updateSubTask) {
        super.updateSubTask(updateSubTask);
        save();
    }

    @Override
    public SubTask deleteSubTask(int id) {
        SubTask sbt = super.deleteSubTask(id);
        save();
        return sbt;

    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Epic addEpic(Epic newEpic) {
        super.addEpic(newEpic);
        save();
        return newEpic;
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        super.updateEpic(updateEpic);
        save();
    }

    @Override
    public Epic deleteEpic(int id) {
        Epic epic = super.deleteEpic(id);
        save();
        return epic;
    }



    // загрузка тасок и истории из файла
    public static FileBackedTaskManager loadFromFile(String file) {
        FileBackedTaskManager backedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            while (br.ready()) {
                line = br.readLine();
                if (line.isEmpty()) {    // в файле история сохранена после пустой строки
                    line = br.readLine();
                    backedTaskManager.historyManager = backedTaskManager.historyFromString(line);
                    break;
                }
                if (backedTaskManager.taskFromString(line).getClass().equals(Task.class)) {
                    Task task = backedTaskManager.taskFromString(line);
                    backedTaskManager.tasks.put(task.getId(),task);
                    backedTaskManager.addPrioritizedTasks(task);

                } else if (backedTaskManager.taskFromString(line).getClass().equals(Epic.class)) {
                    Epic epic = (Epic) backedTaskManager.taskFromString(line);
                    backedTaskManager.epics.put(epic.getId(),epic);

                } else if (backedTaskManager.taskFromString(line).getClass().equals(SubTask.class)) {
                    SubTask subTask = (SubTask) backedTaskManager.taskFromString(line);
                    backedTaskManager.subTasks.put(subTask.getId(),subTask);
                    backedTaskManager.addPrioritizedTasks(subTask);
                }
            }
        } catch (Exception e) {
            System.out.println("Произошла ошибка чтения файла");
            e.printStackTrace();
        }
        return backedTaskManager;
    }

    protected void save()  {
        String header = "id,type,name,status,description,epic, startTime, duration";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            if (Files.size(path) == 0) {
                bw.write(header);
                bw.newLine();
            }
            for (Task tsk : getTasks()) {
                String str = taskToString(tsk);
                bw.write(str);
                bw.newLine();
            }
            for (Epic epc : getEpics()) {
                String str = taskToString(epc);
                bw.write(str);
                bw.newLine();
            }
            for (SubTask sbt : getSubTasks()) {
                String str = taskToString(sbt);
                bw.write(str);
                bw.newLine();
            }
            if (!historyManager.getHistory().isEmpty()) {
                bw.newLine();
                bw.write(historyToString());
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    // методы для помощи в сохранении и загрузки тасок из файла
    private String taskToString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getType()).append(",");
        sb.append(task.getTaskName()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        if (task.getType().equals(Type.SUBTASK)) {
            sb.append(((SubTask) task).getEpic().getId()).append(",");
        } else {
            sb.append(",");
        }
        if (task.getStartTime() != null && task.getDuration() != null) {
            sb.append(task.getStartTime()).append(",");
            sb.append(task.getDuration().toMinutes()).append(",");
        } else {
            sb.append(",");
            sb.append(",");
        }
        return sb.toString();
    }

    private Task taskFromString(String value) {
        String[] str = value.split(",");
        if (Integer.parseInt(str[0]) > maxId) {   // настраиваем генератор id
            maxId = Integer.parseInt(str[0]) + 1;
            idSettings(maxId);
        }
        if (str[1].equals("TASK")) {
            Task task = new Task(str[2], str[4], Status.valueOf(str[3]));
            task.setId(Integer.parseInt(str[0]));
            if (str.length > 5) {   // если больше 5 то значит есть startTime & Duration
                task.setStartTime(LocalDateTime.parse(str[6]));
                task.setDuration(Duration.parse("PT" + str[7] + "M"));
            }
            return task;
        } else if (str[1].equals("EPIC")) {
            Task epic = new Epic(str[2], str[4], Status.valueOf(str[3]));
            epic.setId(Integer.parseInt(str[0]));
            if (str.length > 5) {
                epic.setStartTime(LocalDateTime.parse(str[6]));
                epic.setDuration(Duration.parse("PT" + str[7] + "M"));
            }
            return epic;
        } else if (str[1].equals("SUBTASK")) {
            Task subTask = new SubTask(str[2], str[4], Status.valueOf(str[3]), getEpic(Integer.parseInt(str[5])));
            subTask.setId(Integer.parseInt((str[0])));
            if (str.length > 5) {
                subTask.setStartTime(LocalDateTime.parse(str[6]));
                subTask.setDuration(Duration.parse("PT" + str[7] + "M"));
            }
            return subTask;
        }
        throw new IllegalArgumentException("Не удалось создать задачу");
    }

    // методы для помощи в сохранении и загрузки истории из файла
    private String historyToString() {
        StringBuilder sb = new StringBuilder();
        for (Task task : getHistory()) {
            sb.append(task.getId()).append(" ");
        }
        return sb.toString();
    }

    private HistoryManager historyFromString(String value) {
        HistoryManager hsm = Managers.getDefaultHistory();
        String[] str = value.split(" ");
        for (String s : str) {
            hsm.add(getTask(Integer.parseInt(s)));
        }
        return hsm;
    }

    // метод для актуализации счётчика id после загрузки тасок из файла
    private void idSettings(int id) {
            setIdSequence(id);
    }


}


