package deque;

import org.junit.Test;

public class ArrayDequeTest {

    @Test
    public void testSizeAndIsEmpty() {
        ArrayDeque deque = new ArrayDeque();
        assert(deque.isEmpty());
        deque.addFirst("a");
        assert(!deque.isEmpty());
        deque.addFirst("b");
        assert(deque.size() == 2);
    }

    @Test
    public void testAddFirstAndAddLast() {
        ArrayDeque deque = new ArrayDeque();
        deque.addFirst("a");
        deque.addFirst("b");
        deque.printDeque();
        deque.addLast("c");
        deque.addLast("d");
        deque.printDeque();
    }
    @Test
    public void testRemoveFirstAndAddLast() {
        ArrayDeque deque = new ArrayDeque();
        deque.addFirst("a");
        deque.addFirst("b");
        deque.removeLast();
        deque.removeLast();
        deque.removeLast();
        deque.printDeque();
//        deque.removeFirst();
        Object o = deque.removeLast();
        assert(o == null);

    }


}
