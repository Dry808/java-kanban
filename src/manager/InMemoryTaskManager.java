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
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(getTaskComparator());

    private int idSequence = 0;




    // Методы Task
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        getTasks().stream().filter(task -> task.getStartTime() != null).forEach(prioritizedTasks::remove);
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

        // проверяем на пересечения
        if (newTask.getStartTime() != null && getPrioritizedTasks().stream().anyMatch(task -> isIntersect(task, newTask))) {
            throw new IllegalArgumentException("Задача пересекается по времени с существующей");
        }
        tasks.put(newTask.getId(), newTask);
        addPrioritizedTasks(newTask);
        return newTask;

    }

    @Override
    public Task updateTask(Task updateTask) {
        Task oldTask = tasks.get(updateTask.getId());
        prioritizedTasks.remove(oldTask);

        if (updateTask.getStartTime() != null && getPrioritizedTasks().stream().anyMatch(task -> isIntersect(task, updateTask))) {
            throw new IllegalArgumentException("Задача пересекается по времени с существующей");
        }

        tasks.put(updateTask.getId(), updateTask);
        addPrioritizedTasks(updateTask);
        return updateTask;
    }

    @Override
    public Task deleteTask(int id) {
        historyManager.remove(id);
        prioritizedTasks.remove(tasks.get(id));
        return tasks.remove(id);

    }


    // Методы SubTasks
    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public Map<Integer, SubTask> deleteAllSubTasks() {
        // удаляем из списка приоритетов
        getSubTasks().stream().filter(subTask -> subTask.getStartTime() != null).forEach(prioritizedTasks::remove);

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

        // проверка на пересечения
        if (newSubTask.getStartTime() != null && getPrioritizedTasks().stream().anyMatch(task -> isIntersect(task, newSubTask))) {
            throw new IllegalArgumentException("Задача пересекается по времени с существующей");
        }
        subTasks.put(newSubTask.getId(), newSubTask);
        changeEpicStatus(newSubTask.getEpic());
        changeEpicDuration(newSubTask.getEpic());
        addPrioritizedTasks(newSubTask);
        return newSubTask;
    }

    @Override
    public void updateSubTask(SubTask updateSubTask) {
        SubTask oldSubTask = subTasks.get(updateSubTask.getId());
        prioritizedTasks.remove(oldSubTask);
        subTasks.put(updateSubTask.getId(), updateSubTask);
        changeEpicStatus(updateSubTask.getEpic());
        changeEpicDuration(updateSubTask.getEpic());
        addPrioritizedTasks(updateSubTask);

    }

    @Override
    public SubTask deleteSubTask(int id) {
        SubTask subTask = subTasks.remove(id);
        prioritizedTasks.remove(subTask);
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
            } else if (counterNew == epic.getSubTasks().size()) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    // метод для генерации ID
    private int generateId() {
        return idSequence++;
    }

    private void changeEpicDuration(Epic epic) {
        // Дата начала эпика - момент начала самого раннего сабтаска
        // Дата конца - дата начала самого последнего сабтаска + его длительность
        // длительность - длительность всех сабтасков

        if (epic.getSubTasks().isEmpty()) return; // проверка на наличия сабтасков у эпика

        List<SubTask> subTasksTimeStart = epic.getSubTasks().stream() // сортируем сабтаски эпика по времени начала
                .filter(subTask -> subTask.getStartTime() != null)
                .sorted(Comparator.comparing(SubTask::getStartTime))
                .toList();
        if (subTasksTimeStart.isEmpty()) return;

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

    // метод для добавления тасок в приоритетный список
    protected void addPrioritizedTasks(Task task) {
        if (task.getStartTime() != null && task.getDuration() != null) {
            prioritizedTasks.add(task);
        }
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private Comparator<Task> getTaskComparator() {
        return Comparator.comparing(Task::getStartTime, Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(Task::getDuration, Comparator.nullsFirst(Comparator.naturalOrder()));

    }

    // метод для проверки пересечений тасок по времени(true - пересекаются)
    private boolean isIntersect(Task task, Task task2) {
        return !task.getStartTime().isAfter(task2.getEndTime()) && !task2.getStartTime().isAfter(task.getEndTime());
    }
}


