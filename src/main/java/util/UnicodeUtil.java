package util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaa on 17-12-6.
 */
public class UnicodeUtil {
    //中文转Unicode
    public static String gbEncoding(final String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
            String hexB = Integer.toHexString(utfBytes[byteIndex]);   //转换为16进制整型字符串
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        System.out.println("unicodeBytes is: " + unicodeBytes);
        return unicodeBytes;
    }
    
    //Unicode转中文
    public static String decodeUnicode(final String txt) {
        int start = 0;
        int current = 0;
        List<String> letters = new ArrayList<>();
        String result  = "";
        while (start > -1) {
            start = txt.indexOf("\\u", current);
            
            if (start == -1){
                String last = txt.substring(current);
                result += last;
                letters.add(last);
            } else if (start == current){
                char letter = getChineseCharacter(txt, start);
                letters.add(String.valueOf(letter));
                current = start + 6;
                result += letter;
            } else if (start > current){
                String interlayer = txt.substring(current, start);
                result += interlayer;
                current = start;
            }
        }
        return result;
    }
    
    private static char getChineseCharacter(String txt, int index){
        char hex1 = txt.charAt(index + 2);
        char hex2 = txt.charAt(index + 3);
        char hex3 = txt.charAt(index + 4);
        char hex4 = txt.charAt(index + 5);
        return  (char) Integer.parseInt(new String(new char[]{hex1, hex2, hex3, hex4}), 16);
    }
}
