package rocketDoubleWrite;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import io.netty.util.internal.ConcurrentSet;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by ldb on 2016/5/17.
 */
public final class ProducerClient {
    private static AtomicReference<ProducerDouble> ar = new AtomicReference<ProducerDouble>();
    private volatile static ProducerDouble producer = null;
    private static ProducerPropeties properties = null;

    private ProducerClient(){
    	buildProducer();
    }

    private static ProducerDouble buildProducer() {
        try {
            properties = new ProducerPropeties();
            producer = new ProducerDouble(properties.getAddresses(), properties.getProducerGroup());
            producer.start(properties.getRepeatDelay(), properties.getRepeatPeriod());
        } catch (MQClientException e) {
            System.out.println("reason : ProducerClient instance error" + e.getErrorMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("reason : rocket.properties srv.addresses don't be assigned or repeat error" + e.getMessage());
        }
        return producer;
    }

    public static boolean send(final String key, final String content) {
        ProducerClient.getInstance();
        Message msg = ProducerDouble.buildMessage(properties.getTopic(), properties.getTag(), key, content);
        System.out.println(String.format("case : send to rocket, topic : %s, tag : %s, key : %s, content : %s", properties.getTopic(), properties.getTag(), key, content));
        return producer.send(msg);
    }

    private static ProducerDouble getInstance() {
        if (producer != null) {
            return producer;
        }
        ar.compareAndSet(producer, buildProducer());
        
        return producer;
    }

    public static void main(String[] args) {
        int a = 2;
        for (int i = 0; i < a; i++) {
            send("a1", "{'a12':'121'}");
        }
    }
}

class ProducerDouble {
    final ConcurrentSet<DefaultMQProducer> producers = new ConcurrentSet<DefaultMQProducer>();
    final ConcurrentSet<Message> errorSendeds = new ConcurrentSet<Message>();

    private int repeatDelay;
    private int repeatPeriod;

    public ProducerDouble(final List<String> addresses, final String producerGroup) throws MQClientException {
        if (addresses == null || addresses.isEmpty()) {
            System.out.println("case : Producer init error, reason : the arg addresses should have value");
            throw new IllegalArgumentException(" the arg addresses should have value ! ");
        }
        for (int i = 0; i < addresses.size(); i++) {
        	producers.add(buildProducer(addresses.get(i), producerGroup + i));
        }
        /*this.repeatDelay = repeatDelay;
        this.repeatPeriod = repeatPeriod;
        repeat();*/
    }

    void start(final int repeatDelay, final int repeatPeriod) throws MQClientException {
        for (DefaultMQProducer one : producers) {
            one.start();
        }
        this.repeatDelay = repeatDelay;
        this.repeatPeriod = repeatPeriod;
        repeat();
    }

    boolean send(final Message msg) {
        if (producers.isEmpty()) {
            System.out.println("case : Producer send error, reason : there isn't producer which could be used");
            throw new IllegalArgumentException(" there isn't producer which could be used  ! ");
        }

        DefaultMQProducer errorProducer = null;
        for (DefaultMQProducer one : producers) {
            try {
                SendResult sendResult = one.send(msg);
                if (sendResult.getSendStatus() != SendStatus.SEND_OK){
                    errorSendeds.add(msg);
                }
            } catch (Exception e) {
                System.out.println("reason : producer send error" + e.getMessage());
                if (errorProducer == null) {
                    errorProducer = one;
                } else {
                    return false;
                }
            }
        }

        if (errorProducer != null) {
            errorSendeds.add(msg);
        }
        return true;
    }

    /*重试全部重发，保证两个队列都是全的，完全消费任意队列都可以完整消费
    private void addErrorProducer(final DefaultMQProducer errorProducer, final Message msg){
        ConcurrentSet<Message> es = errorSendeds.get(errorProducer);
        es.add(msg);
        errorSendeds.putIfAbsent(errorProducer, es);
    }*/

    void sendRepeat(final DefaultMQProducer producer, final Message msg) {
        try {
            SendResult sendResult = producer.send(msg);
            if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
                errorSendeds.remove(msg);
            }
        } catch (Exception e) {
            System.out.println("reason : sendRepeat producer send error" + e.getMessage());
        }
    }

    void shutdown() {
        if (producers.isEmpty()) {
            throw new IllegalArgumentException(" there isn't producer which could be used  ! ");
        }
        for (DefaultMQProducer one : producers) {
            one.shutdown();
        }
    }

    private DefaultMQProducer buildProducer(final String address, final String producerGroup) {
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setInstanceName(Long.toString(System.currentTimeMillis()));
        producer.setNamesrvAddr(address);
        producer.setCompressMsgBodyOverHowmuch(Integer.MAX_VALUE);
        return producer;
    }

    private void repeat() {
        int poolSize = 1;
        int delay = repeatDelay;
        int period = repeatPeriod;

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(poolSize);
        Runnable runnable = new Runnable() {
            public void run() {
                if (!errorSendeds.isEmpty()) {
                    for (Message sended : errorSendeds) {
                        for (DefaultMQProducer one : producers) {
                            sendRepeat(one, sended);
                        }
                    }
                }
            }
        };
        executor.scheduleAtFixedRate(runnable, delay, period, TimeUnit.MILLISECONDS);
    }

    static Message buildMessage(final String topic, final String tag, final String key, final String messageBody) {
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
