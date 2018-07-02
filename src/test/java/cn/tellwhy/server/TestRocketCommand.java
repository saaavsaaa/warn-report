package cn.tellwhy.server;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by ldb on 2016/6/2.
 */
public class TestRocketCommand {
    final String ip = "192.168.1.45";
    final int port = 22;
    final String user = "root";
    final String password = "admin123!";

    final String rocketIp = "192.168.1.45";
    final String topic = "registerTopic";

    final String cluster = "DefaultClusterB";
    final String broker = "broker-a";

    final String rocketPath = "/usr/local/alibaba-rocketmq/bin/";
    final String rocketNSrv = rocketIp + ":9876";
    final String rocketBroker = rocketIp + ":10911";

    //todo : 起线程发redis队列,消费者起线程删除
    //todo : sh mqadmin checkMsg -l /root/store -n 192.168.1.44:9876 -p

    @Test
    public void testConsumerProgress() throws IOException {
        String group = "cgr1";
        LinuxExecUtil executor = buildExecutor();
        String exeContent = String.format("sh %smqadmin consumerProgress -g %s -n %s", rocketPath, group, rocketNSrv);
        executor.execute(exeContent);
    }

    @Test
    public void testQueryByOffset() throws IOException {
        int queueId = 0;
        int index = 470;

        LinuxExecUtil executor = buildExecutor();
        String exeContent = String.format("sh %smqadmin queryMsgByOffset -b %s -i %d -o %d -n %s -t %s", rocketPath, broker, queueId, index, rocketNSrv, topic);
        executor.execute(exeContent);
    }

    @Test
    public void testQueryByKey() throws IOException {
        String key = "13211111222";
        LinuxExecUtil executor = buildExecutor();
        String exeContent = String.format("sh %smqadmin queryMsgByKey -k %s -n %s -t %s", rocketPath, key, rocketNSrv, topic);
        executor.execute(exeContent);
        exeContent += "| wc -l";
        executor.execute(exeContent);
        System.out.println("同一个Key最多只能显示65条记录");
    }

    @Test
    public void testBrokerStatus() throws IOException {
        LinuxExecUtil executor = buildExecutor();
        String exeContent = String.format("sh %smqadmin brokerStatus -b %s -n %s", rocketPath, rocketBroker, rocketNSrv);
        executor.execute(exeContent);
    }

    @Test
    public void testConsumerStatus() throws IOException {
        String group = "cgr1";
        LinuxExecUtil executor = buildExecutor();
        String exeContent = String.format("sh %smqadmin consumerStatus -g %s -n %s", rocketPath, group, rocketNSrv);
        executor.execute(exeContent);
        System.out.println("@后为消费者所在线程");
    }

    @Test
    public void testTopicStatus() throws IOException {
        LinuxExecUtil executor = buildExecutor();
        String exeContent = String.format("sh %smqadmin topicStatus -n %s -t %s", rocketPath, rocketNSrv, topic);
        executor.execute(exeContent);
    }

    @Test
    public void testCreateTopic() throws IOException {
        LinuxExecUtil executor = buildExecutor();
        String exeContent = String.format("sh %smqadmin updateTopic -b %s -n %s -t %s", rocketPath, rocketBroker, rocketNSrv, topic);
        executor.execute(exeContent);
    }

    @Test
    public void testDeleteTopic() throws IOException {
        LinuxExecUtil executor = buildExecutor();
        String exeContent = String.format("sh %smqadmin deleteTopic -c %s  -n %s -t %s", rocketPath, cluster, rocketNSrv, topic);
        executor.execute(exeContent);
    }

    private LinuxExecUtil buildExecutor() throws IOException {
        LinuxExecUtil ssh = new LinuxExecUtil();
        ssh.connect(ip, port, user, password);
        return ssh;
    }
}
