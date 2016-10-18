package rocket;


import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;

public class PushConsumer {
    final DefaultMQPushConsumer consumer;

    public PushConsumer(final String address, final String consumerGroup, final String topic, final String subExpression, final MessageListenerConcurrently messageListener) throws MQClientException {
        consumer = new DefaultMQPushConsumer();
        consumer.setNamesrvAddr(address);
        consumer.setConsumerGroup(consumerGroup);
        consumer.subscribe(topic, subExpression);
        consumer.registerMessageListener(messageListener);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.start();
    }

    public void shutdown(){
        consumer.shutdown();
    }
}
