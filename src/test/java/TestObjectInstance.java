import org.junit.Test;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Currency;
import java.util.Optional;

/**
 * Created by ldb on 2016/7/25.
 */
@SuppressWarnings("Since15")
public class TestObjectInstance {

    @Test
    public void testBytesCatch(){
        int count = 0;
        byte [] data = {1, 2, 3, 4, 'a', 'b', 'c', 'd', 'e'};
        boolean result = contrastBytes(data);
        if (result){
            count++;
        }
        System.out.println(count);
    }
    
    private boolean contrastBytes(byte [] data){
        //        byte [] data = {1, 2, 3, 4, 97, 98, 99, 100};
        byte [] newData;
        newData = Arrays.copyOfRange(data, 0, 8);
         
//        for(byte i : newData) {
//            System.out.print(i + " ");
//        }
        if (1 == newData[0] && 2 == newData[1] && 3 == newData[2] && 4 == newData[3]
                && 97 == newData[4] && 98 == newData[5] && 99 == newData[6] && 100 == newData[7]){
            return true;
        } else {
            return false;
        }
    }
    
    @Test
    public void testChar(){
        char c = 99;
        System.out.println("c" + 1 + c);
        System.out.println(c + 1 + "c");
        System.out.println(c + (1 + "c"));
        System.out.println('c');
        System.out.println('c' + 1);
        System.out.println((char) 1);
        System.out.println('c' + (char) 1);
    }
    
    @Test
    public void testStringLength(){
        String str = "123456";
        change(str);
        System.out.println(str);
        str = change(str);
        System.out.println(str);
    }
    
    private String change(String str){
        Class clazz = String.class;
        try {
            Field valueField = clazz.getDeclaredField("value");
            valueField.setAccessible(true);
            valueField.set(str, new char[]{'1','2','3','4','5','6','7'});
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        str = "1234";
        return str;
    }
    
    @Test
    public void testString() throws UnsupportedEncodingException {
        byte[] bytes = new byte[] { 50, 0, -1, 11, -12 };
        String sendString = new String(bytes , "ISO-8859-1" );
        byte[] sendBytes = sendString.getBytes(  "ISO-8859-1" );
        System.out.println(Arrays.toString(sendBytes));
    }
    
    @Test
    public void testDyPro(){
        int n = 60;
        int be = 0;
        int a = 1;
        while (0 < n--){
            a = a + be;
            be = a - be;
        }
        System.out.println(a);
    }
    
    @Test
    public void testRecursion(){
        int n = 61;
        int a = recursion(n);
        System.out.println(a);
    }
    
    private int recursion(int n){
        System.out.println("ing : " + n);
        return (2 > n) ? n : recursion(n - 1) + recursion(n - 2);
    }
    
    @Test
    public void testTailRecursion(){
        int n = 61;
        int a = recursionTail(n, 0, 1);
        System.out.println(a);
    }
    
    private int recursionTail(int n, int acc1, int acc2){
        System.out.println("ing : " + n);
        if (n == 0){
            return acc1;
        }
        return recursionTail(n - 1, acc2, acc1 + acc2);
    }

    @Test
    public void testOptional(){
        String aaa = "5";
        Optional<String> property = Optional.ofNullable(aaa);
        int i = property.map(p -> Integer.valueOf(p)).orElse(3);
        System.out.println(i);
    }

    @Test
    public void testPattern(){
        BigDecimal money = new BigDecimal(211592.9999911119111);
        DecimalFormat pattern = new DecimalFormat(",###.##");
        pattern.setMaximumFractionDigits(15);
        String result = pattern.format(money);
        System.out.print(result);
    }

    @Test
    public void test(){
        B b = new B();
        A a = (A)b;
        if (a instanceof B){
            System.out.println("B," + a.getN());
        } else {
            System.out.println("A," + a.getN());
        }
    }
    
    @Test
    public void testSerializ(){
        SService s = new SService();
        A a = s.getA();
        System.out.println(a.getN());
    }
    
    @Test
    public void testSingle(){
        System.out.println(Single.INSTANCE.add());
        System.out.println(Single.INSTANCE.add());
        System.out.println(Single.INSTANCE.add());
    }
    
    @Test
    public void printAB(){
        int a = 10;
        int b = 20;
        over();
        System.out.println(a);
        System.out.println(b);
    }
    
    private void over(){
        PrintStream print = new PrintStream(System.out){
            @Override
            public void println(int a){
                super.println(a*10);
            }
        };
        System.setOut(print);
    }
    
    public static void main(String[] args) {
        overCache();
        Integer a = 10;
        Integer b = 20;
        System.out.println(a);
        System.out.println(b);
    }
    
    private static void overCache(){
        Class cache = Integer.class.getDeclaredClasses()[0];
        Field c;
        try {
            c = cache.getDeclaredField("cache");
            c.setAccessible(true);
            Integer[] array = (Integer[])c.get(cache);
            System.out.println(array[138]);
            System.out.println(array[148]);
            array[138] = 100;
            array[148] = 200;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

class SService{
    public A getA(){
        A a = new A(){{setN("dto");}};
//        A a = new A();
//        a.setN("dto");
        return a;
    }
}

class A{
    protected String n;

    public A(){
        n = "a";
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }
}

class B extends A{
    public B(){
        n = "b";
    }
}

enum Single{
    INSTANCE;
    int i = 0;
    public int add(){
        return ++i;
    }
}