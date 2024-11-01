package deque;

import java.util.Deque;
import java.util.Iterator;

// 分层 , 缓存 , 不变量
public class LinkedListDeque<T> implements Iterable<T> {
    private class Node{
        T item;
        Node prev;
        Node next;
        public Node(T item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    private class MyIterator implements Iterator<T>{
        Node current;
        public MyIterator() {
            current = sentinel;
        }

        @Override
        public boolean hasNext() {
            return current.next != sentinel;
        }

        @Override
        public T next() {
            current = current.next;
            return current.item;
        }
    }

    private Node sentinel;
    private int size;


    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }


    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null; // 索引无效，返回 null
        }
        return getRecursiveHelper(sentinel.next, index); // 从第一个节点开始查找
    }

    // 辅助方法
    private T getRecursiveHelper(Node current, int index) {
        if (index == 0) {
            return current.item; // 找到目标索引，返回当前节点的 item
        }
        return getRecursiveHelper(current.next, index - 1); // 递归到下一个节点，索引减一
    }



    public void addFirst(T item) {
        Node cur = new Node(item, sentinel, sentinel.next);
        sentinel.next.prev = cur;
        sentinel.next = cur;
        size++;
    }

    public void addLast(T item) {
         Node cur = new Node(item, sentinel.prev, sentinel);
         sentinel.prev.next = cur;
         sentinel.prev = cur;
         size++;
    }

    public boolean isEmpty() {
         return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        if(size == 0) return;
        Node cur = sentinel.next;
        while(cur != sentinel) {
            System.out.print(cur.item + " ");
            cur = cur.next;
        }
        System.out.println();
    }

    public T removeFirst() {
        if(size == 0) return null;
        T item = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size--;
        return item;
    }

    public T removeLast() {
        if(size == 0) return null;
        T item = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size--;
        return item;
    }

    public T get(int index){
        if(index < 0 || index >= size) return null;
        Iterator<T> iterator = iterator();
        while(iterator.hasNext() && index >= 0){
            T item = iterator.next();
            if(index == 0) return item;
            index--;
        }
        return null;
    }


    @Override
    public Iterator<T> iterator() {
        return new MyIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // 如果是同一个对象，直接返回 true
        if (!(o instanceof LinkedListDeque)) return false; // 检查类型

        LinkedListDeque<T> other = (LinkedListDeque<T>) o; // 类型转换
        if (size != other.size) return false; // 比较大小

        Node current1 = this.sentinel.next; // 假设有 front 节点
        Node current2 = other.sentinel.next;// 假设有 front 节点

        while (current1 != this.sentinel && current2 != other.sentinel) {
            if (!current1.item.equals(current2.item)) return false; // 比较内容
            current1 = current1.next; // 移动到下一个节点
            current2 = current2.next; // 移动到下一个节点
        }

        return true; // 所有元素相等
    }


}
