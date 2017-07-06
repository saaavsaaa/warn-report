import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import structure.ArrayListExtend;
import util.Calculate;
import util.StringUtil;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by aaa on 17-6-26.
 */
public class YanghuiTriangleTest {
    private static final char Interval = ' ';
    
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
                String value = StringUtils.leftPad(String.valueOf(e), Calculate.getDigit(layerRowsExceptTop), Interval);
                print += value;
                print += "       ";
            }
            upLayer = newLayer.addInThis(0);
            System.out.println(print);
        }
    }
    
    /*
    * 按多项式各项系数
    * */
    @Test
    public void buildByPolynomial() {
//        System.out.println(calculateCombination(30, 1).toString());
        Stack<String> rows = new Stack<>();
        int layerRowsExceptTop = 30;
        int lastLength = 0;
        for (int l = layerRowsExceptTop; l > -1; l--) {
            StringBuilder thisLine = new StringBuilder();
            
            for (int i = 0; i < l + 1; i++) {
                BigInteger position = Calculate.calculateCombination(l, i);
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
 
    @Test
    public void buildTwoRow(){
        int layerRowsExceptTop = 31;
        Stack<String[]> rows = new Stack<>();
        for (int r = layerRowsExceptTop - 1; r > 1; r--) {
            String[] row = new String[r + 3];
            String[] row1 = new String[r + 2];
            for(int col = 0; col < r; col++){
                BigInteger c1 = Calculate.calculateCombination(r - 1, col - 1);
                BigInteger c2 = Calculate.calculateCombination(r - 1, col);
                row[col] = c2.add(c1).toString();
                row1[col] = c1.toString();
                row1[col + 1] = c2.toString();
            }
            rows.push(row);
            rows.push(row1);
            r--;
        }
    
        while (!rows.empty()){
            String[] row = rows.pop();
            for (int i = 0; i < row.length; i++) {
                System.out.print(row[i] + Interval);
            }
            System.out.println();
        }
    }
    
    Stack<String> ss1 = new Stack<>();
    Stack<String> si = new Stack<>();
    Stack<String> ss = new Stack<>();
    private BigInteger recursion(int r, int col){
        if (r < 0){
            return BigInteger.ZERO;
        }
        BigInteger c1 = Calculate.calculateCombination(r - 1, col + 1);
        BigInteger c2 = recursion(r - 1, col);
        ss1.push(c1.toString());
        si.push(String.valueOf(r));
        ss.push(c2.add(c1).toString());
        return c2.add(c1);
    }
    
    private void printStack(Stack stack){
        while (!stack.empty()){
            String one = (String) stack.pop();
            System.out.print(one + Interval);
        }
        System.out.println();
    }
    
    @Test
    public void buildTilt(){
        recursion(30, 0);
    }

    @Test
    public void test(){
        recursion(30, 0);
        printStack(ss1);
        printStack(si);
        printStack(ss);
    
        buildByPolynomial();
        
        /*int a = 11;
        int b = 5;

        //一次循环生成两行
        BigInteger c = calculateCombination(a, b);
        BigInteger c1 = calculateCombinationByAdd(a, b);
        System.out.println(c + " : " + c1);
        
        System.out.println(StringUtil.padPrx("aaa", 12 , ' '));*/
    }
}
