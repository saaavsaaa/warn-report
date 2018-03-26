package util;

import org.apache.commons.lang3.StringUtils;
import util.type.ByteUtil;
import util.type.LongUtil;
import util.type.StringCreator;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by root on 17-4-25.
 */
public enum  IdGenerator {
    INSTANCE;
    
    private String host;
    IdGenerator(){
        host = getHost();
    }
    
    public static void main(String[] args){
//        System.out.println(IdGenerator.INSTANCE.createNewKey());;
        System.out.println(calculateKey());
    }
    
    public static synchronized long calculateKey(){
        long serverId = getServerId();
        long incr = getIncr();
        long tick = getTick();
        long threadId = getThreadId();
        
        int serverIdSize = LongUtil.getSize(serverId);
        int incrSize = LongUtil.getSize(incr);
        int tickSize = LongUtil.getSize(tick);
        int threadIdSize = LongUtil.getSize(threadId);
        
        long key = calculate(new long[]{serverId, incr, tick, threadId}, new long[]{serverIdSize, incrSize, tickSize, threadIdSize}, threadId, 0);
        return key;
    }
    
    private static long calculate(long[] values, long[] sizes, long step, int vi){
        int length = values.length;
        if (length > vi + 1){
            step += power(values[vi + 1], sizes[vi]);
            return calculate(values, sizes, step, vi + 1);
        } else {
            return step;
        }
    }
    
    private static long power(long v, long s){
        return v * (long)Math.pow(10, s);
    }
    
    public synchronized long createNewKey(){
        KeyCreator stringBuilder = new KeyCreator();
        stringBuilder.appendLong(getServerId());
        stringBuilder.appendLong(getIncr());
        stringBuilder.appendLong(getTick());
        stringBuilder.appendLong(getThreadId());
//        return stringBuilder.get();
        return stringBuilder.getLong();
    }
    
    public synchronized String createNewId(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(host);
        stringBuilder.append("-");
        stringBuilder.append(getIncr());
        stringBuilder.append("-");
        stringBuilder.append(getTick());
        stringBuilder.append("-");
        stringBuilder.append(getThreadId());
        return stringBuilder.toString();
    }
    
    private static LongAdder incr = new LongAdder();
    private static long getIncr(){
        incr.increment();
        return incr.longValue();
    }
    
    private static long getThreadId(){
        return Thread.currentThread().getId();
    }
    
    private static long getTick(){
        return System.currentTimeMillis();
    }
    
    private static long getServerId(){
        ResourceBundle bundle = ResourceBundle.getBundle("server");
        String serverId = bundle.getString("server.id");
        if (StringUtils.isEmpty(serverId)){
            throw new IllegalArgumentException("server.id doesn't exist");
        }
        Long id = Long.valueOf(serverId);
        return id.longValue();
    }
    
    private String getHost() {
        Enumeration<NetworkInterface> ni = null;
        try {
            ni = NetworkInterface.getNetworkInterfaces();
            NetworkInterface netCard = ni.nextElement();
            byte[] bytes = netCard.getHardwareAddress();
            if(netCard != null && bytes != null && bytes.length == 6){
                StringBuffer sb = new StringBuffer().append(bytes[0]).append(bytes[1]).append(bytes[2]).append(bytes[3])
                        .append(bytes[4]).append(bytes[5]);
    
                /*StringBuffer sb = new StringBuffer().append(toHex(bytes[0])).append(toHex(bytes[1])).append(toHex(bytes[2]))
                        .append(toHex(bytes[3])).append(toHex(bytes[4])).append(toHex(bytes[5]));*/
                
                return sb.toString().toUpperCase();
            }
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
        return "12700001";
    }
    
    private static String toHex(byte b){
        //与11110000作按位与运算以便读取当前字节高4位
        String high = Integer.toHexString((b&240)>>4);
        //与00001111作按位与运算以便读取当前字节低4位
        String low = Integer.toHexString(b&15);
        return high + low;
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
