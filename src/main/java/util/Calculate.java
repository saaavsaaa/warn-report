package util;

import java.math.BigInteger;

/**
 * Created by aaa on 17-7-6.
 */
public abstract class Calculate {
    final static int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE};
    
    public static int getDigit(int x) {
        for (int i = 0; ; i++) {
            if (x <= sizeTable[i]) {
                return i + 1;
            }
        }
    }
    
    /*
    * n!
    * */
    public static BigInteger calculateFactorial(long n) {
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
    
    /*
    * c(a,b) = c(a-1,b-1) + c(a-1,b)
    * */
    public static BigInteger calculateCombinationByAdd(int a, int b) {
        if (b == 0 || a == b) {
            return BigInteger.ONE;
        }
        //一次循环生成两行
        BigInteger c1 = calculateCombination(a - 1, b - 1);
        BigInteger c2 = calculateCombination(a - 1, b);
        return c1.add(c2);
    }
    
    /*
    * c(a,b)=a!/((a-b)!*b!)
    * */
    public static BigInteger calculateCombination(int a, int b) {
        if (b < 0 || a < 0){
            return BigInteger.ZERO;
        }
        if (b == 0 || a == b) {
            return BigInteger.ONE;
        }
        BigInteger aFactorial = Calculate.calculateFactorial(a);
//        System.out.println(aFactorial);
        BigInteger bFactorial = Calculate.calculateFactorial(b);
//        System.out.println(bFactorial);
        BigInteger abSubtract = Calculate.calculateFactorial(a - b);
//        System.out.println(abSubtract);
        BigInteger c = aFactorial.divide(bFactorial.multiply(abSubtract));
//        System.out.println(c);
        return c;
    }
    
}
