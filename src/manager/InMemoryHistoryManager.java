package manager;

import models.Task;

import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {
    public Node<Task> head;
    public Node<Task> tail;
    private int size = 0;

   // private List<Task> history = new ArrayList<>();
    private Map<Integer, Node> history = new HashMap<>();



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

    public Node<Task> linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null){
            head = newNode;
        } else{
            oldTail.next = newNode;
        }
        size++;
        return newNode;
    }

    public List<Task> getTasks(){
        List<Task> historyList = new ArrayList<>();
        Node<Task> currentNode = head;

        while (currentNode != null){
            historyList.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return historyList;
    }

    public void removeNode(Node node){
        if (node == null) {
            return;
        }
        Node last = node.prev;
        Node future = node.next;

        if (last != null) {
            last.next = future;
        } else {
            head = future;
        }
        if (future != null) {
            future.prev = last;
        } else {
            tail = last;
        }

    }


}

class Node<Task> {
    public Task data;
    public Node<Task> next;
    public Node<Task> prev;


    public Node(Node<Task> prev, Task data, Node<Task> next ) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(data, node.data) && Objects.equals(next, node.next) && Objects.equals(prev, node.prev);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, next, prev);
    }
}


