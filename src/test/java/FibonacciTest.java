import org.junit.Test;

/**
 * Created by aaa on 17-6-29.
 */
public class FibonacciTest {
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
}
