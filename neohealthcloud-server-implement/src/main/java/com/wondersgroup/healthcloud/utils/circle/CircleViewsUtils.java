package com.wondersgroup.healthcloud.utils.circle;

import com.wondersgroup.healthcloud.utils.RedisConnectionFactory;

import redis.clients.jedis.Jedis;

public class CircleViewsUtils {
    private static final String keyPrefix = "cv:";

    private static Jedis jedis() {
        return RedisConnectionFactory.getConnection6380();
    }

    private static void returnResource(Jedis jedis) {
        RedisConnectionFactory.returnResource6380(jedis);
    }

    private static String getCircleKey(String circleIds) {
        return keyPrefix + circleIds;
    }

    public static Boolean[] isViewedOne(String[] circleIds, String userId){
        Jedis jedis = jedis();
        Boolean[] result = new Boolean[circleIds.length];
        for(int i=0; i<circleIds.length; i++) {
            String key = getCircleKey(circleIds[i]);
            result[i] = jedis.sismember(key, userId);
        }
        returnResource(jedis);
        return result;
    }

    public static Boolean viewOne(String circleId, String userId) {
        Jedis jedis = jedis();
        String key = getCircleKey(circleId);
        Boolean result = jedis.sadd(key, userId) == 1L;
        returnResource(jedis);
        return result;
    }

    public static Boolean isViewOne(String circleId, String userId) {
        Jedis jedis = jedis();
        String key = getCircleKey(circleId);
        Boolean result = jedis.sismember(key, userId);
        returnResource(jedis);
        return result;
    }

    public static Long totalViews(String circleId) {
        Jedis jedis = jedis();
        String key = getCircleKey(circleId);
        Long result = jedis.scard(key);
        returnResource(jedis);
        return result;
    }
}
