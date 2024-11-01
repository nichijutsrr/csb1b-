package deque;

import java.util.Comparator;
import java.util.Iterator;

// 要么说类是可比较的,要么说类中有一个比较器
public class MaxArrayDeque<T> extends ArrayDeque<T>{
    private Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> comparator) {
          this.comparator = comparator;
    }

     public T max(){
        if(isEmpty()) return null;
        T max = get(0);
        Iterator<T> iterator = iterator();
        while(iterator.hasNext()){
            T element = iterator.next();
            if(comparator.compare(max,element) > 0){
                max = element;
            }
        }
        return max;
     }

     public T max(Comparator<T> comparator){
         if(isEmpty()) return null;
         T max = get(0);
        Iterator<T> iterator = iterator();
        while(iterator.hasNext()){
            T element = iterator.next();
            if(comparator.compare(max,element) > 0){
                max = element;
            }
        }
        return max;
     }
}
