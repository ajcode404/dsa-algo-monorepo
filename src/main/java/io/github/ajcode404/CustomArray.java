package io.github.ajcode404;
// add and add with index
// delete/remove and remove with index
// replace
// only grow  logic
import java.util.Arrays;

public class CustomArray<T> {
    private int size;
    private Object[] arr;
    private int currIndex;
    private final double growFactor = 1.5;

    public CustomArray() {
        this(16);
    }

    public CustomArray(int size) {
        this.size = size;
        this.arr = new Object[size];
        this.currIndex = 0;
    }

    public void add(T value) {
        add(currIndex, value);
    }

    public void add(int index, T value) {
        if (index < 0 || index > currIndex) {
            throw new IllegalArgumentException("Index is wrong");
        }
        if (size == currIndex + 1) {
            growAndCopy();
        }
        T currValue = (T) arr[index];
        arr[index] = value;
        currIndex++;
        for (int i = index + 1; i < currIndex; i++) {
            T temp = (T) arr[i];
            arr[i] = currValue;
            currValue = temp;
        }
    }

    public void replace(int index, T value) {
        if (index < 0 || index > currIndex) {
            throw new IllegalArgumentException("Index is wrong");
        }
        arr[index] = value;
    }

    public T remove(int index) {
        if (index < 0 || index > currIndex) {
            throw new IllegalArgumentException("Index is wrong");
        }
        T value = (T)arr[index];
        for (int i = index; i < currIndex; i++) {
            T temp = (T)arr[i + 1];
            arr[i] = temp;
        }
        arr[currIndex] = null;
        currIndex--;
        size--;
        return value;
    }

    private void growAndCopy() {
        int currSize = size;
        size = (int)(size * growFactor);
        Object[] arr = new Object[size];
        for (int i = 0; i < currSize; i++) {
            arr[i] = this.arr[i];
        }
        this.arr = arr;
    }

    public static void main(String[] args) {
        CustomArray<Character> array = new CustomArray<>();
        array.add('1');
        array.add('1');
        array.add('1');
        array.add('1');
        array.add('1');
        array.add(2, 'c');
//        array.remove(2);
        System.out.printf(array.toString());
    }

    @Override
    public String toString() {
        return Arrays.toString(arr);
    }
}