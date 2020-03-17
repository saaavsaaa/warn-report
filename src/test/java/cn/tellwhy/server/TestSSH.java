package cn.tellwhy.server;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ldb on 2016/6/2.
 */
public class TestSSH {
    final String host = "192.168.1.47";
    final int port = 22;
    final String user = "root";
    final String password = "admin123!";
    final String rocketNSrv = "192.168.1.44:9876";
    final String topic = "testTopic";
    
    @Test
    public void grep() throws IOException {
        LinuxExecUtil ssh = new LinuxExecUtil();
        ssh.connect("192.168.1.213", port, user, password);
        
        StringBuilder builder = new StringBuilder();
        builder.append("cd /logs/release/p2p-app;");
//        builder.append("grep -i -A5 '17:24:33.569' info/info.2017-07-18.log");
//        builder.append(String.format("grep %s -ri * --color | head -n %d;", "queryFirstShow", 3));
//        builder.append(String.format("grep %s -r * --color | grep -i '%s' | head -n %d;", "15111111111", "queryMyMessNoNotRead", 3));
//        builder.append(String.format("grep %s -r * | grep -i '%s' | grep '%s';", "15111111111", "CipherFilter", "queryMyMessNoNotRead"));
        builder.append(String.format("grep %s -ri * -A5 | grep '%s' | grep -v %s | head -n 50;", "CipherFilter", "queryLoadingPic", "responseJson"));
        String exeContent = new String(builder);
        ssh.execute(exeContent);
    }

    @Test
    public void testTopicStatus() throws IOException {
        LinuxExecUtil ssh = new LinuxExecUtil();
        ssh.connect(host, port, user, password);

        //ssh.execCommand("cd /usr/local/alibaba-rocketmq/bin/;cat ns.log");
        String exeContent = "";
        exeContent = String.format("sh /usr/local/alibaba-rocketmq/bin/mqadmin topicStatus -n %s -t %s", rocketNSrv, topic);
        //exeContent = String.format("sh mqadmin topicStatus -n %s -t %s", rocketNSrv, topic);
        ssh.execute(exeContent);

        //ssh.execCommand("mvn -v");
        //ssh.execCommand("/usr/java/jdk1.8.0_91/bin/java -version");
    }

    @Test
    public void testUpload() throws IOException {
        LinuxExecUtil ssh = new LinuxExecUtil();
        ssh.connect(host, port, user, password);
        ssh.upload("e:\\1.txt", "/root/");
    }

    @Test
    public void test() throws IOException {
        LinuxExecUtil ssh = new LinuxExecUtil();
        ssh.connect(host, port, user, password);
        //String exeContent = "/bin/sh -c netstat -anp";
        String exeContent = "netstat -anp";
        ssh.execute(exeContent);
    }

    @Test
    public void testDel() throws IOException {
        LinuxExecUtil ssh = new LinuxExecUtil();
        ssh.connect(host, port, user, password);
        //String exeContent = "/bin/sh -c netstat -anp";
        String exeContent = "cd /usr/local/tomcat/;rm -rf webapps/portal-bos*";
        ssh.execute(exeContent);
    }

