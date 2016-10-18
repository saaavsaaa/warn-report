import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ldb on 2016/6/2.
 */
public class ConcurrentRun {
    public static void executeTasks(int threadCounts, final Runnable task) throws InterruptedException {
        final AtomicInteger count = new AtomicInteger();
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(threadCounts);
        for (int i = 0; i < threadCounts; i++) {
            Thread thread = new Thread(){
                public void run() {
                    try {
                        //当前线程开始等待
                        startGate.await();
                        try{
                            task.run();
                            System.out.println(count.incrementAndGet());
                        }
                        finally{
                            endGate.countDown();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }

        long start = System.nanoTime();
        startGate.countDown();
        //等待n个完成
        endGate.await();
        long end = System.nanoTime();
        System.out.println(end - start);
    }
}
