import org.junit.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by aaa on 17-12-28.
 */
public class DailyTest {
    
    @Test
    public void buildP(){
        int[] elec = new int[]{1, 2, 3, 4, 5};
    }

    @Test
    public void aaa() throws IOException {
        String origin = "thunder://QUFodHRwOi8vdmlweHouYm9jYWktenVpZGEuY29tLzIwMTIvRMfYuLMtMDUubXA0Wlo==";
        origin = origin.substring(10);
        System.out.println(origin);
        BASE64Decoder decoder = new BASE64Decoder();
        String content =  new String(decoder.decodeBuffer(origin),"GBK"); // "UTF-8"
        System.out.println(content.replace("AA", "").replace("ZZ", ""));
    }


    @Test
    public void test(){
        ExpressionParser parser = new SpelExpressionParser();
        String expression = "T(java.lang.Runtime).getRuntime().exec('find . -name a*')";
        String result = parser.parseExpression(expression).getValue().toString();
        System.out.println(result);
    }
    
    public static void main(String[] args) {
        DailyTest dailyTest = new DailyTest();
        int length = dailyTest.buy(99999);
        System.out.println("length:" + length);
    }

    public int buy(int n) {
        int length = 1;
        while (n > 1) {
            //n = (n % 2 == 1) ? n * 3 + 1 : n / 2;
            if (n % 2 == 1) {
                n = n * 3 + 1;
            } else {
                n /= 2;
            }
            System.out.println("n:" + n);
            length++;
        }
        return length;
    }
    
    private void test(int i){
        ExecutorService executorService = Executors.newFixedThreadPool(i);
    }
}
