package cn.tellwhy.run;

import cn.tellwhy.util.ConcurrentRun;
import cn.tellwhy.util.IdGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by aaa on 17-9-5.
 */
public enum CurrentTrace {
    INSTANCE;
    
    CurrentTrace() {
        startWatchTrace();
    }
    
    private final Integer start = 0;
    private final Integer fail = 1;
    private final Integer success = 2;
    private final Integer stop = 3;
    
    private final Map<String, Integer> traceIdHolder = new ConcurrentHashMap<>();
    
    public synchronized boolean started(String traceId) {
        if (!traceIdHolder.containsKey(traceId)) {
            traceIdHolder.put(traceId, start);
            System.out.println(System.currentTimeMillis() + " start:" + traceId);
            return false;
        }
        return traceIdHolder.get(traceId) == fail;
    }
    
    public synchronized void fail(String traceId) {
        System.out.println("fail : " + traceId);
        traceIdHolder.replace(traceId, fail);
    }
    
    public synchronized void success(String traceId) {
        System.out.println("success : " + traceId);
        traceIdHolder.replace(traceId, success);
    }
    
    private synchronized void stop() {
        if (traceIdHolder.isEmpty()) {
            System.out.println("current holder break");
            return;
        }
        traceIdHolder.forEach(
                (k, v) -> {
                    if (v == success) {
                        traceIdHolder.replace(k, stop);
                        System.out.println(System.currentTimeMillis() + " stop:" + k);
                    } else if (v == stop) {
                        traceIdHolder.remove(k);
                        System.out.println(System.currentTimeMillis() + " delete:" + k);
                    }
                }
        );
    }
    
    private void startWatchTrace() {
        ScheduledExecutorService scheduledExecutorService =
                Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Current-Trace-Stop"));
        
        scheduledExecutorService.scheduleAtFixedRate(() -> stop(), 1000, 6000, TimeUnit.MILLISECONDS);
    }
}
