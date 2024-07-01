package com.yandex.model;

public class Node {
    private Node prev;
    private Node next;
    private Task value;

    public Node(Task value) {
        this.value = value;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Task getValue() {
        return value;
    }
}
