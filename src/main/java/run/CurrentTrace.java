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
        CurrentTrace.INSTANCE.started("aaaaaaa");
        System.out.println(CurrentTrace.INSTANCE.getTraceIdHolder().size());
        Thread.sleep(3000);
        CurrentTrace.INSTANCE.success("aaaaaaa");
    }
    
    CurrentTrace(){
        startWatchTrace();
    }
    
    private final Integer start = 0;
    private final Integer fail = 1;
    private final Integer success = 2;
    private final Integer stop = 3;
    
    private final Map<String, Integer> traceIdHolder = new ConcurrentHashMap<>();
    
    public Map<String, Integer> getTraceIdHolder(){
        return traceIdHolder;
    }
    
    public synchronized boolean started(String traceId) {
        if (!traceIdHolder.containsKey(traceId)) {
            traceIdHolder.put(traceId, start);
            System.out.println("start:" + traceIdHolder.size());
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
        System.out.println(System.currentTimeMillis() + " stop count:" + traceIdHolder.size());
        if (traceIdHolder.isEmpty()){
            return;
        }
        traceIdHolder.forEach(
                (k, v)->{
                    if (v == success){
                        traceIdHolder.replace(k, stop);
                        System.out.println(System.currentTimeMillis() + " stop:" + k);
                    } else if (v == stop){
                        traceIdHolder.remove(k);
                        System.out.println(System.currentTimeMillis() + " del:" + k);
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
