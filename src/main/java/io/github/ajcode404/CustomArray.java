package io.github.ajcode404;
// add and add with index
// delete/remove and remove with index
// replace
// only grow  logic

import java.util.Arrays;

public class CustomArray {
    private int size;
    private int[] arr;
    private int currIndex;

    private final double growFactor = 1.5;

    public CustomArray() {
        this(16);
    }

    public CustomArray(int size) {
        this.size = size;
        this.arr = new int[size];
        this.currIndex = 0;
    }

    public void add(int value) {
        add(currIndex, value);
    }

    public void add(int index, int value) {
        if (index < 0 || index > currIndex) {
            throw new IllegalArgumentException("Index is wrong");
        }
        if (size == currIndex + 1) {
            growAndCopy();
        }
        int currValue = arr[index];
        arr[index] = value;
        currIndex++;
        for (int i = index + 1; i < currIndex; i++) {
            int temp = arr[i];
            arr[i] = currValue;
            currValue = temp;
        }
    }

    public void replace(int index, int value) {
        if (index < 0 || index > currIndex) {
            throw new IllegalArgumentException("Index is wrong");
        }
        arr[index] = value;
    }

    public int remove(int index) {
        if (index < 0 || index > currIndex) {
            throw new IllegalArgumentException("Index is wrong");
        }
        int value = arr[index];
        for (int i = index; i < currIndex; i++) {
            int temp = arr[i + 1];
            arr[i] = temp;
        }
        arr[currIndex] = 0;
        currIndex--;
        size--;
        return value;
    }

    private void growAndCopy() {
        int currSize = size;
        size = (int)(size * growFactor);
        int[] arr = new int[size];
        for (int i = 0; i < currSize; i++) {
            arr[i] = this.arr[i];
        }
        this.arr = arr;
    }

    public static void main(String[] args) {
        CustomArray array = new CustomArray();
        array.add(10);
        array.add(10);
        array.add(10);
        array.add(10);
        array.add(10);
        array.add(2, 40);
        array.remove(2);
        System.out.printf(array.toString());
    }

    @Override
    public String toString() {
        return Arrays.toString(arr);
    }
}