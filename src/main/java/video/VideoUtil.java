package video;

import video.domain.ImageMetaInfo;
import video.domain.MusicMetaInfo;
import video.domain.VideoMetaInfo;
import video.domain.gif.AnimatedGifEncoder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于FFmpeg内核来编解码音视频信息；
 * 使用前需手动在运行环境中安装FFmpeg运行程序，然后正确设置FFmpeg运行路径后MediaUtil.java才能正常调用到FFmpeg程序去处理音视频；
 * https://www.cnblogs.com/Dreamer-1/p/10394011.html
 * Author: dreamer-1
 *
 * version: 1.0
 *
 */
public class VideoUtil {

    /**
     * 可以处理的视频格式
     */
    public final static String[] VIDEO_TYPE = { "MP4", "WMV" };
    /**
     * 可以处理的图片格式
     */
    public final static String[] IMAGE_TYPE = { "JPG", "JPEG", "PNG", "GIF" };
    /**
     * 可以处理的音频格式
     */
    public final static String[] AUDIO_TYPE = { "AAC" };

    /**
     * 视频帧抽取时的默认时间点，第10s（秒）
     * （Time类构造参数的单位:ms）
     */
    private static final Time DEFAULT_TIME = new Time(0, 0, 10);
    /**
     * 视频帧抽取的默认宽度值，单位：px
     */
    private static int DEFAULT_WIDTH = 320;
    /**
     * 视频帧抽取的默认时长，单位：s（秒）
     */
    private static int DEFAULT_TIME_LENGTH = 10;
    /**
     * 抽取多张视频帧以合成gif动图时，gif的播放速度
     */
    private static int DEFAULT_GIF_PLAYTIME = 110;
    /**
     * FFmpeg程序执行路径
     * 当前系统安装好ffmpeg程序并配置好相应的环境变量后，值为ffmpeg可执行程序文件在实际系统中的绝对路径
     */
    private static String FFMPEG_PATH = "/usr/bin/ffmpeg"; // /usr/bin/ffmpeg


    /**
     * 视频时长正则匹配式
     * 用于解析视频及音频的时长等信息时使用；
     *
     * (.*?)表示：匹配任何除\r\n之外的任何0或多个字符，非贪婪模式
     *
     */
    private static String durationRegex = "Duration: (\\d*?):(\\d*?):(\\d*?)\\.(\\d*?), start: (.*?), bitrate: (\\d*) kb\\/s.*";
    private static Pattern durationPattern;
    /**
     * 视频流信息正则匹配式
     * 用于解析视频详细信息时使用；
     */
    private static String videoStreamRegex = "Stream #\\d:\\d[\\(]??\\S*[\\)]??: Video: (\\S*\\S$?)[^\\,]*, (.*?), (\\d*)x(\\d*)[^\\,]*, (\\d*) kb\\/s, (\\d*[\\.]??\\d*) fps";
    private static Pattern videoStreamPattern;
    /**
     * 音频流信息正则匹配式
     * 用于解析音频详细信息时使用；
     */
    private static String musicStreamRegex = "Stream #\\d:\\d[\\(]??\\S*[\\)]??: Audio: (\\S*\\S$?)(.*), (.*?) Hz, (.*?), (.*?), (\\d*) kb\\/s";;
    private static Pattern musicStreamPattern;

    /**
     * 静态初始化时先加载好用于音视频解析的正则匹配式
     */
    static {
        durationPattern = Pattern.compile(durationRegex);
        videoStreamPattern = Pattern.compile(videoStreamRegex);
        musicStreamPattern = Pattern.compile(musicStreamRegex);
    }

    /**
     * 获取当前多媒体处理工具内的ffmpeg的执行路径
     * @return
     */
    public static String getFFmpegPath() {
        return FFMPEG_PATH;
    }

    /**
     * 设置当前多媒体工具内的ffmpeg的执行路径
     * @param ffmpeg_path ffmpeg可执行程序在实际系统中的绝对路径
     * @return
     */
    public static boolean setFFmpegPath(String ffmpeg_path) {
        if (StringUtils.isBlank(ffmpeg_path)) {
            System.out.println("--- 设置ffmpeg执行路径失败，因为传入的ffmpeg可执行程序路径为空！ ---");
            return false;
        }
        File ffmpegFile = new File(ffmpeg_path);
        if (!ffmpegFile.exists()) {
            System.out.println("--- 设置ffmpeg执行路径失败，因为传入的ffmpeg可执行程序路径下的ffmpeg文件不存在！ ---");
            return false;
        }
        FFMPEG_PATH = ffmpeg_path;
        System.out.println("--- 设置ffmpeg执行路径成功 --- 当前ffmpeg可执行程序路径为： " + ffmpeg_path);
        return true;
    }

