package video;

import java.io.*;
import java.sql.Time;
import java.util.List;

public class VideoExtracting {
    // 视频路径
    private String ffmpegEXE;

    public void getCover(String videoInputPath, String coverOutputPath) throws IOException, InterruptedException {
//        ffmpeg.exe -ss 00:00:01 -i spring.mp4 -vframes 1 bb.jpg
        List<String> command = new java.util.ArrayList<String>();
        command.add(ffmpegEXE);

        // 指定截取第1秒
        command.add("-ss");
        command.add("00:00:01");

        command.add("-y");
        command.add("-i");
        command.add(videoInputPath);

        command.add("-vframes");
        command.add("1");

        command.add(coverOutputPath);

        for (String c : command) {
            System.out.print(c + " ");
        }

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ( (line = br.readLine()) != null ) {
        }

        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }
    }

    public String getFfmpegEXE() {
        return ffmpegEXE;
    }

    public void setFfmpegEXE(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public VideoExtracting(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public static void main(String[] args) {
        //String exePath = "E:\\video\\bin\\ffmpeg.exe";
        //VideoExtracting videoInfo = new VideoExtracting(exePath);
        String videoPath = "C:\\Users\\lidongbo\\Desktop\\群图片\\WeChat_20190809171656.mp4";
        try {
            //videoInfo.getCover(path,"E:\\tmp\\1.jpg");
            File video = new File(videoPath);
            File p = new File("E:\\tmp\\1.gif");
            //System.out.println(VideoUtil.getVideoMetaInfo(video)); //获取视频信息
            //VideoUtil.cutVideoFrame(video, p, new Time(0, 0, 3), 300); //合成动图
            //抽取图片 tbr每秒帧数（真实意义是帧率，这里就直接用了）
            VideoUtil.cutVideoFrame(video, "E:\\tmp", new Time(0, 0, 3), 640, 480, 30, "24",true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
