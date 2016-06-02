import org.junit.Test;

import java.io.IOException;

/**
 * Created by ldb on 2016/6/2.
 */
public class TestSSH {
    final String host = "192.168.1.44";
    final int port = 22;
    final String user = "root";
    final String password = "admin123!";
    final String rocketNSrv = "192.168.1.44:9876";
    final String topic = "testTopic";

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
}
