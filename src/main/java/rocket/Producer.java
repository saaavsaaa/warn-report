package rocket;
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
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;


public class Producer {
    final DefaultMQProducer producer;

    public Producer(final String address, final String producerGroup) throws MQClientException {
        producer = new DefaultMQProducer(producerGroup);
        producer.setInstanceName(Long.toString(System.currentTimeMillis()));
        producer.setNamesrvAddr(address);
        producer.setCompressMsgBodyOverHowmuch(Integer.MAX_VALUE);
        producer.start();
    }

    public SendResult send(final Message msg) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        return producer.send(msg);
    }

    public void shutdown(){
        producer.shutdown();
    }

    public static Message buildMessage(final String topic, final String tag
            , final String key, final String messageBody) {
        Message msg = new Message();
        msg.setTopic(topic);

        if(tag != null && tag.length() > 0) {
            msg.setTags(tag);
        }
        if(key != null && key.length() > 0) {
            msg.setKeys(key);
        }
        msg.setBody(messageBody.getBytes());
        return msg;
    }
}
