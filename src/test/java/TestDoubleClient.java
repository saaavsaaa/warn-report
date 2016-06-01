
import org.junit.Test;
import rocketDoubleWrite.ProducerClient;

import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class TestDoubleClient {
    @Test
    public void send(){
        int a = 10000;
        for (int i = 0; i < a; i++) {
            ProducerClient.send("a1", "{'a12':'121'}");
        }
    }

    private void runTasks(int nThreads, final Runnable task) throws InterruptedException {
        final AtomicInteger count = new AtomicInteger();
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(nThreads);
        for (int i = 0; i < nThreads; i++) {
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

    @Test
    public void test() throws InterruptedException {
        Runnable runnable = new Runnable() {
            public void run() {
                boolean isSuccess = ProducerClient.send("2016060117000", "{'a12':'121'}");
                System.out.print(isSuccess + "\n");
            }
        };
        runTasks(100, runnable);
        Thread.sleep(100000);
    }
}
