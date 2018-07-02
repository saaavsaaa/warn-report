package cn.tellwhy.jRedis;

import java.util.ResourceBundle;

/**
 * Created by ldb on 2016/6/12.
 */
public class RedisProperties {

    public RedisProperties(){
        ResourceBundle bundle = ResourceBundle.getBundle("jredis");
        ip = bundle.getString("j.redis.ip");
        auth = bundle.getString("j.redis.auth");

        String jPort = bundle.getString("j.redis.port");
        try {
            port = Integer.valueOf(jPort);
        }
        catch (Exception e){
            port = 6379;
        }

        String jMaxTotal = bundle.getString("j.redis.maxTotal");
        try {
            maxTotal = Integer.valueOf(jMaxTotal);
        }
        catch (Exception e){
            maxTotal = 300;
        }

        String jMaxIdle = bundle.getString("j.redis.maxIdle");
        try {
            maxIdle = Integer.valueOf(jMaxIdle);
        }
        catch (Exception e){
            maxIdle = 50;
        }

        String jMaxWait = bundle.getString("j.redis.maxWait");
        try {
            maxWait = Integer.valueOf(jMaxWait);
        }
        catch (Exception e){
            maxWait = 10000;
        }

        String jTimeout = bundle.getString("j.redis.timeout");
        try {
            timeout = Integer.valueOf(jTimeout);
        }
        catch (Exception e){
            timeout = 10000;
        }

        String jTestOnBorrow = bundle.getString("j.redis.testOnBorrow");
        try {
            testOnBorrow = Boolean.valueOf(jTestOnBorrow);
        }
        catch (Exception e){
            testOnBorrow = false;
        }

        String jTestWhileIdle = bundle.getString("j.redis.testWhileIdle");
        try {
            testWhileIdle = Boolean.valueOf(jTestWhileIdle);
        }
        catch (Exception e){
            testWhileIdle = false;
        }
    }

    //Redis服务器IP
    private String ip;

    //Redis的端口号
    private int port;

    //访问密码
    private String auth;

    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    //最大连接数, 应用自己评估，不要超过AliCloudDB for Redis每个实例最大的连接数
    private int maxTotal;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private int maxIdle;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private int maxWait;

    private int timeout;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private boolean testOnBorrow;

    private boolean testWhileIdle;

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getAuth() {
        return auth;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }
}
