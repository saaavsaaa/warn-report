package rocketDoubleWrite.listener.impl;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.common.message.MessageExt;
import rocketDoubleWrite.listener.MessageReceive;

import java.util.List;

public class BusinessRun implements MessageReceive {

    @Override
    public boolean exec(List<MessageExt> messages, ConsumeConcurrentlyContext Context) {
        boolean result = true;
        System.out.println(Thread.currentThread().getName() + " Receive New Messages: " + messages);
        for (MessageExt ext : messages){
            String key = ext.getKeys();
            String keys = ext.getProperty("KEYS");
            String value = new String(ext.getBody());
            System.out.println("key : " + key + "; KEYS :" + keys + "; body : " + value);
        }
        return result;
    }
}
