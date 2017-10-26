import org.junit.Test;
import run.CurrentTrace;
import util.ConcurrentRun;
import util.IdGenerator;
import util.size.ClassIntrospection;
import util.size.ObjectInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
//            IdGenerator.INSTANCE.createNewId();
            System.out.println(IdGenerator.INSTANCE.createNewId());
//            UUID.randomUUID();
        }
        long end = System.nanoTime();
        long result = end - start;
        long p = result / (1000 * 1000);
        System.out.println(p + "毫秒");
    }
    
    private static void printSize(Object o) {
        final ClassIntrospection ci = new ClassIntrospection();
        
        ObjectInfo res;
        
        try {
            res = ci.introspect(o);
            System.out.println("size:" + res.getDeepSize());
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private static void circleRetry(List<String> traceIds){
        while (!traceIds.isEmpty()) {
            printSize(CurrentTrace.INSTANCE);
            
            Iterator<String> iterator = traceIds.listIterator();
            while (iterator.hasNext()){
                String traceId = iterator.next();
                if (CurrentTrace.INSTANCE.started(traceId)) {
                    System.out.println("Already started : " + traceId);
                    return;
                }
    
                if (System.currentTimeMillis() % 2 == 0) {
                    CurrentTrace.INSTANCE.success(traceId);
                    iterator.remove();
                } else {
                    CurrentTrace.INSTANCE.fail(traceId);
                }
            }
    
            ConcurrentRun.sleepCurrentThread(6);
        }
    }
    
    public static void main(String[] args) throws Exception {
        List<String> traceIds = new ArrayList<>();
        Runnable watch = () -> {
            String traceId = IdGenerator.INSTANCE.createNewId();
            CurrentTrace.INSTANCE.started(traceId);
            
            traceIds.add(traceId);
            if (System.currentTimeMillis() % 2 == 0) {
                CurrentTrace.INSTANCE.success(traceId);
            } else {
                CurrentTrace.INSTANCE.fail(traceId);
            }
            
        };
        ConcurrentRun.executeTasks(10, watch);
    
        circleRetry(traceIds);
        /*Runnable retry = () -> {
            circleRetry(traceIds);
        };
        ConcurrentRun.executeTasks(10, retry);*/
    }
}
