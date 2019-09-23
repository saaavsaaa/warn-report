package video.domain;

/**
 * Author: dreamer-1
 * Date: 2018/5/7
 * Time: 16:32
 * Description: 视频数据基本信息类
 */
public class VideoMetaInfo extends MetaInfo {
    /**
     * 视频（帧）宽度 ，单位为px
     */
    private Integer width;
    /**
     * 视频（帧）高度 ，单位为px
     */
    private Integer height;

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

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

    public String getEncoder() {
        return encoder;
    }

    public void setEncoder(String encoder) {
        this.encoder = encoder;
    }

    public Float getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(Float frameRate) {
        this.frameRate = frameRate;
    }

    public MusicMetaInfo getMusicMetaInfo() {
        return musicMetaInfo;
    }

    public void setMusicMetaInfo(MusicMetaInfo musicMetaInfo) {
        this.musicMetaInfo = musicMetaInfo;
    }

    /**
     * 视频时长 ,单位：毫秒
     */
    private Long duration;
    /**
     * 比特率，单位：Kb/s
     * 指视频每秒传送（包含）的比特数
     */
    private Integer bitRate;
    /**
     * 编码器
     */
    private String encoder;
    /**
     * 帧率，单位：FPS（Frame Per Second）
     * 指视频每秒包含的帧数
     */
    private Float frameRate;
    /**
     * 视频中包含的音频信息
     */
    private MusicMetaInfo musicMetaInfo;
}
