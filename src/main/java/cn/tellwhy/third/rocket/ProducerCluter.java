package cn.tellwhy.third.rocket;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import io.netty.util.internal.ConcurrentSet;
import java.util.List;
import java.util.concurrent.*;

/**
 *
 * 最终解决办法，主从切换
 * 最简单办法，记录失败消息，定时重发
 * 需要加日志
 */
public class ProducerCluter {
    final ConcurrentSet<DefaultMQProducer> producers = new ConcurrentSet<DefaultMQProducer>();
    //final ConcurrentMap<DefaultMQProducer, ConcurrentSet<Message>> errorSendeds = new ConcurrentHashMap<DefaultMQProducer, ConcurrentSet<Message>>();
    final ConcurrentSet<Message> errorSendeds = new ConcurrentSet<Message>();

    public ProducerCluter(final List<String> addresses, final String producerGroup) throws MQClientException {
        if (addresses == null || addresses.isEmpty()) {
            throw new IllegalArgumentException(" the arg addresses should have value ! ");
        }
        for (String one : addresses) {
            producers.add(buildProducer(one, producerGroup));
        }
        repeat();
    }

    private DefaultMQProducer buildProducer(final String address, final String producerGroup) throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setInstanceName(Long.toString(System.currentTimeMillis()));
        producer.setNamesrvAddr(address);
        producer.setCompressMsgBodyOverHowmuch(Integer.MAX_VALUE);
        producer.start();
        return producer;
    }

    public boolean send(final Message msg) throws InterruptedException, RemotingException, MQBrokerException {
        if (producers.isEmpty()) {
            throw new IllegalArgumentException(" there isn't producer which could be used  ! ");
        }

        DefaultMQProducer errorProducer = null;
        for (DefaultMQProducer one : producers) {
            try {
                SendResult sendResult = one.send(msg);
                if (sendResult.getSendStatus() != SendStatus.SEND_OK){
                    addErrorProducer(one, msg);
                }
            } catch (MQClientException e) {
                if (errorProducer == null){
                    errorProducer = one;
                }
                else {
                    return false;
                }
            }
        }

        if (errorProducer != null){
            addErrorProducer(errorProducer, msg);
        }
        return true;
    }

    private void addErrorProducer(final DefaultMQProducer errorProducer, final Message msg){
        /*ConcurrentSet<Message> es = errorSendeds.get(errorProducer);
        if (es == null){
            es = new ConcurrentSet<Message>();
        }
        es.add(msg);
        errorSendeds.putIfAbsent(errorProducer, es);*/
        errorSendeds.add(msg);
    }

    private void repeat(){
        int poolSize = 1;
        int delay = 10 * 1000;
        int period = 10 * 1000;

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(poolSize);
        Runnable runnable = new Runnable() {
            public void run() {
                if (errorSendeds.isEmpty()){
                    /*for (Map.Entry<DefaultMQProducer, ConcurrentSet<Message>> one : errorSendeds.entrySet()) {
                        DefaultMQProducer producer = one.getKey();
                        ConcurrentSet<Message> es = one.getValue();
                        for(Message e : es){
                            try {
                                producer.send(e);
                            } catch (Exception e1) {
                                addErrorProducer(producer, e);
                            }
                        }
                    }*/

                    for (Message sended : errorSendeds) {
                        for (DefaultMQProducer one : producers) {
                            try {
                                one.send(sended);
                            } catch (Exception e) {
                                addErrorProducer(one, sended);
                            }
                        }
                    }
                }
            }
        };
        executor.scheduleAtFixedRate(runnable, delay, period, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        if (producers.isEmpty()) {
            throw new IllegalArgumentException(" there isn't producer which could be used  ! ");
        }
        for (DefaultMQProducer one : producers) {
            one.shutdown();
        }
    }

    public static Message buildMessage(final String topic, final String tag
            , final String key, final String messageBody) {
        Message msg = new Message();
        msg.setTopic(topic);

        if (tag != null && tag.length() > 0) {
            msg.setTags(tag);
        }
        if (key != null && key.length() > 0) {
            msg.setKeys(key);
        }
        msg.setBody(messageBody.getBytes());
        return msg;
    }
}
