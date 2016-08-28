package com.wondersgroup.healthcloud.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.wondersgroup.healthcloud.common.utils.PropertiesUtils;


/**
 * redis connetion factroy
 * <p/>
 * Created by zhangzhixiu on 15/4/15.
 */
public class RedisConnectionFactory {
    private static final JedisPool pool6379;//用于积分
    private static final JedisPool pool6380;//用于access-token与问答点赞

    static {//初始化连接池
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(100);
        config.setMinIdle(1);
        config.setTestOnBorrow(true);
        config.setTestWhileIdle(true);
        config.setTestOnReturn(false);
        config.setBlockWhenExhausted(true);
        config.setJmxEnabled(true);
        config.setJmxNamePrefix("jedis-pool");
        config.setNumTestsPerEvictionRun(100);
        config.setTimeBetweenEvictionRunsMillis(60000);
        config.setMinEvictableIdleTimeMillis(300000);
        config.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
        config.setTimeBetweenEvictionRunsMillis(600 * 1000);

        if (PropertiesUtils.get("REDIS1PWD") == null) {
            pool6379 = new JedisPool(config, PropertiesUtils.get("REDIS1HOST"), Integer.valueOf(PropertiesUtils.get("REDIS1PORT")));
        } else {
            pool6379 = new JedisPool(config, PropertiesUtils.get("REDIS1HOST"), Integer.valueOf(PropertiesUtils.get("REDIS1PORT")), 600, PropertiesUtils.get("REDIS1PWD"));
        }

        if (PropertiesUtils.get("REDIS2PWD") == null) {
            pool6380 = new JedisPool(config, PropertiesUtils.get("REDIS2HOST"), Integer.valueOf(PropertiesUtils.get("REDIS2PORT")));
        } else {
            pool6380 = new JedisPool(config, PropertiesUtils.get("REDIS2HOST"), Integer.valueOf(PropertiesUtils.get("REDIS2PORT")), 600, PropertiesUtils.get("REDIS2PWD"));
        }
    }

    public static Jedis getConnection6379() {
        return pool6379.getResource();
    }

    public static void returnResource6379(Jedis jedis) {
        pool6379.returnResource(jedis);
    }

    public static Jedis getConnection6380() {
        return pool6380.getResource();
    }

    public static void returnResource6380(Jedis jedis) {
        pool6380.returnResource(jedis);
    }
}
