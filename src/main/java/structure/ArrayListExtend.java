package structure;

import java.util.ArrayList;

/**
 * Created by aaa on 17-7-6.
 */
public class ArrayListExtend<T> extends ArrayList<T> {
    public ArrayListExtend addInThis(T t) {
        super.add(t);
        return this;
    }
}
