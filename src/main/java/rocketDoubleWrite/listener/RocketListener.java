package rocketDoubleWrite.listener;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 *
 * 执行消息返回成功或失败，失败则重试
 */
public class RocketListener implements MessageListenerConcurrently {
    private MessageReceive receive;
    public RocketListener(MessageReceive receive){
        this.receive = receive;
    }

    public MessageReceive getReceive() {
        return receive;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        if(receive.exec(list,consumeConcurrentlyContext)){
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }else{
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
}
