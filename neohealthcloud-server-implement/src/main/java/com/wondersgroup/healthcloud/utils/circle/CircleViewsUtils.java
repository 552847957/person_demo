package com.wondersgroup.healthcloud.utils.circle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class CircleViewsUtils {
    private static final String keyPrefix = "cv:";
    
    @Autowired
    private JedisPool jedisPool;
    
    private Jedis jedis() {
        return jedisPool.getResource();
    }

    private String getCircleKey(String circleIds) {
        return keyPrefix + circleIds;
    }

    public Boolean[] isViewedOne(String[] circleIds, String userId){
        Jedis jedis = jedis();
        Boolean[] result = new Boolean[circleIds.length];
        for(int i=0; i<circleIds.length; i++) {
            String key = getCircleKey(circleIds[i]);
            result[i] = jedis.sismember(key, userId);
        }
        return result;
    }

    public Boolean viewOne(String circleId, String userId) {
        Jedis jedis = jedis();
        String key = getCircleKey(circleId);
        Boolean result = jedis.sadd(key, userId) == 1L;
        return result;
    }

    public Boolean isViewOne(String circleId, String userId) {
        Jedis jedis = jedis();
        String key = getCircleKey(circleId);
        Boolean result = jedis.sismember(key, userId);
        return result;
    }

    public Long totalViews(String circleId) {
        Jedis jedis = jedis();
        String key = getCircleKey(circleId);
        Long result = jedis.scard(key);
        return result;
    }
}
