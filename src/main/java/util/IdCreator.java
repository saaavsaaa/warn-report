package util;

import org.apache.commons.lang3.StringUtils;
import util.type.ByteUtil;
import util.type.LongUtil;

import java.text.ParseException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by aaa on 18-3-27.
 */
public enum IdCreator {

    INSTANCE;
    
    private long serverId;
    private static final ThreadLocal<Long> ticHolder = new ThreadLocal<>();
    private static LongAdder increment = new LongAdder();
    
    IdCreator(){
        serverId = getServerId();
    }
    
    
    public static void main(String[] args) throws ParseException, InterruptedException {
//        System.out.println(IdGenerator.INSTANCE.createNewKey());;
        System.out.println(IdCreator.INSTANCE.calculateKey());
//        System.out.println(DateUtil.getTick("2018-03-27 00:01:01 321"));
//        System.out.println(System.currentTimeMillis());
//        System.out.println(getTick());
    }
    
    /*
    * 能短的尽量短，long就那么长，如果线程id=1000，生成速度就无法忍受了，短的时候要比UUID快很多
    */
    public synchronized long calculateKey() throws ParseException, InterruptedException {
        long threadId = getThreadId();
        long tick = getTick();
        long index = getIncrement(tick);
        
        long key = calculate(new long[]{index, tick, threadId, serverId});
        
        if (key < 0){
            Thread.sleep(1);
            key = calculateKey();
        }
        System.out.println(key);
        return key;
    }
    
    private static long calculate(long[] values){
        long step = values[0];
        for (int i = 1; i < values.length; i++) {
            step += power(values[i], LongUtil.getSize(step));
        }
        if (step < 0){
//            System.out.println("0:" + values[0] + ",1:" + values[1] + ",2:" + values[2] + ",3:" + values[3]);
        }
        return step;
    }
    
    private static long power(long v, long s){
        return v * (long)Math.pow(10, s);
    }
    
    private static long getServerId(){
        // ResourceBundle caches the value in Thread
        ResourceBundle bundle = ResourceBundle.getBundle("server");
        String serverId = bundle.getString("server.id");
        if (StringUtils.isEmpty(serverId)){
            throw new IllegalArgumentException("server.id doesn't exist");
        }
        Long id = Long.valueOf(serverId);
        return id.longValue();
    }
    
    private static long getIncrement(){
        increment.increment();
        return increment.longValue();
    }
    
    private static long getIncrement(long tick){
        if (ticHolder.get() != tick) {
            ticHolder.remove();
            increment.reset();
        }
        return getIncrement();
    }
    
    private static long getThreadId(){
        return Thread.currentThread().getId();
    }
    
    private static long getTick(){
        long tick = System.currentTimeMillis();
        if (null == ticHolder.get()) {
            ticHolder.set(tick);
        }
        return tick;
    }
    
    public synchronized long createNewKey(){
        KeyCreator keyCreator = new KeyCreator();
        keyCreator.appendLong(getServerId());
        keyCreator.appendLong(getIncrement());
        keyCreator.appendLong(getTick());
        keyCreator.appendLong(getThreadId());
        return keyCreator.getLong();
    }
}

class KeyCreator {
    private StringBuilder stringBuilder;
    
    public KeyCreator(){
        stringBuilder = new StringBuilder();
    }
    
    public KeyCreator appendLong(long value){
//        stringBuilder.append(value);
        System.out.println("value:" + value);
        ByteUtil.put(value);
        return this;
    }
    
    public <T> KeyCreator append(T value){
        stringBuilder.append(value);
        return this;
    }
    
    public long getLong(){
        return ByteUtil.get();
    }
    
    public String get(){
        return stringBuilder.toString();
    }
}
