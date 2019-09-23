package video.domain.enums;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: dreamer-1
 * Date: 2018/5/20
 * Time: 上午11:25
 * Description: 视频压缩时采用x264编码器时需要指定的压缩速率值，压缩速度越快，压缩率越低
 */
public enum PresetVauleEnum {
    MAX_FAST_ZIP_SPEED("最快压缩速度，最低压缩率", "faster"),
    SECOND_FAST_ZIP_SPEED("第二快的压缩速度", "fast"),
    MEDIUM_ZIP_SPEED("中等压缩速度", "medium"),
    SLOW_ZIP_SPEED("低压缩速度", "slow"),
    SLOWER_ZIP_SPEED("最慢压缩速度", "slower");

    private String presetName;
    private String presetValue;
    private static Set<String> TYPE_VALUE_SET = new HashSet<String>();

    static {
        PresetVauleEnum[] types = PresetVauleEnum.values();
        if (null != types) {
            for (PresetVauleEnum type : types) {
                TYPE_VALUE_SET.add(type.getPresetValue());
            }
        }
    }

    PresetVauleEnum(String presetName, String presetValue) {
        this.presetName = presetName;
        this.presetValue = presetValue;
    }

    public static boolean isValid(String typeValue) {
        if (TYPE_VALUE_SET.contains(typeValue)) {
            return true;
        }
        return false;
    }

    public static PresetVauleEnum convertoEnum(String typeValue) {
        if (!isValid(typeValue)) {
            return null;
        }
        for (PresetVauleEnum type : PresetVauleEnum.values()) {
            if (typeValue.equals(type.getPresetValue())) {
                return type;
            }
        }
        return null;
    }

    public boolean isEqual(String typeValue) {
        if (typeValue == null) {
            return false;
        }
        return this.getPresetValue().equals(typeValue);
    }

    public String getPresetName() {
        return presetName;
    }

    public void setPresetName(String presetName) {
        this.presetName = presetName;
    }

    public String getPresetValue() {
        return presetValue;
    }

    public void setPresetValue(String presetValue) {
        this.presetValue = presetValue;
    }

    public static Set<String> getTypeValueSet() {
        return TYPE_VALUE_SET;
    }

    public static void setTypeValueSet(Set<String> typeValueSet) {
        TYPE_VALUE_SET = typeValueSet;
    }
}
