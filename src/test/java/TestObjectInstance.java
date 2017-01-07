import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
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
