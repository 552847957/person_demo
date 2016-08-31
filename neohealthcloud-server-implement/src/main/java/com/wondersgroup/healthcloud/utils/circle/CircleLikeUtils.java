package com.wondersgroup.healthcloud.utils.circle;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class CircleLikeUtils {
    private static final String keyPrefix = "c:";
    
    @Autowired
    private JedisPool jedisPool;
    
    private Jedis jedis() {
        return jedisPool.getResource();
    }

    private static String getCircleKey(String articleId) {
        return keyPrefix + articleId;
    }

    public Boolean[] isLikedOne(String[] articleIds, String userId){
        Jedis jedis = jedis();
        Boolean[] result = new Boolean[articleIds.length];
        for(int i=0; i<articleIds.length; i++) {
            String key = getCircleKey(articleIds[i]);
            result[i] = jedis.sismember(key, userId);
        }
        return result;
    }

    public String[] likeUserIds(String articleId){
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Set<String> set = jedis.smembers(key);
        String[] strs = new String[set.size()];
        int i = 0;
        for (String s : set) {
            strs[i]=s;
            i++;
        }
        return strs;
    }

    public Boolean likeOne(String articleId, String userId) {
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Boolean result = jedis.sadd(key, userId) == 1L;
        return result;
    }

    public Boolean cancelLikeOne(String articleId, String userId) {
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Boolean result = jedis.srem(key, userId) == 1L;
        return result;
    }

    public Boolean isLikeOne(String articleId, String userId) {
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Boolean result = jedis.sismember(key, userId);
        return result;
    }

    public Long totalLike(String articleId) {
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Long result = jedis.scard(key);
        return result;
    }
}
