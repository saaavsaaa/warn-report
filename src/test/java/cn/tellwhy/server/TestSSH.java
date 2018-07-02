package cn.tellwhy.server;

import org.junit.Test;

import java.io.IOException;

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
}
