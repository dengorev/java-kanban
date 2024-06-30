package com.yandex.service;

import com.yandex.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        if (first == null) {
            return  tasks;
        }
        Node current = first;

        while (current != null) {
            tasks.add(current.value);
            current = current.next;
        }
        return tasks;
    }

    private Node linkLast(Task task) {
        Node node = new Node(task);
        if (first != null) {
            last.next = node;
            node.prev = last;
        } else {
            first = node;
        }
        last = node;
        return node;
    }

    @Override
    public void add(Task task) {
        Node node = linkLast(task);
        if (nodeMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        nodeMap.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.get(id);
        removeNode(node);
        nodeMap.remove(id);
    }

    private void removeNode(Node node) {
        if (!nodeMap.containsValue(node)) {
            return;
        }
        if (node == first) {
            node.next.prev = null;
            first = node.next;
            node.next = null;
        } else if (node == last) {
            node.prev.next = null;
            last = node.prev;
            node.prev = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    private static class Node {
        Node prev;
        Node next;
        Task value;

        public Node(Task value) {
            this.value = value;
        }
    }
}
