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

/**
 * Created by ldb on 2016/6/1.
 */
public class ProducerDouble implements IProducer {
    final ConcurrentSet<DefaultMQProducer> producers = new ConcurrentSet<DefaultMQProducer>();
    final ConcurrentSet<Message> errorSendeds = new ConcurrentSet<Message>();

    private int repeatDelay;
    private int repeatPeriod;

    public ProducerDouble() {

    }

    @Override
    public void init(List<String> addresses, String producerGroup) {
        if (addresses == null || addresses.isEmpty()) {
            System.out.println("case : Producer init error, reason : the arg addresses should have value");
            throw new IllegalArgumentException(" the arg addresses should have value ! ");
        }
        for (int i = 0; i < addresses.size(); i++) {
            producers.add(buildProducer(addresses.get(i), producerGroup + i));
        }
    }

    public void start(final int repeatDelay, final int repeatPeriod) throws MQClientException {
        for (DefaultMQProducer one : producers) {
            one.start();
        }
        this.repeatDelay = repeatDelay;
        this.repeatPeriod = repeatPeriod;
        repeat();
    }

    public boolean send(final Message msg) {
        if (producers.isEmpty()) {
            System.out.println("case : Producer send error, reason : there isn't producer which could be used");
            throw new IllegalArgumentException(" there isn't producer which could be used  ! ");
        }

        DefaultMQProducer errorProducer = null;
        for (DefaultMQProducer one : producers) {
            try {
                SendResult sendResult = one.send(msg);
                System.out.println(String.format("==============ip : %s, send NamesrvAddr : %s, send key: %s, topic :%s", one.getClientIP(), one.getNamesrvAddr(), msg.getKeys(), msg.getTopic()));
                if (sendResult != null && sendResult.getSendStatus() != SendStatus.SEND_OK) {
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
            if (sendResult != null && sendResult.getSendStatus() == SendStatus.SEND_OK) {
                errorSendeds.remove(msg);
            }
        } catch (Exception e) {
            System.out.println("reason : sendRepeat producer send error" + e.getMessage());
        }
    }

    public void shutdown() {
        if (producers.isEmpty()) {
            throw new IllegalArgumentException(" there isn't producer which could be used  ! ");
        }
        for (DefaultMQProducer one : producers) {
            one.shutdown();
        }
    }

    private DefaultMQProducer buildProducer(final String address, final String producerGroup) {
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        String instance = Long.toString(System.currentTimeMillis()) + producerGroup ;
        System.out.println(String.format("producer : %s, instance : %s", producerGroup, instance));
        producer.setInstanceName(instance);
        producer.setNamesrvAddr(address);
        producer.setCompressMsgBodyOverHowmuch(Integer.MAX_VALUE);
        return producer;
    }

    public void repeat() {
        int poolSize = 1;
        int delay = repeatDelay;
        int period = repeatPeriod;

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(poolSize);
        Runnable runnable = new Runnable() {
            public void run() {
                if (!errorSendeds.isEmpty()) {
                    for (Message sended : errorSendeds) {
                        for (DefaultMQProducer one : producers) {
                            System.out.println(String.format("===repeat==============ip : %s, send NamesrvAddr : %s, send key: %s, topic :%s", one.getClientIP(), one.getNamesrvAddr(), sended.getKeys(), sended.getTopic()));
                            sendRepeat(one, sended);
                        }
                    }
                }
            }
        };
        executor.scheduleAtFixedRate(runnable, delay, period, TimeUnit.MILLISECONDS);
    }

    void setRepeatDelay(int repeatDelay) {
        this.repeatDelay = repeatDelay;
    }

    void setRepeatPeriod(int repeatPeriod) {
        this.repeatPeriod = repeatPeriod;
    }
}
