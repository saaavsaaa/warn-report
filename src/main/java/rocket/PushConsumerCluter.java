package rocket;


import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import io.netty.util.internal.ConcurrentSet;

import java.util.List;

public class PushConsumerCluter {
    /*final ConcurrentSet<DefaultMQPushConsumer> consumers = new ConcurrentSet<DefaultMQPushConsumer>();

    public PushConsumerCluter(final List<String> addresses, final String consumerGroup, final String topic, final String subExpression, final MessageListenerConcurrently messageListener) throws MQClientException {
        if (addresses == null || addresses.isEmpty()) {
            throw new IllegalArgumentException(" the arg addresses should have value ! ");
        }
        for (String one : addresses) {
            consumers.add(buildPushConsumer(one, consumerGroup, topic, subExpression, messageListener));
        }
    }

    private DefaultMQPushConsumer buildPushConsumer(final String address, final String consumerGroup, final String topic, final String subExpression, final MessageListenerConcurrently messageListener) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setNamesrvAddr(address);
        consumer.setConsumerGroup(consumerGroup);
        consumer.subscribe(topic, subExpression);
        consumer.registerMessageListener(messageListener);
        consumer.start();
        return consumer;
    }*/

    /*public void shutdown(){
        consumer.shutdown();
    }*/
}
