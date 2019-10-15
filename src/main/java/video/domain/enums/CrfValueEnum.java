package video.domain.enums;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: dreamer-1
 * Date: 2018/5/26
 * Time: 下午6:22
 * Description: 视频压缩时采用x264编码器时需要指定的视频质量值
 */
public enum CrfValueEnum {
    LOW_QUALITY("低质量", 28),
    MEDIUM_QUALITY("中等质量", 26),
    HIGH_QUALITY("高质量", 23);

    private String name;
    private Integer code;
    private static Set<Integer> TYPE_CODE_SET = new HashSet<Integer>();

    static {
        CrfValueEnum[] types = CrfValueEnum.values();
        if (null != types) {
            for (CrfValueEnum type : types) {
                TYPE_CODE_SET.add(type.getCode());
            }
        }
    }

    CrfValueEnum(String typeName, Integer typeCode) {
        this.name = typeName;
        this.code = typeCode;
    }

    public static boolean isValid(Integer typeCode) {
        if (TYPE_CODE_SET.contains(typeCode)) {
            return true;
        }
        return false;
    }

    public static CrfValueEnum convertoEnum(Integer typeCode) {
        if (!isValid(typeCode)) {
            return null;
        }
        for (CrfValueEnum type : CrfValueEnum.values()) {
            if (typeCode.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public boolean isEqual(Integer typeCode) {
        if (typeCode == null) {
            return false;
        }
        return this.getCode().equals(typeCode);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static Set<Integer> getTypeCodeSet() {
        return TYPE_CODE_SET;
    }

    public static void setTypeCodeSet(Set<Integer> typeCodeSet) {
        TYPE_CODE_SET = typeCodeSet;
    }
}
