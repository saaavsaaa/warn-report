import org.junit.Test;

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
    public void test(){
        
    }
    
    public static void main(String[] args) {
        DailyTest test = new DailyTest();
        test.test(10);
    }
    
    private void test(int i){
        ExecutorService executorService = Executors.newFixedThreadPool(i);
    }
}
