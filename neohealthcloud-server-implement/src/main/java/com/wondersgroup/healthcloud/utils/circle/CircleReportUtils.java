package com.wondersgroup.healthcloud.utils.circle;

import com.wondersgroup.healthcloud.utils.RedisConnectionFactory;

import redis.clients.jedis.Jedis;

public class CircleReportUtils {
    private static final String keyPrefix = "cr:";

    private static Jedis jedis() {
        return RedisConnectionFactory.getConnection6380();
    }

    private static void returnResource(Jedis jedis) {
        RedisConnectionFactory.returnResource6380(jedis);
    }

    private static String getCircleKey(String articleId) {
        return keyPrefix + articleId;
    }

    public static Boolean[] isReportedOne(String[] articleIds, String userId){
        Jedis jedis = jedis();
        Boolean[] result = new Boolean[articleIds.length];
        for(int i=0; i<articleIds.length; i++) {
            String key = getCircleKey(articleIds[i]);
            result[i] = jedis.sismember(key, userId);
        }
        returnResource(jedis);
        return result;
    }

    public static Boolean reportOne(String articleId, String userId, String reportType) {
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Boolean result = jedis.sadd(key, userId+"#"+reportType) == 1L;//todo 备忘,针对文章的每种举报类型做了缓存, 其他接口也需要修改
        returnResource(jedis);
        return result;
    }

    public static Boolean cancelReportOne(String articleId, String userId) {
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Boolean result = jedis.srem(key, userId) == 1L;
        returnResource(jedis);
        return result;
    }

    public static Boolean isReportOne(String articleId, String userId) {
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Boolean result = jedis.sismember(key, userId);
        returnResource(jedis);
        return result;
    }

    public static Long totalReport(String articleId) {
        Jedis jedis = jedis();
        String key = getCircleKey(articleId);
        Long result = jedis.scard(key);
        returnResource(jedis);
        return result;
    }
}
