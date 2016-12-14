package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.services.bbs.BadWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


/**
 * 违禁词
 * Created by ys on 2016-12-14.
 */
@Service("badWordsService")
public class BadWordsServiceImpl implements BadWordsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JedisPool jedisPool;
    private String badWordsCacheKey = "wd_app_bbs_badWords";

    @Override
    public String getBadWords() {
        String badWords = "";
        try(Jedis jedis = jedisPool.getResource()){
            badWords = jedis.get(badWordsCacheKey);
        }
        return badWords;
    }

    @Override
    public void setBadWords(String badWords) {
        try(Jedis jedis = jedisPool.getResource()){
            jedis.set(badWordsCacheKey, badWords);
        }
    }
}
