package video.domain;

/**
 * Author: dreamer-1
 * Date: 2018/5/11
 * Time: 11:28
 * Description: 音频数据的基本信息
 */
public class MusicMetaInfo extends MetaInfo {
    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getBitRate() {
        return bitRate;
    }

    public void setBitRate(Integer bitRate) {
        this.bitRate = bitRate;
    }

    public Long getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Long sampleRate) {
        this.sampleRate = sampleRate;
    }

    /**
     * 音频时长 ,单位：毫秒
     */
    private Long duration;
    /**
     * 比特率，单位：Kb/s
     * 指音频每秒传送（包含）的比特数
     */
    private Integer bitRate;

    /**
     * 采样频率，单位：Hz
     * 指一秒钟内对声音信号的采样次数
     */
    private Long sampleRate;
}
