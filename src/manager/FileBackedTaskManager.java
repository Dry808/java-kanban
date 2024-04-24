package manager;


import models.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private String file;
    private Path path;

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

    public void save()  {
        String header = "id,type,name,status,description,epic";
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
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    public String taskToString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getType()).append(",");
        sb.append(task.getTaskName()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        if (task.getType().equals(Type.SUBTASK)) {
            sb.append(((SubTask) task).getEpic().getId());
        }
        return sb.toString();
    }

    public Task taskFromString(String value) {
        String[] str = value.split(",");
        if (str[1].equals("TASK")) {
            Task task = new Task(str[2], str[4], Status.valueOf(str[3]));
            task.setId(Integer.parseInt(str[0]));
            return task;
        } else if (str[1].equals("EPIC")) {
            Epic epic = new Epic(str[2], str[4], Status.valueOf(str[3]));
            epic.setId(Integer.parseInt(str[0]));
            return epic;
        } else if (str[1].equals("SUBTASK")) {
            SubTask subTask = new SubTask(str[2], str[4], Status.valueOf(str[3]), getEpic(Integer.parseInt(str[5])));
            subTask.setId(Integer.parseInt((str[0])));
            return subTask;
        }
        return null;
    }

    public static FileBackedTaskManager loadFromFile(String file) {
        FileBackedTaskManager backedTaskManager = new FileBackedTaskManager(file.toString());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            while (br.ready()) {
                line = br.readLine();
                if (backedTaskManager.taskFromString(line).getClass().equals(Task.class)) {
                    Task task = backedTaskManager.taskFromString(line);
                    backedTaskManager.tasks.put(task.getId(),task);
                } else if (backedTaskManager.taskFromString(line).getClass().equals(Epic.class)) {
                    Epic epic = (Epic) backedTaskManager.taskFromString(line);
                    backedTaskManager.epics.put(epic.getId(),epic);
                } else {
                    SubTask subTask = (SubTask) backedTaskManager.taskFromString(line);
                    backedTaskManager.subTasks.put(subTask.getId(),subTask);
                }

            }
        } catch (Throwable t) {
            System.out.println("Произошла ошибка чтения файла");
        }
        return backedTaskManager;
    }


    public static void main(String[] args) {
        Task task = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", Status.NEW);
        SubTask subTask = new SubTask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic);

        FileBackedTaskManager fbm = new FileBackedTaskManager("file.csv");
        fbm.addTask(task);
        fbm.addTask(task2);
        fbm.addEpic(epic);
        fbm.addSubTask(subTask);


        FileBackedTaskManager fbm2 = FileBackedTaskManager.loadFromFile("file.csv");
    }
}


