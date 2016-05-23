import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;
import rocket.ProducerCluter;
import rocket.PullConsumerCluter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldb on 2016/5/16.
 */
public class TestRocketCluter {
    @Test
    public void TestSend() throws MQClientException, InterruptedException, RemotingException, MQBrokerException {
        final String producerGroup = "pg";
        final String topic = "topicTest";
        final String tag = "";
        final String key = "aaa";
        final String body = "it's body by qqq";

        List<String> addresses = new ArrayList<String>();
        addresses.add("192.168.1.44:9876");
        addresses.add("192.168.1.45:9876");

        ProducerCluter producer = new ProducerCluter(addresses, producerGroup);

        Message msg = ProducerCluter.buildMessage(topic, tag, key, body);
        boolean sendResult = producer.send(msg);
        System.out.println(sendResult);

        //Thread.sleep(1000*1000);
    }

    @Test
    public void TestCatch() throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        //336
        final String address = "192.168.1.44:9876";//"192.168.1.45:9876";
        final String topic = "registerTopic";
        final String consumerGroup = "cgr1";
        final String subExpression = "*";
        final Long offset = 0L;

        List<String> addresses = new ArrayList<String>();
        addresses.add("192.168.1.44:9876");
        addresses.add("192.168.1.45:9876");

        PullConsumerCluter consumer = new PullConsumerCluter(address, consumerGroup);
        List<MessageExt> extList = consumer.getMessage(topic, offset, subExpression);
        for (MessageExt ext : extList){
            String key = ext.getKeys();
            String keys = ext.getProperty("KEYS");
            String value = new String(ext.getBody());
            System.out.println("key : " + key + "; KEYS :" + keys + "; body : " + value + "\n");
        }
    }
}
