package util;

import org.apache.commons.lang3.StringUtils;
import util.type.ByteUtil;
import util.type.DateUtil;
import util.type.LongUtil;
import util.type.StringCreator;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
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
    
    public synchronized String createNewId(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(host);
        stringBuilder.append("-");
        stringBuilder.append(getIncrement());
        stringBuilder.append("-");
        stringBuilder.append(getTick());
        stringBuilder.append("-");
        stringBuilder.append(getThreadId());
        return stringBuilder.toString();
    }
    
    private static LongAdder increment = new LongAdder();
    private static long getIncrement(){
        increment.increment();
        return increment.longValue();
    }
    
    private static long getThreadId(){
        return Thread.currentThread().getId();
    }
    
    private static long getTick(){
        return System.currentTimeMillis();
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
