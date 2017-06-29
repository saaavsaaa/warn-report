package util;

import java.util.Arrays;

/**
 * Created by aaa on 17-6-29.
 */
public abstract class StringUtil {
    public static String padPrx(String source, int addLength, char addChar){
        if (addLength == 0) {
            return source;
        }
        int len = source.length();

//        char dest[] = Arrays.copyOf(buildByChar(addLength, addChar), len + addLength);
        char dest[] = buildSpaceByChar(len, addLength, addChar);
        System.arraycopy(source.toCharArray(), 0, dest, addLength, len);
        return new String(dest);
    }
    
    public static char[] buildByChar(int addLength, char addChar){
        char[] dest = new char[addLength];
        for (int i = 0; i < addLength; i++) {
            dest[i] = addChar;
        }
        return dest;
    }
    
    public static char[] buildSpaceByChar(int len, int addLength, char addChar){
        char[] dest = new char[len + addLength];
        for (int i = 0; i < addLength; i++) {
            dest[i] = addChar;
        }
        return dest;
    }
}
