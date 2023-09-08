package util;

import cn.tellwhy.structure.ArrayListExtend;
import cn.tellwhy.util.type.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static util.PinYin.toPinyin;

public class Phones {

    final static Map<String,String> phones = FileUtil.readPhones(new Phones().getClass().getResource("/thchs30-phone/pinyin2phone.tone.txt").getPath());

    public static void main(String[] args) {
        String pinyin = toPinyin("绿");
        System.out.println(toPhones(pinyin));
    }

    public static String toPhones(final String pinyin) {
        String[] pinyin_s = pinyin.split(" ");
        String result = "";
        for (String each : pinyin_s) {
            if (StringUtils.isNotBlank(each)) {
                String phone = phones.get(each.toUpperCase());
                if (phone == null) {
                    throw new IllegalArgumentException("illegal pin yin!");
                }
                result += phone + " ";
            }
        }
        return result;
    }

    // 声母 b p m f d t n l g k h j q x zh ch sh r z c s y w
    private static Set<String> pre_pinyin() {
        Set<String> result = new HashSet<>();
        List<String> list = new ArrayListExtend().addInThis("b").addInThis("p").addInThis("m")
                .addInThis("f").addInThis("d").addInThis("t")
                .addInThis("n").addInThis("l").addInThis("g")
                .addInThis("k").addInThis("h").addInThis("j")
                .addInThis("q").addInThis("x").addInThis("zh")
                .addInThis("ch").addInThis("sh").addInThis("r")
                .addInThis("z").addInThis("c").addInThis("s")
                .addInThis("y").addInThis("w");
        result.addAll(list);
        return result;
    }

    //单韵母 a o e i u ü
    private static Set<String> s_pinyin() {
        Set<String> s = new HashSet<>();
        List<String> sl = new ArrayListExtend().addInThis("a").addInThis("o").addInThis("e")
                .addInThis("i").addInThis("u").addInThis("v");// ü
        s.addAll(sl);
        return s;
    }

    //复韵母 ai ei ui ao ou iu ie üe er
    private static Set<String> d_pinyin() {
        Set<String> d = new HashSet<>();
        List<String> dlist = new ArrayListExtend().addInThis("ai").addInThis("ei").addInThis("ui")
                .addInThis("ao").addInThis("ou").addInThis("iu")
                .addInThis("ie").addInThis("er").addInThis("ve"); // üe
        d.addAll(dlist);
        return d;
    }

    //鼻韵母 an en in un ün ang eng ing ong
    private static Set<String> bi_pinyin() {
        Set<String> result = new HashSet<>();
        List<String> list = new ArrayListExtend().addInThis("an").addInThis("en").addInThis("in").addInThis("un")
                .addInThis("ang").addInThis("eng")
                .addInThis("ing").addInThis("ong"); // ün 只能与j q x y拼读
        result.addAll(list);
        return result;
    }
}
