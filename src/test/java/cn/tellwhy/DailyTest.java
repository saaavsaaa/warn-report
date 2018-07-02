package cn.tellwhy;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by aaa on 17-12-28.
 */
//@ContextConfiguration("")
public class DailyTest {
    
    /*static {
        System.out.println("1111111111");
        if (true){
            while (true){
                System.out.println("aaa");
            }
        }
    }*/
    
    @Test
    public void buildP(){
        int[] elec = new int[]{1, 2, 3, 4, 5};
    }
    
    public static void main(String[] args) {
        String[] array = {""};
        array[test()] += "a";
    }
    
    static int test(){
        System.out.println("aaaaaaaaaa");
        return 0;
    }
    
    @Test
    public void daily() {
    }
    
    private void testExec(int i){
        ExecutorService executorService = Executors.newFixedThreadPool(i);
    }
}