    /**
     * 测试当前多媒体工具是否可以正常工作
     * @return
     */
    public static boolean isExecutable() {
        File ffmpegFile = new File(FFMPEG_PATH);
        if (!ffmpegFile.exists()) {
            System.out.println("--- 工作状态异常，因为传入的ffmpeg可执行程序路径下的ffmpeg文件不存在！ ---");
            return false;
        }
        List<String> cmds = new ArrayList<>(1);
        cmds.add("-version");
        String ffmpegVersionStr = executeCommand(cmds);
        if (StringUtils.isBlank(ffmpegVersionStr)) {
            System.out.println("--- 工作状态异常，因为ffmpeg命令执行失败！ ---");
            return false;
        }
        System.out.println("--- 工作状态正常 ---");
        return true;
    }


    /**
     * 执行FFmpeg命令
     * @param commonds 要执行的FFmpeg命令
     * @return FFmpeg程序在执行命令过程中产生的各信息，执行出错时返回null
     */
    public static String executeCommand(List<String> commonds) {
        if (commonds == null && commonds.isEmpty()) {
            System.out.println("--- 指令执行失败，因为要执行的FFmpeg指令为空！ ---");
            return null;
        }
        LinkedList<String> ffmpegCmds = new LinkedList<>(commonds);
        ffmpegCmds.addFirst(FFMPEG_PATH); // 设置ffmpeg程序所在路径
        System.out.println("--- 待执行的FFmpeg指令为：---" + ffmpegCmds);

        Runtime runtime = Runtime.getRuntime();
        Process ffmpeg = null;
        try {
            // 执行ffmpeg指令
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(ffmpegCmds);
            ffmpeg = builder.start();
            System.out.println("--- 开始执行FFmpeg指令：--- 执行线程名：" + builder.toString());

            // 取出输出流和错误流的信息
            // 注意：必须要取出ffmpeg在执行命令过程中产生的输出信息，如果不取的话当输出流信息填满jvm存储输出留信息的缓冲区时，线程就回阻塞住
            PrintStream errorStream = new PrintStream(ffmpeg.getErrorStream());
            PrintStream inputStream = new PrintStream(ffmpeg.getInputStream());
            errorStream.start();
            inputStream.start();
            // 等待ffmpeg命令执行完
            ffmpeg.waitFor();

            // 获取执行结果字符串
            String result = errorStream.stringBuffer.append(inputStream.stringBuffer).toString();

            // 输出执行的命令信息
            String cmdStr = Arrays.toString(ffmpegCmds.toArray()).replace(",", "");
            String resultStr = StringUtils.isBlank(result) ? "【异常】" : "正常";
            System.out.println("--- 已执行的FFmepg命令： ---" + cmdStr + " 已执行完毕,执行结果： " + resultStr);
            return result;

        } catch (Exception e) {
            System.out.println("--- FFmpeg命令执行出错！ --- 出错信息： " + e.getMessage());
            return null;

        } finally {
            if (null != ffmpeg) {
                ProcessKiller ffmpegKiller = new ProcessKiller(ffmpeg);
                // JVM退出时，先通过钩子关闭FFmepg进程
                runtime.addShutdownHook(ffmpegKiller);
            }
        }
    }


