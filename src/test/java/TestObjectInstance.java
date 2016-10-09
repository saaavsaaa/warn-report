import org.junit.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Currency;
import java.util.Optional;

/**
 * Created by ldb on 2016/7/25.
 */
@SuppressWarnings("Since15")
public class TestObjectInstance {

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
