package manager;

import models.Epic;
import models.Status;
import models.SubTask;
import models.Task;

import java.time.Duration;
import java.util.*;

public class InMemoryTaskManager implements TaskManager  {
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, SubTask> subTasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    private int idSequence = 0;


    // Методы Task
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);

    }


    @Override
    public Task addTask(Task newTask) {
        newTask.setId(generateId());
        tasks.put(newTask.getId(), newTask);
        return newTask;

    }

    @Override
    public Task updateTask(Task updateTask) {
        tasks.put(updateTask.getId(), updateTask);
        return updateTask;
    }

    @Override
    public Task deleteTask(int id) {
        historyManager.remove(id);
        return tasks.remove(id);

    }


    // Методы SubTasks
    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public Map<Integer, SubTask> deleteAllSubTasks() {
        for (Epic epic : epics.values()) {
            getEpicSubTasks(epic).clear();
            changeEpicStatus(epic);
            changeEpicDuration(epic);
        }
        subTasks.clear();
        return subTasks;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public SubTask addSubTask(SubTask newSubTask) {
        newSubTask.setId(generateId());
        subTasks.put(newSubTask.getId(), newSubTask);
        changeEpicStatus(newSubTask.getEpic());
        changeEpicDuration(newSubTask.getEpic());
        return newSubTask;
    }

    @Override
    public void updateSubTask(SubTask updateSubTask) {
        subTasks.put(updateSubTask.getId(), updateSubTask);
        changeEpicStatus(updateSubTask.getEpic());
        changeEpicDuration(updateSubTask.getEpic());
    }

    @Override
    public SubTask deleteSubTask(int id) {
        SubTask subTask = subTasks.remove(id);
        subTask.getEpic().getSubTasks().remove(subTask);
        changeEpicStatus(subTask.getEpic());
        changeEpicDuration(subTask.getEpic());
        historyManager.remove(id);
        return subTask;
    }


    //Методы Epic
    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
    }


    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Epic addEpic(Epic newEpic) {
        newEpic.setId(generateId());
        changeEpicStatus(newEpic);
        changeEpicDuration(newEpic);
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }


    @Override
    public void updateEpic(Epic updateEpic) {
        epics.put(updateEpic.getId(), updateEpic);
    }


    @Override
    public Epic deleteEpic(int id) {
        Epic epic = epics.get(id);
        List<SubTask> subTasksToDelete = new ArrayList<>();

        //удаляем сабтаски удалённого эпика
        subTasksToDelete.addAll(epic.getSubTasks());
        for (SubTask sb : subTasksToDelete) {
            historyManager.remove(sb.getId());
            subTasks.remove(sb.getId());


        }

        epics.remove(epic.getId());
        historyManager.remove(id);
        return epic;
    }

    @Override
    public List<SubTask> getEpicSubTasks(Epic epic) {
        return epic.getSubTasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void setIdSequence(int id) {
        idSequence = id;
    }

    private void changeEpicStatus(Epic epic) {
        int counterDone = 0; // счётчик для подзачад со статусом Done
        int counterNew = 0; // счётчик для подзадач со статусом New
        if (epic.getSubTasks() == null) {
            epic.setStatus(Status.NEW);
        } else {
            for (SubTask sub : epic.getSubTasks()) {
                if (sub.getStatus() == Status.DONE) {
                    counterDone++;
                } else if (sub.getStatus() == Status.NEW) {
                    counterNew++;
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
            if (counterDone == epic.getSubTasks().size()) {
                epic.setStatus(Status.DONE);
            }
            if (counterNew == epic.getSubTasks().size()) {
                epic.setStatus(Status.NEW);
            }
        }
    }

    // метод для генерации ID
    private int generateId() {
        return idSequence++;
    }

    private void changeEpicDuration(Epic epic) {
        // Дата начала эпика - момент начала самой раннего сабтаска
        // Дата конца - дата начала самого последнего сабтаска + его длительность
        // длительность - длительность всех сабтасков

        if (epic.getSubTasks().isEmpty()) return; // проверка на наличия сабтасков у эпика

        List<SubTask> subTasksTimeStart = epic.getSubTasks().stream() // сортируем сабтаски эпика по времени начало
                .filter(subTask -> subTask.getStartTime() != null)
                .sorted(Comparator.comparing(SubTask::getStartTime))
                .toList();

        Duration allSubTasksDuration = epic.getSubTasks().stream() // считаем общую продолжительность сабтасков
                .filter(subTask -> subTask.getDuration() != null)
                .map(SubTask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        // дата начала эпика
        epic.setStartTime(subTasksTimeStart.getFirst().getStartTime());

        // дата конца эпика
        epic.setEndTime(subTasksTimeStart.getLast().getStartTime().plus(subTasksTimeStart.getLast().getDuration()));

        // продолжительность эпика
        epic.setDuration(allSubTasksDuration);
    }



    public static void main(String[] args) {
            FileBackedTaskManager taskManager = new FileBackedTaskManager("file.csv");

            Epic epic = new Epic("NameEpic", "Opisanie", Status.NEW);
            Epic epic2 = new Epic("NameEpic", "Opisanie", Status.NEW);
            SubTask subTask = new SubTask("Name", "Descr", Status.NEW, epic, "05.05.2024 11:00", 20);
            SubTask subTask2 = new SubTask("Name2", "Descr2", Status.NEW, epic, "03.05.2024 12:00", 34);
            SubTask subTask3 = new SubTask("Name3", "Descr3", Status.IN_PROGRESS, epic, "23.02.2023 21:33", 10);
           // Task task = new Task("Задача 1", "task 1", Status.IN_PROGRESS, "21.02.2024 21:33", 70);
       Task task = new Task("Задача 1", "task 1", Status.IN_PROGRESS);

            taskManager.addEpic(epic);
            taskManager.addSubTask(subTask);
            taskManager.addSubTask(subTask2);
            taskManager.addSubTask(subTask3);
            taskManager.addTask(task);
            taskManager.addEpic(epic2);

         FileBackedTaskManager taskManager1 = FileBackedTaskManager.loadFromFile("file.csv");
    }
}


