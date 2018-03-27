import org.junit.Test;
import run.CurrentTrace;
import util.ConcurrentRun;
import util.IdCreator;
import util.IdGenerator;
import util.size.ClassIntrospection;
import util.size.ObjectInfo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by root on 17-4-25.
 */
public class TestConcurrent {
    @Test
    public void testConcurrentCreate() throws InterruptedException {
        Runnable create = () -> {
            System.out.println(IdGenerator.INSTANCE.createNewId());
//            UUID.randomUUID();
            /*try {
                System.out.println(IdCreator.INSTANCE.calculateKey());
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        };
        ConcurrentRun.executeTasks(10000, create);
    }
    
    @Test
    public void testCreate() throws ParseException, InterruptedException {
        long start = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
//            IdGenerator.INSTANCE.createNewId();
//            System.out.println(IdGenerator.INSTANCE.createNewId());
//            IdGenerator.INSTANCE.createNewKey();
            IdCreator.INSTANCE.calculateKey();
//            System.out.println(IdGenerator.INSTANCE.calculateKey());
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
    
    public static void main(String[] args) throws Exception {
        List<String> traceIds = new ArrayList<>();
        Runnable watch = () -> {
            String traceId = IdGenerator.INSTANCE.createNewId();
            CurrentTrace.INSTANCE.started(traceId);
            
            long i = Thread.currentThread().getId() % 2;
            if (i == 0) {
                CurrentTrace.INSTANCE.success(traceId);
            } else {
                CurrentTrace.INSTANCE.fail(traceId);
                traceIds.add(traceId);
            }
        };
        ConcurrentRun.executeTasks(10, watch);
    
        circleRetry(traceIds);
        /*Runnable retry = () -> {
            circleRetry(traceIds);
        };
        ConcurrentRun.executeTasks(10, retry);*/
    }
    
    private static void circleRetry(List<String> traceIds){
        while (!traceIds.isEmpty()) {
            printSize(CurrentTrace.INSTANCE);
            
            Iterator<String> iterator = traceIds.listIterator();
            while (iterator.hasNext()){
                String traceId = iterator.next();
                if (!CurrentTrace.INSTANCE.started(traceId)) {
                    System.out.println("Already started : " + traceId);
                    continue;
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
}
