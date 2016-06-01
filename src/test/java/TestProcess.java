import com.alibaba.rocketmq.client.consumer.PullResult;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;
import rocket.Producer;
import rocket.PullConsumer;
import rocket.PushConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestProcess {

    //sh mqadmin updateTopic -b 192.168.3.62 -n 192.168.3.62:9876 -t 'testTopic'

    @Test
    public void TestSend() throws MQClientException, InterruptedException, RemotingException, MQBrokerException {
        final String address = "192.168.1.44:9876;192.168.1.45:9876";
        final String producerGroup = "pg";
        final String topic = "topicTest";
        final String tag = "";
        final String key = "aaa";
        final String body = "it's body by aaa";
        Producer producer = new Producer(address, producerGroup);

        Message msg = Producer.buildMessage(topic, tag, key, body);
        SendResult sendResult = producer.send(msg);
        System.out.println(sendResult);
    }

    @Test
    public void TestReceive() throws InterruptedException, RemotingException, MQBrokerException, MQClientException {
        final String address = "192.168.1.45:9876";//"192.168.1.45:9876";
        final String topic = "testTopic2";


        MessageListenerConcurrently listener = new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                System.out.println(Thread.currentThread().getName() + " Receive New Messages: " + msgs);
                for (MessageExt ext : msgs){
                    String key = ext.getKeys();
                    String keys = ext.getProperty("KEYS");
                    String value = new String(ext.getBody());
                    System.out.println("key : " + key + "; KEYS :" + keys + "; body : " + value);
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        };

        final String consumerGroup = "cgr1";
        final String subExpression = "*";
        PushConsumer consumer = null;
        try {
            consumer = new PushConsumer(address, consumerGroup, topic, subExpression, listener);
        } catch (MQClientException e) {
            consumer = new PushConsumer("192.168.1.45:9876", consumerGroup, topic, subExpression, listener);
        }
        if (consumer != null){
            Thread.sleep(1000);
        }
    }

    @Test
    public void TestPull() throws MQClientException {
        final String address = "192.168.1.45:9876";//"192.168.1.44:9876";
        final String topic = "topicTest";
        final String consumerGroup = "cgr";
        final String subExpression = "*";

        PullConsumer consumer = new PullConsumer(address, consumerGroup);

        Set<MessageQueue> mqs = consumer.getMessageQueue(topic);
        for (MessageQueue mq : mqs) {
            System.out.println("Consume from the queue: " + mq);


            SINGLE_MQ: while (true) {
                try {
                    PullResult pullResult =
                            consumer.pullBlockIfNotFound(mq, subExpression, getMessageQueueOffset(mq), 32);
                    System.out.println(pullResult);
                    putMessageQueueOffset(mq, pullResult.getNextBeginOffset());
                    switch (pullResult.getPullStatus()) {
                        case FOUND:
                            List<MessageExt> msgs = pullResult.getMsgFoundList();
                            for (MessageExt ext : msgs){
                                String key = ext.getKeys();
                                String keys = ext.getProperty("KEYS");
                                String value = new String(ext.getBody());
                                System.out.println("key : " + key + "; KEYS :" + keys + "; body : " + value + "\n");
                            }
                            break;
                        case NO_MATCHED_MSG:
                            break;
                        case NO_NEW_MSG:
                            break SINGLE_MQ;
                        case OFFSET_ILLEGAL:
                            break;
                        default:
                            break;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (Map.Entry<MessageQueue, Long> one : offseTable.entrySet()) {
            System.out.println(one.getValue());
        }
    }

    private static final Map<MessageQueue, Long> offseTable = new HashMap<MessageQueue, Long>();
    private static long getMessageQueueOffset(MessageQueue mq) {
        Long offset = offseTable.get(mq);
        if (offset != null)
            return offset;

        return 0;
    }
    private static void putMessageQueueOffset(MessageQueue mq, long offset) {
        offseTable.put(mq, offset);
    }
}
