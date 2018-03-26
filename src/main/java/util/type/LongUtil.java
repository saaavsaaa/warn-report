package util.type;

/**
 * Created by aaa on 18-3-26.
 */
public class LongUtil {
    final static long [] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE };
    
    public static int getSize(long value){
        if (value <= Integer.MAX_VALUE){
            return intSize(value);
        } else {
            return longSize(value);
        }
    }
    
    private static int longSize(long x) {
        long p = 10;
        for (int i=1; i<19; i++) {
            if (x < p) {
                return i;
            }
            p = 10*p;
        }
        return 19;
    }
    
    private static int intSize(long x) {
        for (int i=0; ; i++)
            if (x <= sizeTable[i]) {
                return i + 1;
            }
    }
}
