package com.yandex.service;

import com.yandex.model.Node;
import com.yandex.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        if (first == null) {
            return tasks;
        }
        Node current = first;

        while (current != null) {
            tasks.add(current.getValue());
            current = current.getNext();
        }
        return tasks;
    }

    private Node linkLast(Task task) {
        Node node = new Node(task);
        if (first != null) {
            last.setNext(node);
            node.setPrev(last);
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
            node.getNext().setPrev(null);
            first = node.getNext();
            node.setNext(null);
        } else if (node == last) {
            node.getPrev().setNext(null);
            last = node.getPrev();
            node.setPrev(null);
        } else {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }
    }
}
