package cn.tellwhy.third.rocketDoubleWrite.listener;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.common.message.MessageExt;

import java.util.List;

public interface MessageReceive {
    boolean exec(List<MessageExt> messages, ConsumeConcurrentlyContext Context);
}
