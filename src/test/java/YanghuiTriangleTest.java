import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import util.StringUtil;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by aaa on 17-6-26.
 */
public class YanghuiTriangleTest {
    private static final char Interval = ' ';
    final static int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE};
    
    @Test
    public void build() {
        int layerRowsExceptTop = 30;
        ArrayListExtend<Integer> upLayer = new ArrayListExtend<>();
        upLayer.addInThis(0).addInThis(1).addInThis(0);
        System.out.println(StringUtils.leftPad("1", layerRowsExceptTop * 2, Interval));
        int beginLayer = 2;
        int upAddA;
        int upAddB;
        for (int r = layerRowsExceptTop; r > 0; r--, beginLayer++) {
            ArrayListExtend<Integer> newLayer = new ArrayListExtend<>();
            newLayer.addInThis(0);
            String print = StringUtils.leftPad("", (r - 1) * 4, Interval);
            for (int i = 0; i < beginLayer; i++) {
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
    
    private int getDigit(int x) {
        for (int i = 0; ; i++) {
            if (x <= sizeTable[i]) {
                return i + 1;
            }
        }
    }
    
    /*
    * 按多项式各项系数
    * */
    @Test
    public void calculatePolynomial() {
//        System.out.println(calculateCombination(30, 1).toString());
        Stack<String> rows = new Stack<>();
        int layerRowsExceptTop = 30;
        int lastLength = 0;
        for (int l = layerRowsExceptTop; l > 0; l--) {
            StringBuilder thisLine = new StringBuilder();
            for (int i = 0; i < l; i++) {
                BigInteger position = calculateCombination(l, i);
                thisLine.append(position).append("  ");
            }
            int thisLength = thisLine.length();
            if (lastLength > 0) {
                int prxSpace = (lastLength - thisLength) / 2;
                thisLine.insert(0, StringUtil.buildByChar(prxSpace, Interval));
//                System.out.println(prxSpace + " : " + thisLine.toString());
            } else {
                lastLength = thisLine.length();
            }
            rows.push(new String(thisLine));
        }
        
        while (!rows.empty()){
            System.out.println(rows.pop());
        }
    }
    
    /*
    * c(a,b)=a!/((a-b)!*b!)
    * */
    private BigInteger calculateCombination(int a, int b) {
        if (b == 0) {
            return BigInteger.ONE;
        }
        BigInteger aFactorial = calculateFactorial(a);
//        System.out.println(aFactorial);
        BigInteger bFactorial = calculateFactorial(b);
//        System.out.println(bFactorial);
        BigInteger abSubtract = calculateFactorial(a - b);
//        System.out.println(abSubtract);
        BigInteger c = aFactorial.divide(bFactorial.multiply(abSubtract));
//        System.out.println(c);
        return c;
    }
    
    /*
    * n!
    * */
    private BigInteger calculateFactorial(long n) {
        if (n <= 1) {
            return BigInteger.valueOf(n);
        }
        BigInteger result = BigInteger.ONE;
        for (int i = 1; i < n; i++) {
            result = result.multiply(BigInteger.valueOf(i + 1));
//            result *= i + 1;
//            System.out.println(i + 1 + " : " + result.toString());
        }
        return result;
    }
    
    @Test
    public void test(){
        System.out.println(StringUtil.padPrx("aaa", 12 , ' '));
    }
    

}

class ArrayListExtend<T> extends ArrayList<T> {
    public ArrayListExtend addInThis(T t) {
        super.add(t);
        return this;
    }
}