    @Test
    public void testAWS() throws IOException {
        List<String> waitRecognizations = new ArrayList<String>(
                Arrays.asList("pcm_alaw","pcm_f32be","pcm_f32le","pcm_f64be","pcm_f64le","pcm_mulaw","pcm_s16be","pcm_s16le"
                        ,"pcm_s24be","pcm_s24le","pcm_s32be","pcm_s32le","pcm_s8","pcm_u16be","pcm_u16le","pcm_u24be"
                        ,"pcm_u24le","pcm_u32be","pcm_u32le","pcm_u8")
        ) ;
        String localPath = "D:\\share\\chinese_speech\\collect\\";
        waitRecognizations = getFileList(localPath);
        for (String each : waitRecognizations) {
            try {
                String target = each + "target.wav";
                speechRecognization(localPath, target);
            } catch (Exception e) {
                System.out.println(each + ":" + e.getMessage());
                e.printStackTrace();
            }
        }

//        String exeContent = "/home/ubuntu/github/kaldi-ali/kaldi-trunk/src/onlinebin/online-wav-gmm-decode-faster  --verbose=1 --rt-min=0.8 --rt-max=0.85 " +
//                "--max-active=4000 --beam=12.0 --acoustic-scale=0.0769 --left-context=3 --right-context=3 " +
//                "scp:/home/ubuntu/github/kaldi-ali/kaldi-trunk/egs/thchs30/online_demo_tri4b_ali/work/input.scp " +
//                "/home/ubuntu/github/kaldi-ali/kaldi-trunk/egs/thchs30/online_demo_tri4b_ali/online-data/models/tri4b/final.mdl " +
//                "/home/ubuntu/github/kaldi-ali/kaldi-trunk/egs/thchs30/online_demo_tri4b_ali/online-data/models/tri4b/HCLG.fst " +
//                "/home/ubuntu/github/kaldi-ali/kaldi-trunk/egs/thchs30/online_demo_tri4b_ali/online-data/models/tri4b/words.txt " +
//                "'1:2:3:4:5' ark,t:/home/ubuntu/github/kaldi-ali/kaldi-trunk/egs/thchs30/online_demo_tri4b_ali/work/trans.txt " +
//                "ark,t:/home/ubuntu/github/kaldi-ali/kaldi-trunk/egs/thchs30/online_demo_tri4b_ali/work/ali.txt " +
//                "/home/ubuntu/github/kaldi-ali/kaldi-trunk/egs/thchs30/online_demo_tri4b_ali/online-data/models/tri4b/final.mat" ;
//        ssh.execute(exeContent);
    }

    public void speechRecognization(String localPath, String target) throws IOException {
        String keyPath = "D:\\share\\deep.pem";
        String host = "ec2-52-82-22-125.cn-northwest-1.compute.amazonaws.com.cn";
        String localWAV = localPath + target;
        String basePath = "/home/ubuntu/github/kaldi-ali/kaldi-trunk/egs/thchs30/online_demo_tri4b_ali";
        String audioPath = basePath + "/online-data/audio";

        LinuxExecUtil ssh = new LinuxExecUtil();
        ssh.connect(host, "ubuntu" , "", keyPath);

        ssh.upload(localWAV, audioPath);

        System.out.println(target);
        String exeContent = "sh " + basePath + "/run_with_conda.sh";//shell脚本全都需要改为全路径
        ssh.execute(exeContent);

        exeContent = "rm " + audioPath + "/" + target;
        ssh.execute(exeContent);

        ssh.close();
    }

    /*
     * 读取指定路径下的文件名和目录名
     */
    public List<String> getFileList(String path) {
        File file = new File(path);
        File[] fileList = file.listFiles();
        List<String> result = new ArrayList<>();
        for (File each : fileList) {
            if (each.isFile() && each.getName().endsWith(".wav") &&
                    !each.getName().equals("3gdb.wav") && !each.getName().equals("三个代表.wav")) {
                System.out.println(each.getName());
                result.add(each.getName());
            }
        }
        return result;
    }

    @Test
    public void testDelAWS() throws IOException {
        String keyPath = "D:\\share\\deep.pem";
        String host = "ec2-52-82-22-125.cn-northwest-1.compute.amazonaws.com.cn";
        String target = "pcm_alaw" + "target.wav";
        String localWAV = "D:\\share\\chinese_speech\\collect\\" + target;
        String basePath = "/home/ubuntu/github/kaldi-ali/kaldi-trunk/egs/thchs30/online_demo_tri4b_ali";
        String audioPath = basePath + "/online-data/audio";

        LinuxExecUtil ssh = new LinuxExecUtil();
        ssh.connect(host, "ubuntu" , "", keyPath);

        String exeContent = "rm " + audioPath + "/" + target;
        ssh.execute(exeContent);

        ssh.close();
    }
}
