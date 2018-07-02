package cn.tellwhy.structure;

/**
 * Created by root on 17-2-8.
 */
import java.util.ArrayList;
import java.util.Comparator;

/**
 * 二叉堆
 * @author DBJ(dubenju@126.com)
 *
 * @param <E>
 */
public class BinaryHeap<E extends Comparable<?>> {
    
    /* 按照数组下标，下标为n的节点，它的子结点下标为2 * n + 1 和 2 * n + 2; */
    private ArrayList<E> values;
    private boolean minHeap;
    private Comparator<E> comparator;
    
    public BinaryHeap(boolean isMinHeap, Comparator<E> comparator) {
        this.values = new ArrayList<E>();
        this.minHeap = isMinHeap;
        this.comparator = comparator;
    }
    
    public void insert(E element) {
        //percolate element to it's place in tree
        if (this.minHeap) {
            percolateUpMinHeap(element);
        } else {
            percolateUpMaxHeap(element);
        }
    }

    public E get() {
        if (isEmpty()) {
            return null;
        } else {
            return this.values.get(0);
        }
    }

    public E delete() {
        
        final E result = get();
        this.values.set(0, this.values.get(this.values.size() - 1));
        
        // set the unused element to 'null' so that the garbage collector
        // can free the object if not used anywhere else.(remove reference)
        this.values.remove(this.values.size() - 1);
        
        if (this.values.size() != 0) {
            // percolate top element to it's place in tree
            if (this.minHeap) {
                percolateDownMinHeap(0);
            } else {
                percolateDownMaxHeap(0);
            }
        }
        
        return result;
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }
    
    
    /**
     * 从索引位置向下过滤最小堆
     *
     * @param index 被过滤元素的索引
     */
    protected void percolateDownMinHeap(final int index) {
        final E element = this.values.get(index);
        int hole = index;
        
        
        while ((((hole + 1) * 2) - 1) <= this.values.size() - 1) {
            int child = (((hole + 1) * 2) - 1);
            
            // if we have a right child and that child can not be percolated up then move onto other child
            if (child < this.values.size() - 1 && compare(this.values.get(child + 1), this.values.get(child)) < 0) {
                child ++;
            }
            
            // if we found resting place of bubble then terminate search
            if (compare(this.values.get(child), element) >= 0) {
                break;
            }
            
            this.values.set(hole, this.values.get(child));
            hole = child;
        }
        
        this.values.set(hole, element);
    }
    
    /**
     * 从索引位置向下过滤最大堆
     *
     * @param index 被过滤元素的索引
     */
    protected void percolateDownMaxHeap(final int index) {
        final E element = this.values.get(index);
        int hole = index;
        
        while ((hole * 2) <= this.values.size()) {
            int child = hole * 2;
            
            // if we have a right child and that child can not be percolated up then move onto other child
            if (child != this.values.size() && compare(this.values.get(child + 1), this.values.get(child)) > 0) {
                child ++;
            }
            
            // if we found resting place of bubble then terminate search
            if (compare(this.values.get(child), element) <= 0) {
                break;
            }
            
            this.values.set(hole, this.values.get(child));
            hole = child;
        }
        
        this.values.set(hole, element);
    }
    
    /**
     * 从索引位置向上过滤最小堆
     *
     * @param index 被过滤元素的索引
     */
    protected void percolateUpMinHeap(final int index) {
        int hole = index;
        E element = this.values.get(hole);
        while (hole > 0 && compare(element, this.values.get((hole - 1) / 2)) < 0) {
            // save element that is being pushed down as the element"bubble"is percolated up
            final int next = (hole - 1) / 2;
            this.values.set(hole, this.values.get(next));
            hole = next;
        }
        this.values.set(hole, element);
    }
    
    /**
     * 过滤在最小堆底部的新元素
     *
     * @param element 数据元素
     */
    protected void percolateUpMinHeap(final E element) {
        
        int size = this.values.size();
        this.values.add(element);
        percolateUpMinHeap(size);
    }
    
    /**
     * 从索引位置向上过滤最大堆
     *
     * @param index 被过滤元素的索引
     */
    protected void percolateUpMaxHeap(final int index) {
        int hole = index;
        E element = this.values.get(hole - 1);
        while (hole > 1 && compare(element, this.values.get(( hole - 1) / 2)) > 0) {
            // save element that is being pushed down as the element"bubble"is percolated up
            final int next = ( hole - 1) / 2;
            this.values.set(hole, this.values.get(next));
            hole = next;
        }
        this.values.set(hole, element);
    }
    
    /**
     * 过滤在最大堆底部的新元素
     *
     * @param element 数据元素
     */
    protected void percolateUpMaxHeap(final E element) {
        
        int size = this.values.size();
        this.values.add(element);
        percolateUpMaxHeap(size);
    }
    
    /**
     * 如果指定比较器，使用比较器比较两个对象,否则自然比较
     *
     * @param a the first object
     * @param b the second object
     * @return 比较结果
     */
    private int compare(E a, E b) {
        if (this.comparator != null) {
            return this.comparator.compare(a, b);
        } else {
            @SuppressWarnings("unchecked")
            Comparable<E> ca = Comparable.class.cast(a);
            return ca.compareTo(b);
        }
    }
    
    /**
     * Returns a string representation of this heap.
     * The returned string is similar to those produced by standard JDK collections.
     *
     * @return a String representation of this heap
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < this.values.size(); i ++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(this.values.get(i));
        }
        sb.append("]");
        return sb.toString();
    }
}
