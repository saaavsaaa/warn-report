package cn.tellwhy.third.rocketDoubleWrite;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.Message;

import java.util.List;

/**
 * Created by ldb on 2016/6/1.
 */
public interface IProducer {
    void init(final List<String> addresses, final String producerGroup);
    void start(final int repeatDelay, final int repeatPeriod) throws MQClientException;
    boolean send(final Message msg);
    void shutdown();
    void repeat();
}
