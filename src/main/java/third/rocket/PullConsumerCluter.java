package third.rocket;
/**
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.alibaba.rocketmq.client.consumer.PullResult;
import com.alibaba.rocketmq.client.consumer.PullStatus;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * PullConsumer，订阅消息
 */
public class PullConsumerCluter {
    final DefaultMQPullConsumer consumer;

    public PullConsumerCluter(final String address, String consumerGroup) throws MQClientException {
        consumer = new DefaultMQPullConsumer();
        consumer.setNamesrvAddr(address);
        consumer.setConsumerGroup(consumerGroup);
        consumer.start();
    }

    public Set<MessageQueue> getMessageQueue(final String topic) throws MQClientException {
        return consumer.fetchSubscribeMessageQueues(topic);
    }

    public List<MessageExt> getMessage(final String topic, final Long offset, final String subExpression) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        List<MessageExt> result = new ArrayList<MessageExt>();
        Set<MessageQueue> mqs =  consumer.fetchSubscribeMessageQueues(topic);
        for (MessageQueue mq : mqs) {
            PullResult pullResult = consumer.pullBlockIfNotFound(mq, subExpression, offset, 1);
            if (pullResult.getPullStatus() == PullStatus.FOUND){
                List<MessageExt> extList = pullResult.getMsgFoundList();
                result.addAll(extList);
            }
        }
        return result;
    }

    public PullResult pullBlockIfNotFound(MessageQueue mq, String subExpression, long offset, int maxNums)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return consumer.pullBlockIfNotFound(mq, subExpression, offset, maxNums);
    }

    public void shutdown(){
        consumer.shutdown();
    }
}