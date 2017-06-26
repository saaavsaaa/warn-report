import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.*;

/**
 * Created by aaa on 17-6-26.
 */
public class YanghuiTriangleTest {
    private static final char Interval = ' ';
    final static int [] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE };

    @Test
    public void build(){
        int layerRowsExceptTop = 30;
        ArrayListExtend<Integer> upLayer = new ArrayListExtend<>();
        upLayer.addInThis(0).addInThis(1).addInThis(0);
        System.out.println(StringUtils.leftPad("1", layerRowsExceptTop * 2, Interval));
        int beginLayer = 2;
        int upAddA = 0;
        int upAddB = 0;
        for (int r = layerRowsExceptTop; r > 0; r--, beginLayer++) {
            ArrayListExtend<Integer> newLayer = new ArrayListExtend<>();
            newLayer.addInThis(0);
            String print = StringUtils.leftPad("", (r - 1) * 4, Interval);
            for(int i = 0; i < beginLayer; i++) {
                upAddA = upLayer.get(i) > -1 ? upLayer.get(i) : 0;
                upAddB = upLayer.get(i + 1) > -1 ? upLayer.get(i + 1) : 0;
                int e = upAddA + upAddB;
                newLayer.addInThis(e);
                String value = StringUtils.leftPad(String.valueOf(e), getDigit(layerRowsExceptTop), Interval);
                print += value;
                print += "       ";
            }
            upLayer = newLayer.addInThis(0);
            System.out.println(print);
        }
    }
    
    private int getDigit(int x){
        for (int i=0; ; i++) {
            if (x <= sizeTable[i]) {
                return i + 1;
            }
        }
    }
}

class ArrayListExtend<T> extends ArrayList<T> {
    public ArrayListExtend addInThis(T t){
        super.add(t);
        return this;
    }
}
