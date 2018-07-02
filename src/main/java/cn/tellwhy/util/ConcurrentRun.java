package cn.tellwhy.util;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ldb on 2016/6/2.
 */
public class ConcurrentRun {
    private static final AtomicInteger count = new AtomicInteger();
    
    public static void executeTasks(int threadCounts, final Runnable task) throws InterruptedException {
//        final AtomicInteger count = new AtomicInteger();
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(threadCounts);
        for (int i = 0; i < threadCounts; i++) {
            /*Thread thread = new Thread(){
                public void run() {
                    try {
                        //当前线程开始等待
                        startGate.await();
                        try{
                            task.run();
                            System.out.println(count.incrementAndGet());
                        }
                        finally{
                            /*//***注意子线程的countDown一定要保证能执行到,因为异常在主线程catch不到***
                            endGate.countDown();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();*/
            startThread(startGate, endGate, task);
        }

        long start = System.nanoTime();
        startGate.countDown();
        //等待n个完成
        endGate.await();
        long end = System.nanoTime();
        long result = end - start;
        long p = result / (1000 * 1000);
        System.out.println(p + "毫秒");
//        System.out.println(end - start);
    }
    
    
    public static void executeTasks(final List<Runnable> tasks) throws InterruptedException {
        int threadCounts = tasks.size();

        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(threadCounts);
    
        tasks.forEach((t) -> {
            startThread(startGate, endGate, t);
        });
        
        long start = System.nanoTime();
        startGate.countDown();
        //等待n个完成
        endGate.await();
        long end = System.nanoTime();
        long result = end - start;
        long p = result / (1000 * 1000);
        System.out.println(p + "毫秒");
//        System.out.println(end - start);
    }
    
    private static Thread startThread(final CountDownLatch startGate, final CountDownLatch endGate, final Runnable task){
         Thread thread = new Thread(){
            public void run() {
                try {
                    //当前线程开始等待
                    startGate.await();
                    try{
                        task.run();
//                        System.out.println(count.incrementAndGet());
                    }
                    finally{
                        //***注意子线程的countDown一定要保证能执行到,因为异常在主线程catch不到***
                        endGate.countDown();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        return thread;
    }
    
    public static void sleepCurrentThread(long s){
        try {
            Thread.sleep(s * 1000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