    /**
     * 视频转换
     *
     * 注意指定视频分辨率时，宽度和高度必须同时有值；
     *
     * @param fileInput 源视频路径
     * @param fileOutPut 转换后的视频输出路径
     * @param withAudio 是否保留音频；true-保留，false-不保留
     * @param crf 指定视频的质量系数（值越小，视频质量越高，体积越大；该系数取值为0-51，直接影响视频码率大小）,取值参考：CrfValueEnum.code
     * @param preset 指定视频的编码速率（速率越快压缩率越低），取值参考：PresetVauleEnum.presetValue
     * @param width 视频宽度；为空则保持源视频宽度
     * @param height 视频高度；为空则保持源视频高度
     */
    public static void convertVideo(File fileInput, File fileOutPut, boolean withAudio, Integer crf, String preset, Integer width, Integer height) {
        if (null == fileInput || !fileInput.exists()) {
            throw new RuntimeException("源视频文件不存在，请检查源视频路径");
        }
        if (null == fileOutPut) {
            throw new RuntimeException("转换后的视频路径为空，请检查转换后的视频存放路径是否正确");
        }

        if (!fileOutPut.exists()) {
            try {
                fileOutPut.createNewFile();
            } catch (IOException e) {
                System.out.println("视频转换时新建输出文件失败");
            }
        }

        String format = getFormat(fileInput);
        if (!isLegalFormat(format, VIDEO_TYPE)) {
            throw new RuntimeException("无法解析的视频格式：" + format);
        }

        List<String> commond = new ArrayList<String>();
        commond.add("-i");
        commond.add(fileInput.getAbsolutePath());
        if (!withAudio) { // 设置是否保留音频
            commond.add("-an");  // 去掉音频
        }
        if (null != width && width > 0 && null != height && height > 0) { // 设置分辨率
            commond.add("-s");
            String resolution = width.toString() + "x" + height.toString();
            commond.add(resolution);
        }

        commond.add("-vcodec"); // 指定输出视频文件时使用的编码器
        commond.add("libx264"); // 指定使用x264编码器
        commond.add("-preset"); // 当使用x264时需要带上该参数
        commond.add(preset); // 指定preset参数
        commond.add("-crf"); // 指定输出视频质量
        commond.add(crf.toString()); // 视频质量参数，值越小视频质量越高
        commond.add("-y"); // 当已存在输出文件时，不提示是否覆盖
        commond.add(fileOutPut.getAbsolutePath());

        executeCommand(commond);
    }


    /**
     * 视频帧抽取
     * 默认抽取第10秒的帧画面
     * 抽取的帧图片默认宽度为300px
     *
     * 转换后的文件路径以.gif结尾时，默认截取从第10s开始，后10s以内的帧画面来生成gif
     *
     * @param videoFile 源视频路径
     * @param fileOutPut 转换后的文件路径
     */
    public static void cutVideoFrame(File videoFile, File fileOutPut) {
        cutVideoFrame(videoFile, fileOutPut, DEFAULT_TIME);
    }

    /**
     * 视频帧抽取（抽取指定时间点的帧画面）
     * 抽取的视频帧图片宽度默认为320px
     *
     * 转换后的文件路径以.gif结尾时，默认截取从指定时间点开始，后10s以内的帧画面来生成gif
     *
     * @param videoFile 源视频路径
     * @param fileOutPut 转换后的文件路径
     * @param time 指定抽取视频帧的时间点（单位：s）
     */
    public static void cutVideoFrame(File videoFile, File fileOutPut, Time time) {
        cutVideoFrame(videoFile, fileOutPut, time, DEFAULT_WIDTH);
    }

    /**
     * 视频帧抽取（抽取指定时间点、指定宽度值的帧画面）
     * 只需指定视频帧的宽度，高度随宽度自动计算
     *
     * 转换后的文件路径以.gif结尾时，默认截取从指定时间点开始，后10s以内的帧画面来生成gif
     *
     * @param videoFile 源视频路径
     * @param fileOutPut 转换后的文件路径
     * @param time 指定要抽取第几秒的视频帧（单位：s）
     * @param width 抽取的视频帧图片的宽度（单位：px）
     */
    public static void cutVideoFrame(File videoFile, File fileOutPut, Time time, int width) {
        if (null == videoFile || !videoFile.exists()) {
            throw new RuntimeException("源视频文件不存在，请检查源视频路径");
        }
        if (null == fileOutPut) {
            throw new RuntimeException("转换后的视频路径为空，请检查转换后的视频存放路径是否正确");
        }
        VideoMetaInfo info = getVideoMetaInfo(videoFile);
        if (null == info) {
            System.out.println("--- 未能解析源视频信息，视频帧抽取操作失败 --- 源视频： " + videoFile);
            return;
        }
        int height = width * info.getHeight() / info.getWidth(); // 根据宽度计算适合的高度，防止画面变形
        cutVideoFrame(videoFile, fileOutPut, time, width, height);
    }

