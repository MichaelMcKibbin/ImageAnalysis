package com.michaelmckibbin.imageanalysis;

// my version of a linked list

import java.io.Serializable;
import java.util.Iterator;

public class LinkedList<N> implements Iterable<N>, Serializable {

    private Node<N> head;
    private Node<N> tail;

    public void clear() {
        head = null;
        tail = null;
        System.out.println("List has been cleared");
    }


    // inner class
    private static class Node<N> implements Serializable {
        N data;
        Node<N> next;

        Node(N data) {
            this.data = data;
            this.next = null;
        }
    }

    public LinkedList() {
        this.head = null;
        this.tail = null;
    }


    @Override
    public Iterator<N> iterator() {
        return new LinkedListIterator<>(this);
    }

    private static class LinkedListIterator<N> implements Iterator<N> {
        private Node<N> current;

        public LinkedListIterator(LinkedList<N> list) {
            this.current = list.head;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public N next() {
            N data = current.data;
            current = current.next;
            return data;
        }
    }

    /*
    add all items to list
     */
    public void addAll(N... items) {
        for (N item : items) {
            add(item);
        }

    }

    public void addFirst(N item) {

        Node<N> newNode = new Node<>(item);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head = newNode;
        }
    }

    public void addLast(N item) {
        Node<N> newNode = new Node<>(item);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    public void add(N item) { // same as addLast
        Node<N> newNode = new Node<>(item);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    public void getFirst() {
        if (isEmpty()) {
            System.out.println("List is empty");
        } else {
            System.out.println("First item: " + head.data);
        }
    }

    public void getLast() {
        if (isEmpty()) {
            System.out.println("List is empty");
        } else {
            System.out.println("Last item: " + tail.data);
        }
    }

    // insert item at index
    public void insertAt(int index, N item) {
        if (index < 0) {
            System.out.println("Invalid index");
            return;
        }
        if (index == 0) {
            addFirst(item);
            return;
        }
        Node<N> newNode = new Node<>(item);
        Node<N> current = head;
        for (int i = 0; i < index - 1; i++) {
            if (current == null) {
                System.out.println("Invalid index");
                return;
            }
            current = current.next;
        }
        if (current == null) {
            System.out.println("Invalid index");
            return;
        }
        newNode.next = current.next;
        current.next = newNode;
        if (newNode.next == null) {
            tail = newNode;
        }
    }


    public void deleteHead() {
        if (isEmpty()) {
            System.out.println("List is empty");
        } else {
            head = head.next;
            if (head == null) {
                tail = null;
            }
            System.out.println("Head deleted");
        }
    }

    // Set head and tail to null, effectively deleting the list.
    public void deleteList() {
        head = null;
        tail = null;
        System.out.println("List has been deleted");
    }

    // delete a node with a given value and close the gap
    public void deleteNode(N value) {
        Node<N> current = head;
        Node<N> previous = null;
        while (current != null) {
            if (value.equals(current.data)) {
                if (previous == null) {
                    head = current.next;
                } else {
                    previous.next = current.next;
                }
                // Update tail if removing last node
                if (current == tail) {
                    tail = previous;
                }
                return;
            }
            previous = current;
            current = current.next;
        }
    }


    public void show() {
        Node<N> current = head;
        while (current != null) {
            System.out.println(current.data);
            current = current.next;
        }
        System.out.println();
    }

    /*
     ************ MANIPULATING THE LIST ************
     */

    /*
    GET & SET HEAD & TAIL
     */
    public void getHead() {
        System.out.println("Head: " + head.data);
    } // same as getFirst

    public void getTail() {
        System.out.println("Tail: " + tail.data);
    } // same as getLast

    public void setHead(Node<N> head) {
        this.head = head;
    }

    public void setTail(Node<N> tail) {
        this.tail = tail;
    }

    /*
    Get size of list
     */

    public void getSize() {
        System.out.println("Size: " + size());
    }

    public int size() {
        int count = 0;
        Node<N> current = head;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    // Remove an item from the list
    public void remove(N item) {
        Node<N> current = head;
        Node<N> previous = null;

        // Handle empty list case
        if (current == null) {
            return;
        }

        // Handle case where item is in head node
        if (current.data.equals(item)) {
            head = current.next;
            return;
        }

        // Search for item in rest of list
        while (current != null && !current.data.equals(item)) {
            previous = current;
            current = current.next;
        }

        // If item was found, remove it by updating links
        if (current != null) {
            previous.next = current.next;
        }
    }


    // set node by index
    public void set(int index, N item) {
        Node<N> current = head;
        int count = 0;
        while (current != null) {
            if (count == index) {
                current.data = item;
                return;
            }
            count++;
            current = current.next;
        }
    }

    // Contains using ==
    // delete when sure not needed
//    public boolean contains(N item) {
//        Node<N> current = head;
//        while (current != null) {
//            if (current.data == item) { // using ==
//                return true;
//            }
//            current = current.next;
//        }
//        return false;
//    }

    //    Contains using equals()
    public boolean contains(N item) {
        if (item == null) return false;
        Node<N> current = head;
        while (current != null) {
            if (item.equals(current.data)) {  // Using equals()
                return true;
            }
            current = current.next;
        }
        return false;
    }


    // get index of item in list
    public int indexOf(N item) {
        Node<N> current = head;
        int index = 0;
        while (current != null) {
            if (current.data == item) {
                return index;
            }
            index++;
            current = current.next;
        }
        return -1;
    }

    // check if list is empty
    public boolean isEmpty() {
        return head == null;
    }

    // add at index
    public void add(int index, N item) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative");
        }

        Node<N> newNode = new Node<>(item);
        if (index == 0) {
            newNode.next = head;
            head = newNode;
            if (tail == null) {
                tail = newNode;
            }
            return;
        }

        Node<N> current = head;
        int currentIndex = 0;
        while (current != null && currentIndex < index - 1) {
            current = current.next;
            currentIndex++;
        }

        if (current == null) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }

        newNode.next = current.next;
        current.next = newNode;
        if (newNode.next == null) {
            tail = newNode;
        }
    }


    // remove at index
    public void remove(int index) {
        if (index < 0 || head == null) {
            throw new IndexOutOfBoundsException();
        }

        if (index == 0) {
            head = head.next;
            if (head == null) {
                tail = null;
            }
            return;
        }

        Node<N> current = head;
        int currentIndex = 0;
        while (current != null && currentIndex < index - 1) {
            current = current.next;
            currentIndex++;
        }

        if (current == null || current.next == null) {
            throw new IndexOutOfBoundsException();
        }

        if (current.next == tail) {
            tail = current;
        }
        current.next = current.next.next;
    }


    // get at index
    public N get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Try 0 or a positive number!");
        }

        Node<N> current = head;
        int currentIndex = 0;

        while (current != null) {
            if (currentIndex == index) {
                return current.data;
            }
            currentIndex++;
            current = current.next;
        }
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
    }


    // send list to array
    public Object[] toArray() {
        Object[] array = new Object[size()];
        Node<N> current = head;
        int index = 0;
        while (current != null) {
            array[index++] = current.data;
            current = current.next;
        }
        return array;
    }


}

