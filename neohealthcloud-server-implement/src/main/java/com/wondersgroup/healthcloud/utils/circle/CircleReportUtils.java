package com.wondersgroup.healthcloud.utils.circle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class CircleReportUtils {
    private static final String keyPrefix = "cr:";
    
    @Autowired
    private JedisPool jedisPool;
    
    private Jedis jedis() {
        return jedisPool.getResource();
    }

    private String getCircleKey(String articleId) {
        return keyPrefix + articleId;
    }

    public Boolean[] isReportedOne(String[] articleIds, String userId){
        Jedis jedis = jedis();
        Boolean[] result = new Boolean[articleIds.length];
        for(int i=0; i<articleIds.length; i++) {
            String key = getCircleKey(articleIds[i]);
            result[i] = jedis.sismember(key, userId);
        }
        return result;
    }

    public Boolean reportOne(String articleId, String userId, String reportType) {
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Boolean result = jedis.sadd(key, userId+"#"+reportType) == 1L;//todo 备忘,针对文章的每种举报类型做了缓存, 其他接口也需要修改
        return result;
    }

    public Boolean cancelReportOne(String articleId, String userId) {
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Boolean result = jedis.srem(key, userId) == 1L;
        return result;
    }

    public Boolean isReportOne(String articleId, String userId) {
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Boolean result = jedis.sismember(key, userId);
        return result;
    }

    public Long totalReport(String articleId) {
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Long result = jedis.scard(key);
        return result;
    }
}
