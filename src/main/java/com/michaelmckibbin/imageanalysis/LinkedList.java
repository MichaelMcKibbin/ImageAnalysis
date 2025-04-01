package com.michaelmckibbin.imageanalysis;

import java.io.Serializable;
import java.util.*;

/**
 * A generic singly linked list implementation that stores elements of type N.
 * This implementation maintains both head and tail references for efficient
 * operations at both ends of the list.
 *
 * <p>This linked list supports basic operations such as:
 * <ul>
 *     <li>Adding elements at the start or end</li>
 *     <li>Removing elements from any position</li>
 *     <li>Inserting elements at any position</li>
 *     <li>Traversing the list</li>
 * </ul>
 *
 * @param <N> the type of elements held in this linked list
 */

public class LinkedList<N> implements Iterable<N>, Serializable {

    private Node<N> head;
    private Node<N> tail;


    /**
     * Removes all elements from the list.
     * After this operation, the list will be empty.
     */
    public void clear() {
        head = null;
        tail = null;
        System.out.println("List has been cleared");
    }

    /**
     * Represents a node in the linked list.
     * Each node contains the data element and a reference to the next node.
     *
     * @param <N> the type of data stored in the node
     */

    // inner class
    private static class Node<N> implements Serializable {
        N data;
        Node<N> next;

        /**
         * Constructs a new node with the given data.
         *
         * @param data the element to be stored in this node
         */

        Node(N data) {
            this.data = data;
            this.next = null;
        }
    }

    public LinkedList() {
        this.head = null;
        this.tail = null;
    }


    /**
     * Returns an iterator over the elements in this list in proper sequence.
     * The returned iterator is fail-fast.
     *
     * @return an iterator over the elements in this list
     */

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

    /**
     * Adds an element to the beginning of the list.
     *
     * @param item the element to add
     * @throws NullPointerException if the specified element is null
     */

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


    /**
     * Retrieves the first element in the list.
     *
     * @return the first element in the list
     * @throws NoSuchElementException if the list is empty
     */
    public N getFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
        return head.data;
    }

    /**
     * Retrieves the last element in the list.
     * @return
     * @throws NoSuchElementException if the list is empty
     */
    public N getLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
        return tail.data;
    }

    /**
     * Inserts an element at the specified position in the list.
     *
     * @param index the position at which to insert the element
     * @param item the element to insert
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0)
     * @throws NullPointerException if the specified element is null
     */
    public void insertAt(int index, N item) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative!");
        }
        if (index == 0) {
            addFirst(item);
            return;
        }
        Node<N> newNode = new Node<>(item);
        Node<N> current = head;
        for (int i = 0; i < index - 1; i++) {
            if (current == null) {
                throw new IndexOutOfBoundsException("Index: " + index + " is out of bounds");
            }
            current = current.next;
        }
        if (current == null) {
            throw new IndexOutOfBoundsException("Index cannot be null!");
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



    /**
     * Checks if the list contains a specific element.
     * Uses equals() method for comparison.
     *
     * @param item the element to search for
     * @return true if the list contains the specified element, false otherwise
     */

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

    /**
     * Checks if the list is empty.
     *
     * @return true if the list contains no elements, false otherwise
     */
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

    public void deleteAt(int index) {
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
            throw new IndexOutOfBoundsException("Index cannot be negative");
        }
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot get element from empty list");
        }
        Node<N> current = head;
        for (int i = 0; i < index; i++) {
            if (current == null) {
                throw new IndexOutOfBoundsException("Index: " + index + " is out of bounds");
            }
            current = current.next;
        }
        if (current == null) {
            throw new IndexOutOfBoundsException("Index: " + index + " is out of bounds");
        }
        return current.data;
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

    public void addAll(Collection<? extends N> collection) {
        for (N item : collection) {
            add(item);
        }
    }

    public List<N> toList() {
        List<N> list = new ArrayList<>();
        for (N item : this) {
            list.add(item);
        }
        return list;
    }

    public N peekFirst() {
    return isEmpty() ? null : head.data;
}

public N peekLast() {
    return isEmpty() ? null : tail.data;
}

public void reverse() {
    Node<N> prev = null;
    Node<N> current = head;
    Node<N> next;
    tail = head;

    while (current != null) {
        next = current.next;
        current.next = prev;
        prev = current;
        current = next;
    }
    head = prev;
}



}

