package jRedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by ldb on 2016/6/12.
 */
public class JRedisPool {
    private static RedisProperties properties;
    private static JedisPool jedisPool;

    private JRedisPool(){
        System.out.println("case : JRedisPool instance");
        buildRedisPool();
    }

    public static JRedisPool getInstance()
    {
        return Nested.instance;
    }

    //在第一次被引用时被加载
    static class Nested
    {
        private static JRedisPool instance = new JRedisPool();
    }

    private void buildRedisPool(){
        properties = new RedisProperties();
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(properties.getMaxTotal());
            config.setMaxIdle(properties.getMaxIdle());
            config.setMaxWaitMillis(properties.getMaxWait());
            //在获取连接的时候检查有效性, 默认false
            config.setTestOnBorrow(properties.isTestOnBorrow());
            //在空闲时检查有效性, 默认false
            config.setTestWhileIdle(properties.isTestWhileIdle());

            jedisPool = new JedisPool(config, properties.getIp(), properties.getPort(), properties.getTimeout(), properties.getAuth());
        } catch (Exception e) {
            System.out.println("case : new JedisPoolConfig or JedisPool error, detail : " + e.getMessage());
        }
    }

    /**
     * 获取Jedis实例
     * @return
     */
    public static Jedis getJedis() {
        try {
            JRedisPool.getInstance();
            Jedis resource = jedisPool.getResource();
            return resource;
        } catch (Exception e) {
            System.out.println("case : jedisPool.getResource, detail : " + e.getMessage());
            return null;
        }
    }
}
