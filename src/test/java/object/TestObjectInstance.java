package object;

import org.junit.Test;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by ldb on 2016/7/25.
 */
@SuppressWarnings("Since15")
public class TestObjectInstance {

    @Test
    public void contrastClassTest(){
        Class c1 = new ArrayList<String>().getClass();
        Class c2 = new ArrayList<Integer>().getClass();
        System.out.println(c1 == c2);
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