    /**
     * 视频帧抽取（抽取指定时间点、指定宽度值、指定高度值的帧画面）
     *
     * 转换后的文件路径以.gif结尾时，默认截取从指定时间点开始，后10s以内的帧画面来生成gif
     *
     * @param videoFile 源视频路径
     * @param fileOutPut 转换后的文件路径
     * @param time 指定要抽取第几秒的视频帧（单位：s）
     * @param width 抽取的视频帧图片的宽度（单位：px）
     * @param height 抽取的视频帧图片的高度（单位：px）
     */
    public static void cutVideoFrame(File videoFile, File fileOutPut, Time time, int width, int height) {
        if (null == videoFile || !videoFile.exists()) {
            throw new RuntimeException("源视频文件不存在，请检查源视频路径");
        }
        if (null == fileOutPut) {
            throw new RuntimeException("转换后的视频路径为空，请检查转换后的视频存放路径是否正确");
        }
        String format = getFormat(fileOutPut);
        if (!isLegalFormat(format, IMAGE_TYPE)) {
            throw new RuntimeException("无法生成指定格式的帧图片：" + format);
        }
        String fileOutPutPath = fileOutPut.getAbsolutePath();
        if (!"GIF".equals(StringUtils.upperCase(format))) {
            // 输出路径不是以.gif结尾，抽取并生成一张静态图
            cutVideoFrame(videoFile, fileOutPutPath, time, width, height, 1, false);
        } else {
            // 抽取并生成一个gif（gif由10张静态图构成）
            String path = fileOutPut.getParent();
            String name = fileOutPut.getName();
            // 创建临时文件存储多张静态图用于生成gif
            String tempPath = path + File.separator + System.currentTimeMillis() + "_" + name.substring(0, name.indexOf("."));
            File file = new File(tempPath);
            if (!file.exists()) {
                file.mkdir();
            }
            try {
                cutVideoFrame(videoFile, tempPath, time, width, height, DEFAULT_TIME_LENGTH, true);
                // 生成gif
                String images[] = file.list();
                for (int i = 0; i < images.length; i++) {
                    images[i] = tempPath + File.separator + images[i];
                }
                createGifImage(images, fileOutPut.getAbsolutePath(), DEFAULT_GIF_PLAYTIME);
            } catch (Exception e) {
                System.out.println("--- 截取视频帧操作出错 --- 错误信息：" + e.getMessage());
            } finally {
                // 删除用于生成gif的临时文件
                String images[] = file.list();
                for (int i = 0; i < images.length; i++) {
                    File fileDelete = new File(tempPath + File.separator + images[i]);
                    fileDelete.delete();
                }
                file.delete();
            }
        }
    }

    /**
     * 视频帧抽取（抽取指定时间点、指定宽度值、指定高度值、指定时长、指定单张/多张的帧画面）
     *
     * @param videoFile 源视频
     * @param path 转换后的文件输出路径
     * @param time 开始截取视频帧的时间点（单位：s）
     * @param width 截取的视频帧图片的宽度（单位：px）
     * @param height 截取的视频帧图片的高度（单位：px，需要大于20）
     * @param timeLength 截取的视频帧的时长（从time开始算，单位:s，需小于源视频的最大时长）
     * @param isContinuty false - 静态图（只截取time时间点的那一帧图片），true - 动态图（截取从time时间点开始,timelength这段时间内的多张帧图）
     */
    private static void cutVideoFrame(File videoFile, String path, Time time, int width, int height, int timeLength, boolean isContinuty) {
        if (videoFile == null || !videoFile.exists()) {
            throw new RuntimeException("源视频文件不存在，源视频路径： ");
        }
        if (null == path) {
            throw new RuntimeException("转换后的文件路径为空，请检查转换后的文件存放路径是否正确");
        }
        VideoMetaInfo info = getVideoMetaInfo(videoFile);
        if (null == info) {
            throw new RuntimeException("未解析到视频信息");
        }
        if (time.getTime() + timeLength > info.getDuration()) {
            throw new RuntimeException("开始截取视频帧的时间点不合法：" + time.toString() + "，因为截取时间点晚于视频的最后时间点");
        }
        if (width <= 20 || height <= 20) {
            throw new RuntimeException("截取的视频帧图片的宽度或高度不合法，宽高值必须大于20");
        }
        try {
            List<String> commond = new ArrayList<String>();
            commond.add("-ss");
            commond.add(time.toString());
            if (isContinuty) {
                commond.add("-t");
                commond.add(timeLength + "");
            } else {
                commond.add("-vframes");
                commond.add("1");
            }
            commond.add("-i");
            commond.add(videoFile.getAbsolutePath());
            commond.add("-an");
            commond.add("-f");
            commond.add("image2");
            if (isContinuty) {
                commond.add("-r");
                commond.add("3");
            }
            commond.add("-s");
            commond.add(width + "*" + height);
            if (isContinuty) {
                commond.add(path + File.separator + "foo-%03d.jpeg");
            } else {
                commond.add(path);
            }

            executeCommand(commond);
        } catch (Exception e) {
            System.out.println("--- 视频帧抽取过程出错 --- 错误信息： " + e.getMessage());
        }
    }

