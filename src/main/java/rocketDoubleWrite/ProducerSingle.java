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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ldb on 2016/6/1.
 */
class ProducerSingle implements IProducer {
    DefaultMQProducer producer = null;
    final ConcurrentSet<Message> errorSendeds = new ConcurrentSet<Message>();
    final AtomicInteger repeatCount = new AtomicInteger();
    private int repeatDelay;
    private int repeatPeriod;

    public ProducerSingle() {
    }

    @Override
    public void init(List<String> addresses, String producerGroup) {
        if (addresses == null || addresses.isEmpty()) {
            System.out.println("case : Producer init error, reason : the arg addresses should have value");
            throw new IllegalArgumentException(" the arg addresses should have value ! ");
        }
        String srvAddrs = addresses.get(0);
        if (addresses.size() > 1){
            for (int i = 1; i < addresses.size(); i++) {
                srvAddrs += new StringBuilder(";").append(addresses.get(i)).toString();
            }
        }
        System.out.println("srvAddrs : " + srvAddrs);
        producer = new DefaultMQProducer(producerGroup);
        producer.setInstanceName(Long.toString(System.currentTimeMillis()));
        producer.setNamesrvAddr(srvAddrs);
        producer.setCompressMsgBodyOverHowmuch(Integer.MAX_VALUE);
    }

    @Override
    public void start(int repeatDelay, int repeatPeriod) throws MQClientException {
        producer.start();
        this.repeatDelay = repeatDelay;
        this.repeatPeriod = repeatPeriod;
        repeat();
    }

    @Override
    public boolean send(final Message msg) {
        SendResult sendResult = null;
        try {
            sendResult = producer.send(msg);
        } catch (Exception e) {
            System.out.println("reason : producer send error" + e.getMessage());
            errorSendeds.add(msg);
            System.out.println("error count : " + errorSendeds.size());
            return false;
        }

        System.out.println(String.format("case : ProducerSingle send, ip : %s, send NamesrvAddr : %s, send key: %s, topic :%s", producer.getClientIP(), producer.getNamesrvAddr(), msg.getKeys(), msg.getTopic()));
        if (sendResult != null && sendResult.getSendStatus() == SendStatus.SEND_OK) {
            return true;
        } else {
            System.out.println(String.format("case : send result is %s, reason : producer send error", sendResult.getSendStatus().toString()));
            errorSendeds.add(msg);
            System.out.println("error count : " + errorSendeds.size());
            return false;
        }
    }

    public void shutdown() {
        System.out.println("case : producer shutdown !");
        producer.shutdown();
    }

    @Override
    public void repeat() {
        int poolSize = 1;
        int delay = repeatDelay;
        int period = repeatPeriod;

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(poolSize);
        Runnable runnable = new Runnable() {
            public void run() {
                if (!errorSendeds.isEmpty()) {
                    for (Message one : errorSendeds) {
                        System.out.println(String.format("case : ProducerSingle repeat, ip : %s, send NamesrvAddr : %s, send key: %s, topic :%s", producer.getClientIP(), producer.getNamesrvAddr(), one.getKeys(), one.getTopic()));
                        try {
                            SendResult sendResult = producer.send(one);
                            if (sendResult != null && sendResult.getSendStatus() == SendStatus.SEND_OK) {
                                errorSendeds.remove(one);
                                System.out.println("error count : " + errorSendeds.size());
                            }
                            System.out.println("repeat count :" + repeatCount.incrementAndGet());
                        } catch (Exception e) {
                            System.out.println("reason : sendRepeat ProducerSingle send error" + e.getMessage());
                            System.out.println("repeat count :" + repeatCount.incrementAndGet());
                        }
                    }
                }
            }
        };
        executor.scheduleAtFixedRate(runnable, delay, period, TimeUnit.MILLISECONDS);
    }
}
