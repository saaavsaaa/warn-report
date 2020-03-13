package util;

import ws.schild.jave.*;

import java.io.File;

//https://github.com/a-schild/jave2/wiki/Examples
public class AudioTest {

    public static void main(String[] args) throws EncoderException {
        String sourcePath = "D:\\share\\chinese_speech\\三个代表.mp3";
        String targetPath = "D:\\share\\chinese_speech\\target.wav";
        String exePath = "D:\\big-data\\video\\bin\\ffmpeg.exe";

        FFMPEGLocator locator = new FFMPEGLocator() {
            @Override
            protected String getFFMPEGExecutablePath() {
                return exePath;
            }
        };

        toWav16Hz(sourcePath, targetPath, locator);
    }

    public static void toWav(String sourcePath, String targetPath, FFMPEGLocator locator) throws EncoderException {
        File source = new File(sourcePath);
        File target = new File(targetPath);
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le");
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("wav");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder(locator);
        encoder.encode(new MultimediaObject(source, locator), target, attrs);
    }

    public static void toWav16Hz(String sourcePath, String targetPath, FFMPEGLocator locator) throws EncoderException {
        File source = new File(sourcePath);
        File target = new File(targetPath);
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libmp3lame");
        audio.setBitRate(new Integer(512000)); //kbit/s
        audio.setChannels(new Integer(2));
        audio.setSamplingRate(new Integer(16000)); //Hz
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("mp3");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder(locator);
        encoder.encode(new MultimediaObject(source, locator), target, attrs);
    }
}