    /**
     * 截取视频中的某一段，生成新视频
     *
     * @param videoFile 源视频路径
     * @param outputFile 转换后的视频路径
     * @param startTime 开始抽取的时间点（单位:s）
     * @param timeLength 需要抽取的时间段（单位:s，需小于源视频最大时长）；例如：该参数值为10时即抽取从startTime开始之后10秒内的视频作为新视频
     */
    public static void cutVideo(File videoFile, File outputFile, Time startTime, int timeLength) {
        if (videoFile == null || !videoFile.exists()) {
            throw new RuntimeException("视频文件不存在：");
        }
        if (null == outputFile) {
            throw new RuntimeException("转换后的视频路径为空，请检查转换后的视频存放路径是否正确");
        }
        VideoMetaInfo info = getVideoMetaInfo(videoFile);
        if (null == info) {
            throw new RuntimeException("未解析到视频信息");
        }
        if (startTime.getTime() + timeLength > info.getDuration()) {
            throw new RuntimeException("截取时间不合法：" + startTime.toString() + "，因为截取时间大于视频的时长");
        }
        try {
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            List<String> commond = new ArrayList<String>();
            commond.add("-ss");
            commond.add(startTime.toString());
            commond.add("-t");
            commond.add("" + timeLength);
            commond.add("-i");
            commond.add(videoFile.getAbsolutePath());
            commond.add("-vcodec");
            commond.add("copy");
            commond.add("-acodec");
            commond.add("copy");
            commond.add(outputFile.getAbsolutePath());
            executeCommand(commond);
        } catch (IOException e) {
            System.out.println("--- 视频截取过程出错 ---");
        }
    }

    /**
     * 抽取视频里的音频信息
     * 只能抽取成MP3文件
     * @param videoFile 源视频文件
     * @param audioFile 从源视频提取的音频文件
     */
    public static void getAudioFromVideo(File videoFile, File audioFile) {
        if (null == videoFile || !videoFile.exists()) {
            throw new RuntimeException("源视频文件不存在： ");
        }
        if (null == audioFile) {
            throw new RuntimeException("要提取的音频路径为空：");
        }
        String format = getFormat(audioFile);
        if (!isLegalFormat(format, AUDIO_TYPE)) {
            throw new RuntimeException("无法生成指定格式的音频：" + format + " 请检查要输出的音频文件是否是AAC类型");
        }
        try {
            if (!audioFile.exists()) {
                audioFile.createNewFile();
            }

            List<String> commond = new ArrayList<String>();
            commond.add("-i");
            commond.add(videoFile.getAbsolutePath());
            commond.add("-vn"); // no video，去除视频信息
            commond.add("-y");
            commond.add("-acodec");
            commond.add("copy");
            commond.add(audioFile.getAbsolutePath());
            executeCommand(commond);
        } catch (Exception e) {
            System.out.println("--- 抽取视频中的音频信息的过程出错 --- 错误信息： " + e.getMessage());
        }
    }

