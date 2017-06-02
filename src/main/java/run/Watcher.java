package run;

import java.util.concurrent.*;

/**
 * Created by root on 17-6-1.
 */
public class Watcher {
    
    private CountDownLatch runSignal = new CountDownLatch(1);
    private static Watcher watcher;
    
    private final ScheduledExecutorService scheduledExecutorService;
    
    public Watcher(){
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "stopScheduledThread"));
        stop();
    }
    
    public static void main(String[] args) throws InterruptedException {
        watcher = new Watcher();
        watcher.runSignal.await();
        watcher.scheduledExecutorService.shutdown();
        System.out.println("over");
    }
    
    private void stop(){
        this.scheduledExecutorService.scheduleAtFixedRate((Runnable) () -> {
            watcher.runSignal.countDown();
            System.out.println("stoped!");
        }, 10, 10, TimeUnit.SECONDS);
    }
}
