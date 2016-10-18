package service;

import com.alibaba.rocketmq.client.exception.MQClientException;

public interface PushConsumerService {
    void run() throws MQClientException, InterruptedException;
}