    /**
     * 解析视频的基本信息（从文件中）
     *
     * 解析出的视频信息一般为以下格式：
     * Input #0, mov,mp4,m4a,3gp,3g2,mj2, from '6.mp4':
     * Duration: 00:00:30.04, start: 0.000000, bitrate: 19031 kb/s
     * Stream #0:0(eng): Video: h264 (Main) (avc1 / 0x31637661), yuv420p(tv, bt709), 1920x1080, 18684 kb/s, 25 fps, 25 tbr, 25k tbn, 50 tbc (default)
     * Stream #0:1(eng): Audio: aac (LC) (mp4a / 0x6134706D), 48000 Hz, stereo, fltp, 317 kb/s (default)
     *
     * 注解：
     * Duration: 00:00:30.04【视频时长】, start: 0.000000【视频开始时间】, bitrate: 19031 kb/s【视频比特率/码率】
     * Stream #0:0(eng): Video: h264【视频编码格式】 (Main) (avc1 / 0x31637661), yuv420p(tv, bt709), 1920x1080【视频分辨率，宽x高】, 18684【视频比特率】 kb/s, 25【视频帧率】 fps, 25 tbr, 25k tbn, 50 tbc (default)
     * Stream #0:1(eng): Audio: aac【音频格式】 (LC) (mp4a / 0x6134706D), 48000【音频采样率】 Hz, stereo, fltp, 317【音频码率】 kb/s (default)
     *
     * @param videoFile 源视频路径
     * @return 视频的基本信息，解码失败时返回null
     */
    public static VideoMetaInfo getVideoMetaInfo(File videoFile) {
        if (null == videoFile || !videoFile.exists()) {
            System.out.println("--- 解析视频信息失败，因为要解析的源视频文件不存在 ---");
            return null;
        }

        VideoMetaInfo videoInfo = new VideoMetaInfo();

        String parseResult = getMetaInfoFromFFmpeg(videoFile);

        Matcher durationMacher = durationPattern.matcher(parseResult);
        Matcher videoStreamMacher = videoStreamPattern.matcher(parseResult);
        Matcher videoMusicStreamMacher = musicStreamPattern.matcher(parseResult);

        Long duration = 0L; // 视频时长
        Integer videoBitrate = 0; // 视频码率
        String videoFormat = getFormat(videoFile); // 视频格式
        Long videoSize = videoFile.length(); // 视频大小

        String videoEncoder = ""; // 视频编码器
        Integer videoHeight = 0; // 视频高度
        Integer videoWidth = 0; // 视频宽度
        Float videoFramerate = 0F; // 视频帧率

        String musicFormat = ""; // 音频格式
        Long samplerate = 0L; // 音频采样率
        Integer musicBitrate = 0; // 音频码率

        try {
            // 匹配视频播放时长等信息
            if (durationMacher.find()) {
                long hours = (long)Integer.parseInt(durationMacher.group(1));
                long minutes = (long)Integer.parseInt(durationMacher.group(2));
                long seconds = (long)Integer.parseInt(durationMacher.group(3));
                long dec = (long)Integer.parseInt(durationMacher.group(4));
                duration = dec * 100L + seconds * 1000L + minutes * 60L * 1000L + hours * 60L * 60L * 1000L;
                //String startTime = durationMacher.group(5) + "ms";
                videoBitrate = Integer.parseInt(durationMacher.group(6));
            }
            // 匹配视频分辨率等信息
            if (videoStreamMacher.find()) {
                videoEncoder = videoStreamMacher.group(1);
                String s2 = videoStreamMacher.group(2);
                videoWidth = Integer.parseInt(videoStreamMacher.group(3));
                videoHeight = Integer.parseInt(videoStreamMacher.group(4));
                String s5 = videoStreamMacher.group(5);
                videoFramerate = Float.parseFloat(videoStreamMacher.group(6));
            }
            // 匹配视频中的音频信息
            if (videoMusicStreamMacher.find()) {
                musicFormat = videoMusicStreamMacher.group(1); // 提取音频格式
                //String s2 = videoMusicStreamMacher.group(2);
                samplerate = Long.parseLong(videoMusicStreamMacher.group(3)); // 提取采样率
                //String s4 = videoMusicStreamMacher.group(4);
                //String s5 = videoMusicStreamMacher.group(5);
                musicBitrate = Integer.parseInt(videoMusicStreamMacher.group(6)); // 提取比特率
            }
        } catch (Exception e) {
            System.out.println("--- 解析视频参数信息出错！ --- 错误信息： " + e.getMessage());
            return null;
        }

        // 封装视频中的音频信息
        MusicMetaInfo musicMetaInfo = new MusicMetaInfo();
        musicMetaInfo.setFormat(musicFormat);
        musicMetaInfo.setDuration(duration);
        musicMetaInfo.setBitRate(musicBitrate);
        musicMetaInfo.setSampleRate(samplerate);
        // 封装视频信息
        VideoMetaInfo videoMetaInfo = new VideoMetaInfo();
        videoMetaInfo.setFormat(videoFormat);
        videoMetaInfo.setSize(videoSize);
        videoMetaInfo.setBitRate(videoBitrate);
        videoMetaInfo.setDuration(duration);
        videoMetaInfo.setEncoder(videoEncoder);
        videoMetaInfo.setFrameRate(videoFramerate);
        videoMetaInfo.setHeight(videoHeight);
        videoMetaInfo.setWidth(videoWidth);
        videoMetaInfo.setMusicMetaInfo(musicMetaInfo);

        return videoMetaInfo;
    }

    /**
     * 获取视频的基本信息（从流中）
     *
     * @param inputStream 源视频流路径
     * @return 视频的基本信息，解码失败时返回null
     */
    public static VideoMetaInfo getVideoMetaInfo(InputStream inputStream) {
        VideoMetaInfo videoInfo = new VideoMetaInfo();
        try {
            File file = File.createTempFile("tmp", null);
            if (!file.exists()) {
                return null;
            }
            FileUtils.copyInputStreamToFile(inputStream, file);
            videoInfo = getVideoMetaInfo(file);
            file.deleteOnExit();
            return videoInfo;
        } catch (Exception e) {
            System.out.println("--- 从流中获取视频基本信息出错 --- 错误信息： " + e.getMessage());
            return null;
        }
    }

