package video.domain;

/**
 * Author: dreamer-1
 * Date: 2018/5/7
 * Time: 16:30
 * Description: 多媒体数据（包含图片，视频，音频等）的基本信息类
 */
public class MetaInfo {

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * 多媒体的大小，指的是存储体积，单位为B
     * （某些系统返回的大小可能为0）
     */
    private Long size;
    /**
     * 格式
     */
    private String format;


}
