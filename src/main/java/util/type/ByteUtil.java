package util.type;

import java.nio.ByteBuffer;

/**
 * Created by aaa on 18-3-26.
 */
public class ByteUtil {
    private static ByteBuffer buffer = ByteBuffer.allocate(64);
    private static final ThreadLocal<ByteBuffer> holder = new ThreadLocal();
    
    public static void put(long x) {
        if (holder.get() == null){
            holder.set(ByteBuffer.allocate(32));
        }
//        int index = buffer.arrayOffset();
//        buffer.putLong(index, x);
        holder.get().putLong(x);
    }
    
    public static long get(){
        long result = holder.get().getLong(0);
        holder.remove();
        return result;
    }
    //byte 数组与 long 的相互转换
    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }
    
    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }
}
