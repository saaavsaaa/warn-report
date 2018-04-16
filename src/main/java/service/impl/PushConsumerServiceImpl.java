package service.impl;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import third.rocketDoubleWrite.listener.MessageReceive;
import third.rocketDoubleWrite.listener.RocketListener;
import third.rocketDoubleWrite.listener.impl.BusinessRun;
import service.PushConsumerService;

import java.util.ResourceBundle;

public class PushConsumerServiceImpl implements PushConsumerService {

    private String topic;
    private String subExpression;
    private String consumerGroup;
    private String namesrvAddr;
    private String namesrvAddrBak;
    private MessageReceive receive;

    public PushConsumerServiceImpl(){
        ResourceBundle bundle = ResourceBundle.getBundle("third/rocket");
        topic = bundle.getString("rocket.topic");
        subExpression = bundle.getString("rocket.subExpression");
        consumerGroup = bundle.getString("rocket.consumerGroup");
        namesrvAddr = bundle.getString("rocket.namesrvAddr");
        namesrvAddrBak = bundle.getString("rocket.namesrvAddrBak");
        receive = new BusinessRun();
    }

    @Override
    public void run() throws MQClientException, InterruptedException {
        System.out.println("case : PushConsumerServiceImpl start");
        RocketListener listener = new RocketListener(receive);

        DefaultMQPushConsumer consumer;
        try {
            consumer = buildPushConsumer(namesrvAddr, consumerGroup, topic, subExpression, listener);
            consumer.start();
            System.out.println("case : consumer start");
        } catch (Exception e) {
            consumer = buildPushConsumer(namesrvAddrBak, consumerGroup, topic, subExpression, listener);
            consumer.start();
            System.out.println("case : consumerbak start, reason : consumer error" +  e.getMessage());
        }

    }

    private DefaultMQPushConsumer buildPushConsumer(final String address, final String consumerGroup, final String topic, final String subExpression, final MessageListenerConcurrently messageListener) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setNamesrvAddr(address);
        consumer.setConsumerGroup(consumerGroup);
        consumer.subscribe(topic, subExpression);
        consumer.registerMessageListener(messageListener);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        return consumer;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setSubExpression(String subExpression) {
        this.subExpression = subExpression;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public void setNamesrvAddrBak(String namesrvAddrBak) {
        this.namesrvAddrBak = namesrvAddrBak;
    }

    public void setReceive(MessageReceive receive) {
        this.receive = receive;
    }
}