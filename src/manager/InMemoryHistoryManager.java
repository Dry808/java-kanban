package manager;

import models.Node;
import models.Task;

import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private int size = 0;

    private final Map<Integer, Node> history = new HashMap<>();


    @Override
    public void add(Task task) {
        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));

        }

        history.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        removeNode(history.remove(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public Node linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        size++;
        return newNode;
    }

    public List<Task> getTasks() {
        List<Task> historyList = new ArrayList<>();
        Node currentNode = head;

        while (currentNode != null) {
            historyList.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return historyList;
    }

    public void removeNode(Node node) {
        if (node == null) {
            return;
        }
        Node previous = node.prev;
        Node next = node.next;

        if (previous != null) {
            previous.next = next;
        } else {
            head = next;
        }
        if (next != null) {
            next.prev = previous;
        } else {
            tail = previous;
        }

    }


}



