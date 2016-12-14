package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.services.bbs.BadWordsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${bbs_badwords_open}")
    private String isDealBadWords = "1";//是否处理违禁词

    @Override
    public Boolean isDealBadWords(){
        return "1".equals(isDealBadWords);
    }

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
        badWords = this.dealWords(badWords);
        try(Jedis jedis = jedisPool.getResource()){
            jedis.set(badWordsCacheKey, badWords);
        }
    }

    @Override
    public String dealBadWords(String text){
        if (!isDealBadWords()){
            return text;
        }
        return StringUtils.isEmpty(text) ? "" : text.replaceAll(getBadWords(), "**");
    }


    private String dealWords(String badWords){
        badWords = null == badWords ? "" : badWords;
        badWords = badWords.replaceAll(" ", "");
        badWords = badWords.replaceAll("\\|+", "|");
        badWords = badWords.startsWith("|") ? badWords.substring(1) : badWords;
        badWords = badWords.endsWith("|") ? badWords.substring(0, badWords.length()-1) : badWords;
        return badWords;
    }

    public static void main(String[] args) {
        String info = "个哥哥飞蛾瓦妹妹温家宝房和外观符合我来开发和外卡号";
        String badWords = " |温家宝|||||家   宝|外观|||";
        badWords = null == badWords ? "" : badWords;
        badWords = badWords.replaceAll(" ", "");
        badWords = badWords.replaceAll("\\|+", "|");
        badWords = badWords.startsWith("|") ? badWords.substring(1) : badWords;
        badWords = badWords.endsWith("|") ? badWords.substring(0, badWords.length()-1) : badWords;
        System.out.println(badWords);
    }
}
