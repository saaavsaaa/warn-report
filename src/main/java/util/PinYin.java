package util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PinYin {

    public static void main(String[] args) {
        String path = "D:\\share\\chinese_speech\\thchs30\\data_thchs30\\data\\A2_0.wav.trn"; // "D:\\share\\chinese_speech\\test.txt"
        String txt = readFileContent(path);
        System.out.println(toPinyin(txt));
        System.out.println(toPinyin("绿"));
    }

    /**
     * net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination: tone marks cannot be added to v or u:
     */
    public static String toPinyin(final String chinese){
        String pinyinStr = "";
        char[] newChar = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] > 128) {
                try {
                    pinyinStr += PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0] + " ";
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            }else{
                pinyinStr += newChar[i];
            }
        }
        return pinyinStr;
    }

    public static String readFileContent(final String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

    public static void toPhones(final String pinyin) {
        Set<String> s = s_pinyin();

    }

    /*
    * 声母
b p m f d t n l g k h j q x zh ch sh r z c s y w
单韵母
a o e i u ü
复韵母
　　ai ei ui ao ou iu ie üe er
鼻韵母
an en in un ün ang eng ing ong
    * */
    private static Set<String> s_pinyin() {
        Set<String> s = new HashSet<>();
        s.add("");

        return s;
    }
}
