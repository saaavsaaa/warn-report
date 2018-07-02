package cn.tellwhy.third.rocketDoubleWrite;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ldb on 2016/5/17.
 */
public final class ProducerClient {
    private static final Logger logger = LoggerFactory.getLogger(ProducerClient.class);
    
    private volatile static IProducer producer = new ProducerSingle();
    private static ProducerProperties properties = null;
    
    private ProducerClient(){
        System.out.println("case : ProducerClient instance");
    	buildProducer();
    }  
    
    public static ProducerClient getInstance()  
    {  
        return Nested.instance;       
    }  

    //在第一次被引用时被加载  
    static class Nested  
    {
        private static ProducerClient instance = new ProducerClient();  
    }
    
    
    private static IProducer buildProducer() {
        try {
            properties = new ProducerProperties();
            producer.init(properties.getAddresses(), properties.getProducerGroup());
            producer.start(properties.getRepeatDelay(), properties.getRepeatPeriod());
        } catch (MQClientException e) {
            logger.error("reason : ProducerClient instance error", e);
        } catch (IllegalArgumentException e) {
            logger.error("reason : rocket.properties srv.addresses don't be assigned or repeat error", e);
        }
        return producer;
    }

    public static boolean send(final String key, final String content) {
        ProducerClient.getInstance();
        return send(key, content, properties.getTopic());
    }

    public static boolean send(final String key, final String content, final String topic) {
        ProducerClient.getInstance();
        return send(key, content, topic, properties.getTag());
    }

    public static boolean send(final String key, final String content, final String topic, final String tag) {
        ProducerClient.getInstance();
        Message msg = buildMessage(topic, tag, key, content);
        logger.info("case : send to rocket, topic : {}, tag : {}, key : {}, content : {}", topic, tag, key, content);
        return producer.send(msg);
    }

    private static Message buildMessage(final String topic, final String tag
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

    public static void main(String[] args) {
        send("201606011", "{'a12':'121'}", "testTopic", "*");
    }
}
