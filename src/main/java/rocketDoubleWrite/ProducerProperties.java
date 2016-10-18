package rocketDoubleWrite;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by ldb on 2016/5/18.
 */
public class ProducerProperties {
    private String producerGroup;
    private String topic;
    private static String tag;
    private List<String> addresses;

    private int repeatDelay;
    private int repeatPeriod;

    public static void main(String[] args) {
        ResourceBundle bundle = ResourceBundle.getBundle("resources.rocket");
        String addrs = bundle.getString("srv.addresses");
        List<String> addresses = new ArrayList<String>();
        if (addrs == null || addrs.isEmpty()){
            throw new IllegalArgumentException("rocket.properties srv.addresses should be assigned");
        }
        String[] splits = addrs.split(",");
        for (String one: splits) {
            addresses.add(one);
        }

        System.out.print(addresses);
    }

    public ProducerProperties(){
        addresses = new ArrayList<String>();
        ResourceBundle bundle = ResourceBundle.getBundle("resources.rocket");
        producerGroup = bundle.getString("producer.group");
        topic = bundle.getString("register.topic");
        tag = "";
        String addrs = bundle.getString("srv.addresses");
        if (addrs == null || addrs.isEmpty()){
            throw new IllegalArgumentException("rocket.properties srv.addresses should be assigned");
        }
        String[] splits = addrs.split(",");
        for (String one: splits) {
            addresses.add(one);
        }

        String delay = bundle.getString("repeat.deloy.second");
        String period = bundle.getString("repeat.period.second");
        try {
            repeatDelay = Integer.valueOf(delay) * 1000;
        }
        catch (Exception e){
            repeatDelay = 10 * 1000;
        }
        try {
            repeatPeriod = Integer.valueOf(period) * 1000;
        }
        catch (Exception e){
            repeatPeriod = 10 * 1000;
        }
    }

    public static String getTag() {
        return tag;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public String getTopic() {
        return topic;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public int getRepeatDelay() {
        return repeatDelay;
    }

    public int getRepeatPeriod() {
        return repeatPeriod;
    }
}
