package run;

import util.ConcurrentRun;
import util.IdGenerator;
import util.size.ClassIntrospection;
import util.size.ObjectInfo;

import java.util.ArrayList;
import java.util.List;
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
        
        Runnable retry = () -> {
            while (!traceIds.isEmpty()) {
                traceIds.forEach(traceId -> {
                            if (CurrentTrace.INSTANCE.started(traceId)) {
                                System.out.println("Already started : " + traceId);
                                return;
                            }
                            
                            if (System.currentTimeMillis() % 2 == 0) {
                                CurrentTrace.INSTANCE.success(traceId);
                            } else {
                                CurrentTrace.INSTANCE.fail(traceId);
                            }
                            
                            ConcurrentRun.sleepCurrentThread(6);
                        }
                );
            }
        };
        ConcurrentRun.executeTasks(10, retry);
    }
    
    private void printSize() {
        final ClassIntrospection ci = new ClassIntrospection();
        
        ObjectInfo res;
        
        try {
            res = ci.introspect(traceIdHolder);
            System.out.println("size:" + res.getDeepSize());
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }
    
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
        traceIdHolder.replace(traceId, fail);
    }
    
    public synchronized void success(String traceId) {
        traceIdHolder.replace(traceId, success);
    }
    
    private synchronized void stop() {
        if (traceIdHolder.isEmpty()) {
            return;
        }
        traceIdHolder.forEach(
                (k, v) -> {
                    if (v == success) {
                        traceIdHolder.replace(k, stop);
                        System.out.println(System.currentTimeMillis() + " stop:" + k);
                    } else if (v == stop) {
                        traceIdHolder.remove(k);
                        System.out.println(System.currentTimeMillis() + " del:" + k);
                    }
                }
        );
        this.printSize();
    }
    
    private void startWatchTrace() {
        ScheduledExecutorService scheduledExecutorService =
                Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Current-Trace-Stop"));
        
        scheduledExecutorService.scheduleAtFixedRate(() -> stop(), 1000, 6000, TimeUnit.MILLISECONDS);
    }
}
