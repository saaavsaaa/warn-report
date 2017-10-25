package run;

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
        CurrentTrace.INSTANCE.startWatchTrace();
        Thread.sleep(3000);
        CurrentTrace.INSTANCE.started("aaaaaaa");
        System.out.println(CurrentTrace.INSTANCE.getTraceIdHolder().size());
        Thread.sleep(3000);
        CurrentTrace.INSTANCE.success("aaaaaaa");
    }
    
    CurrentTrace(){
        startWatchTrace();
    }
    
    private final Integer start = 0;
    private final Integer success = 2;
    private final Integer fail = 1;
    
    private final Map<String, Integer> traceIdHolder = new ConcurrentHashMap<>();
    
    public Map<String, Integer> getTraceIdHolder(){
        return traceIdHolder;
    }
    
    public synchronized boolean started(String traceId) {
        if (!traceIdHolder.containsKey(traceId)) {
            traceIdHolder.put(traceId, start);
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
    
    private synchronized void stop(){
        System.out.println("count:" + traceIdHolder.size());
        if (traceIdHolder.isEmpty()){
            return;
        }
        traceIdHolder.forEach(
                (k, v)->{
                    if (v == success){
                        traceIdHolder.remove(k);
                        System.out.println("del:" + k);
                    }
                }
        );
    }
    
    public void startWatchTrace() {
        ScheduledExecutorService scheduledExecutorService = Executors
                .newSingleThreadScheduledExecutor(r -> new Thread(r, "Current-Trace-Stop"));
        
        scheduledExecutorService.scheduleAtFixedRate(() -> stop(), 3000, 1000, TimeUnit.MILLISECONDS);
    }
}