    /**
     * 获取音频的基本信息（从文件中）
     * @param musicFile 音频文件路径
     * @return 音频的基本信息，解码失败时返回null
     */
    public static MusicMetaInfo getMusicMetaInfo(File musicFile) {
        if (null == musicFile || !musicFile.exists()) {
            System.out.println("--- 无法获取音频信息，因为要解析的音频文件为空 ---");
            return null;
        }
        // 获取音频信息字符串，方便后续解析
        String parseResult = getMetaInfoFromFFmpeg(musicFile);

        Long duration = 0L; // 音频时长
        Integer musicBitrate = 0; // 音频码率
        Long samplerate = 0L; // 音频采样率
        String musicFormat = ""; // 音频格式
        Long musicSize = musicFile.length(); // 音频大小

        Matcher durationMacher = durationPattern.matcher(parseResult);
        Matcher musicStreamMacher = musicStreamPattern.matcher(parseResult);

        try {
            // 匹配音频播放时长等信息
            if (durationMacher.find()) {
                long hours = (long)Integer.parseInt(durationMacher.group(1));
                long minutes = (long)Integer.parseInt(durationMacher.group(2));
                long seconds = (long)Integer.parseInt(durationMacher.group(3));
                long dec = (long)Integer.parseInt(durationMacher.group(4));
                duration = dec * 100L + seconds * 1000L + minutes * 60L * 1000L + hours * 60L * 60L * 1000L;
                //String startTime = durationMacher.group(5) + "ms";
                musicBitrate = Integer.parseInt(durationMacher.group(6));
            }
            // 匹配音频采样率等信息
            if (musicStreamMacher.find()) {
                musicFormat = musicStreamMacher.group(1); // 提取音频格式
                //String s2 = videoMusicStreamMacher.group(2);
                samplerate = Long.parseLong(musicStreamMacher.group(3)); // 提取采样率
                //String s4 = videoMusicStreamMacher.group(4);
                //String s5 = videoMusicStreamMacher.group(5);
                musicBitrate = Integer.parseInt(musicStreamMacher.group(6)); // 提取比特率
            }
        } catch (Exception e) {
            System.out.println("--- 解析音频参数信息出错！ --- 错误信息： " + e.getMessage());
            return null;
        }

        // 封装视频中的音频信息
        MusicMetaInfo musicMetaInfo = new MusicMetaInfo();
        musicMetaInfo.setFormat(musicFormat);
        musicMetaInfo.setDuration(duration);
        musicMetaInfo.setBitRate(musicBitrate);
        musicMetaInfo.setSampleRate(samplerate);
        musicMetaInfo.setSize(musicSize);
        return musicMetaInfo;
    }

    /**
     * 获取音频的基本信息（从流中）
     * @param inputStream 源音乐流路径
     * @return 音频基本信息，解码出错时返回null
     */
    public static MusicMetaInfo getMusicMetaInfo(InputStream inputStream) {
        MusicMetaInfo musicMetaInfo = new MusicMetaInfo();
        try {
            File file = File.createTempFile("tmp", null);
            if (!file.exists()) {
                return null;
            }
            FileUtils.copyInputStreamToFile(inputStream, file);
            musicMetaInfo = getMusicMetaInfo(file);
            file.deleteOnExit();
            return musicMetaInfo;
        } catch (Exception e) {
            System.out.println("--- 从流中获取音频基本信息出错 --- 错误信息： " + e.getMessage());
            return null;
        }
    }


    /**
     * 获取图片的基本信息（从流中）
     *
     * @param inputStream 源图片路径
     * @return 图片的基本信息，获取信息失败时返回null
     */
    public static ImageMetaInfo getImageInfo(InputStream inputStream) {
        BufferedImage image = null;
        ImageMetaInfo imageInfo = new ImageMetaInfo();
        try {
            image = ImageIO.read(inputStream);
            imageInfo.setWidth(image.getWidth());
            imageInfo.setHeight(image.getHeight());
            imageInfo.setSize(Long.valueOf(String.valueOf(inputStream.available())));
            return imageInfo;
        } catch (Exception e) {
            System.out.println("--- 获取图片的基本信息失败 --- 错误信息： " + e.getMessage());
            return null;
        }
    }

