package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T> , Deque<T>{
    private static final int INITIAL_CAPACITY = 8;
    private static final int K = 4;
    private T[] array;
    private int head;
    private int tail;
    private int size;
    private int capacity;
    private class MyIterator implements Iterator<T> {
        private int cursor;
        public MyIterator() {
            cursor = head;
        }

        @Override
        public boolean hasNext() {
            return cursor != tail;
        }

        @Override
        public T next() {
            T item = array[cursor];
            cursor = getNextIndex(cursor);
            return item;
        }
    }

    public ArrayDeque() {
        array = (T[]) new Object[INITIAL_CAPACITY];
        head = tail = 0;
        size = 0;
        capacity = INITIAL_CAPACITY;
    }

    private int getPrevIndex(int index) {
        return (index - 1 + capacity) % capacity;
    }

    private int getNextIndex(int index) {
        return (index + 1) % capacity;
    }

    private void resize() {
        T[] newArray = (T[]) new Object[capacity * 2];
        // 复制元素到新数组
        for (int i = 0; i < size; i++) {
            newArray[i] = array[(head + i) % capacity];
        }
        array = newArray;
        head = 0; // 重置 head 为新数组的开始位置
        tail = size; // 更新 tail 为新数组的末尾位置
        capacity *= 2; // 更新容量
    }



    public void addFirst(T x) {
        if(size == capacity)  resize();
        head = getPrevIndex(head);
        array[head] = x;
        size++;
    }

    public void addLast(T x) {
        if(size == capacity)  resize();
        array[tail] = x;
        tail = getNextIndex(tail);
        size++;
    }

    private void resizeForSpace(){
        if(K * size < capacity && capacity > INITIAL_CAPACITY){
            T[] newArray = (T[]) new Object[capacity / 2];
            for (int i = 0; i < size; i++) {
                newArray[i] = array[(head + i) % capacity];
            }
            array = newArray;
            head = 0;
            tail = size;
            capacity /= 2;
        }
    }

    public T removeFirst() {
        if(size == 0) return null;
        resizeForSpace();
        T item = array[head];
        array[head] = null;
        head = getNextIndex(head);
        size --;
        return item;
    }

    public T removeLast() {
        if(size == 0) return null;
        resizeForSpace();
        tail = getPrevIndex(tail);
        size --;
        T item = array[tail];
        array[tail] = null;
        return item;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        if(size == 0) return;
        int index = head;
        while(index != tail) {
            System.out.print(array[index] + " ");
            index = getNextIndex(index);
        }
        System.out.println();
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return array[(head + index) % capacity];
    }

    @Override
    public Iterator<T> iterator() {
        return new MyIterator();
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof ArrayDeque)) return false;
        ArrayDeque<T> other = (ArrayDeque<T>) o;
        if(size != other.size) return false;
        int cur1 = head , cur2 = other.head;
        while(cur1 != tail && cur2 != other.tail) {
            if(!array[cur1].equals(array[cur2])) return false;
            cur1 = getNextIndex(cur1);
            cur2 = getNextIndex(cur2);
        }
        return true;
    }
}
