package com.wondersgroup.healthcloud.utils.circle;

import java.util.Set;

import com.wondersgroup.healthcloud.utils.RedisConnectionFactory;

import redis.clients.jedis.Jedis;

public class CircleLikeUtils {
    private static final String keyPrefix = "c:";

    private static Jedis jedis() {
        return RedisConnectionFactory.getConnection6380();
    }

    private static void returnResource(Jedis jedis) {
        RedisConnectionFactory.returnResource6380(jedis);
    }

    private static String getCircleKey(String articleId) {
        return keyPrefix + articleId;
    }

    public static Boolean[] isLikedOne(String[] articleIds, String userId){
        Jedis jedis = jedis();
        Boolean[] result = new Boolean[articleIds.length];
        for(int i=0; i<articleIds.length; i++) {
            String key = getCircleKey(articleIds[i]);
            result[i] = jedis.sismember(key, userId);
        }
        returnResource(jedis);
        return result;
    }

    public static String[] likeUserIds(String articleId){
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Set<String> set = jedis.smembers(key);
        String[] strs = new String[set.size()];
        int i = 0;
        for (String s : set) {
            strs[i]=s;
            i++;
        }
        returnResource(jedis);
        return strs;
    }

    public static Boolean likeOne(String articleId, String userId) {
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Boolean result = jedis.sadd(key, userId) == 1L;
        returnResource(jedis);
        return result;
    }

    public static Boolean cancelLikeOne(String articleId, String userId) {
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Boolean result = jedis.srem(key, userId) == 1L;
        returnResource(jedis);
        return result;
    }

    public static Boolean isLikeOne(String articleId, String userId) {
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Boolean result = jedis.sismember(key, userId);
        returnResource(jedis);
        return result;
    }

    public static Long totalLike(String articleId) {
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Long result = jedis.scard(key);
        returnResource(jedis);
        return result;
    }
}