    /**
     * 获取图片的基本信息 （从文件中）
     *
     * @param imageFile 源图片路径
     * @return 图片的基本信息，获取信息失败时返回null
     */
    public static ImageMetaInfo getImageInfo(File imageFile) {
        BufferedImage image = null;
        ImageMetaInfo imageInfo = new ImageMetaInfo();
        try {
            if (null == imageFile || !imageFile.exists()) {
                return null;
            }
            image = ImageIO.read(imageFile);
            imageInfo.setWidth(image.getWidth());
            imageInfo.setHeight(image.getHeight());
            imageInfo.setSize(imageFile.length());
            imageInfo.setFormat(getFormat(imageFile));
            return imageInfo;
        } catch (Exception e) {
            System.out.println("--- 获取图片的基本信息失败 --- 错误信息： " + e.getMessage());
            return null;
        }
    }

    /**
     * 检查文件类型是否是给定的类型
     * @param inputFile 源文件
     * @param givenFormat 指定的文件类型；例如：{"MP4", "AVI"}
     * @return
     */
    public static boolean isGivenFormat(File inputFile, String[] givenFormat) {
        if (null == inputFile || !inputFile.exists()) {
            System.out.println("--- 无法检查文件类型是否满足要求，因为要检查的文件不存在 --- 源文件： " + inputFile);
            return false;
        }
        if (null == givenFormat || givenFormat.length <= 0) {
            System.out.println("--- 无法检查文件类型是否满足要求，因为没有指定的文件类型 ---");
            return false;
        }
        String fomat = getFormat(inputFile);
        return isLegalFormat(fomat, givenFormat);
    }

    /**
     * 使用FFmpeg的"-i"命令来解析视频信息
     * @param inputFile 源媒体文件
     * @return 解析后的结果字符串，解析失败时为空
     */
    public static String getMetaInfoFromFFmpeg(File inputFile) {
        if (inputFile == null || !inputFile.exists()) {
            throw new RuntimeException("源媒体文件不存在，源媒体文件路径： ");
        }
        List<String> commond = new ArrayList<String>();
        commond.add("-i");
        commond.add(inputFile.getAbsolutePath());
        String executeResult = executeCommand(commond);
        return executeResult;
    }

    /**
     * 检测视频格式是否合法
     * @param format
     * @param formats
     * @return
     */
    private static boolean isLegalFormat(String format, String formats[]) {
        for (String item : formats) {
            if (item.equals(StringUtils.upperCase(format))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建gif
     *
     * @param image 多个jpg文件名（包含路径）
     * @param outputPath 生成的gif文件名（包含路径）
     * @param playTime 播放的延迟时间，可调整gif的播放速度
     */
    private static void createGifImage(String image[], String outputPath, int playTime) {
        if (null == outputPath) {
            throw new RuntimeException("转换后的GIF路径为空，请检查转换后的GIF存放路径是否正确");
        }
        try {
            AnimatedGifEncoder encoder = new AnimatedGifEncoder();
            encoder.setRepeat(0);
            encoder.start(outputPath);
            BufferedImage src[] = new BufferedImage[image.length];
            for (int i = 0; i < src.length; i++) {
                encoder.setDelay(playTime); // 设置播放的延迟时间
                src[i] = ImageIO.read(new File(image[i])); // 读入需要播放的jpg文件
                encoder.addFrame(src[i]); // 添加到帧中
            }
            encoder.finish();
        } catch (Exception e) {
            System.out.println("--- 多张静态图转换成动态GIF图的过程出错 --- 错误信息： " + e.getMessage());
        }
    }


    /**
     * 获取指定文件的后缀名
     * @param file
     * @return
     */
    private static String getFormat(File file) {
        String fileName = file.getName();
        String format = fileName.substring(fileName.indexOf(".") + 1);
        return format;
    }


    /**
     * 在程序退出前结束已有的FFmpeg进程
     */
    private static class ProcessKiller extends Thread {
        private Process process;

        public ProcessKiller(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            this.process.destroy();
            System.out.println("--- 已销毁FFmpeg进程 --- 进程名： " + process.toString());
        }
    }


    /**
     * 用于取出ffmpeg线程执行过程中产生的各种输出和错误流的信息
     */
    static class PrintStream extends Thread {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = new StringBuffer();

        public PrintStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try {
                if (null == inputStream) {
                    System.out.println("--- 读取输出流出错！因为当前输出流为空！---");
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println(line);
                    stringBuffer.append(line);
                }
            } catch (Exception e) {
                System.out.println("--- 读取输入流出错了！--- 错误信息：" + e.getMessage());
            } finally {
                try {
                    if (null != bufferedReader) {
                        bufferedReader.close();
                    }
                    if (null != inputStream) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    System.out.println("--- 调用PrintStream读取输出流后，关闭流时出错！---");
                }
            }
        }
    }

}