import org.junit.Test;
import util.IdGenerator;

import java.util.UUID;

/**
 * Created by root on 17-4-25.
 */
public class TestConcurrent {
    @Test
    public void testConcurrentCreate() throws InterruptedException {
        Runnable create = () -> {
            System.out.println(IdGenerator.INSTANCE.createNewId());
        };
        ConcurrentRun.executeTasks(10000, create);
    }
    
    @Test
    public void testCreate(){
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            IdGenerator.INSTANCE.createNewId();
//            UUID.randomUUID();
        }
        long end = System.nanoTime();
        long result = end - start;
        long p = result / (1000 * 1000);
        System.out.println(p + "毫秒");
    }
}
