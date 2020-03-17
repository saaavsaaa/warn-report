package util;

import ws.schild.jave.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//https://github.com/a-schild/jave2/wiki/Examples
public class AudioTest {
    /*
    * s是有符号，u是无符号，f是浮点数。be是大端，le是小端
         DE alaw            PCM A-law
         DE f32be           PCM 32-bit floating-point big-endian
         DE f32le           PCM 32-bit floating-point little-endian
         DE f64be           PCM 64-bit floating-point big-endian
         DE f64le           PCM 64-bit floating-point little-endian
         DE mulaw           PCM mu-law
         DE s16be           PCM signed 16-bit big-endian
         DE s16le           PCM signed 16-bit little-endian
         DE s24be           PCM signed 24-bit big-endian
         DE s24le           PCM signed 24-bit little-endian
         DE s32be           PCM signed 32-bit big-endian
         DE s32le           PCM signed 32-bit little-endian
         DE s8              PCM signed 8-bit
         DE u16be           PCM unsigned 16-bit big-endian
         DE u16le           PCM unsigned 16-bit little-endian
         DE u24be           PCM unsigned 24-bit big-endian
         DE u24le           PCM unsigned 24-bit little-endian
         DE u32be           PCM unsigned 32-bit big-endian
         DE u32le           PCM unsigned 32-bit little-endian
         DE u8              PCM unsigned 8-bit
    * */
    static List<String> codecs = new ArrayList<String>(
            Arrays.asList("pcm_alaw","pcm_f32be","pcm_f32le","pcm_f64be","pcm_f64le","pcm_mulaw","pcm_s16be","pcm_s16le"
                    ,"pcm_s24be","pcm_s24le","pcm_s32be","pcm_s32le","pcm_s8","pcm_u16be","pcm_u16le","pcm_u24be"
                    ,"pcm_u24le","pcm_u32be","pcm_u32le","pcm_u8")
    ) ;

    public static void main(String[] args) {
        String sourcePath = "D:\\share\\chinese_speech\\collect\\三个代表.mp3";
        String targetPath = "D:\\share\\chinese_speech\\collect\\";
        String exePath = "D:\\big-data\\video\\bin\\ffmpeg.exe";

        FFMPEGLocator locator = new FFMPEGLocator() {
            @Override
            protected String getFFMPEGExecutablePath() {
                return exePath;
            }
        };

        for (String each : codecs) {
            try {
                String target = targetPath + each + "target.wav";
                toWav16Hz(sourcePath, target, locator, each);
            } catch (Exception e) {
                System.out.println(each + ":" + e.getMessage());
                e.printStackTrace();
            }
        }
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

    public static void toWav16Hz(String sourcePath, String targetPath, FFMPEGLocator locator, String codec) throws EncoderException {
        File source = new File(sourcePath);
        File target = new File(targetPath);
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec(codec); // "pcm_s16le"
        audio.setBitRate(new Integer(11025)); //kbit/s   44100*16kbps 11025 22050  512000
        audio.setChannels(new Integer(1)); // 1=mono, 2=stereo, 4=quad
        audio.setSamplingRate(new Integer(16000)); //Hz
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("wav");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder(locator);
        encoder.encode(new MultimediaObject(source, locator), target, attrs);
    }
}
