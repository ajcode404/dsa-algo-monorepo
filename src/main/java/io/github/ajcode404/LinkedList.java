package io.github.ajcode404;

class Node<T> {
    T data;
    Node<T> next;

    public Node(T data) {
        this(data, null);
    }

    public Node(T data, Node<T> next) {
        this.data = data;
        this.next = next;
    }

    @Override
    public String toString() {
        return "{ data: "+data+" }";
    }
}

class CustomLinkedList {
    private int size;
    private Node<Integer> root;
    private Node<Integer> head;

    public CustomLinkedList() {
        this.root = null;
        this.head = null;
        size = 0;
    }


    // insertion
    public void add(int value) {
        if (root == null) {
            root = new Node<>(value);
            head = root;
            return;
        }
        head.next = new Node<>(value);
        head = head.next;
        size++;
    }

    public int delete(int data) {
        if (root == null) return -1;
        if (root.data == data) {
            root = root.next;
            return data;
        }   
        Node<Integer> curr = root;
        while (curr.next != null) {
            if (curr.next.data == data) {
                curr.next = curr.next.next;
                return data;
            }
            curr = curr.next;
        }
        return -1;
    }

    // traversal
    public String toString() {
        Node<Integer> temp = root;
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        while (temp.next != null) {
            sb.append(temp.data.toString()).append(", ");
            temp = temp.next;
        }
        sb.append(temp.data).append("}");
        return sb.toString();
    }

    public static void main(String[] args) {
        CustomLinkedList c = new CustomLinkedList();
        c.add(1);
        c.add(2);
        c.add(3);
        c.add(4);
        c.add(5);
        c.delete(3);
        System.out.println(c);
    }
}
