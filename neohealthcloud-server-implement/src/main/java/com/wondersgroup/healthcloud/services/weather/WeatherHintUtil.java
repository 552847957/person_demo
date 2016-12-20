package com.wondersgroup.healthcloud.services.weather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * ░░░░░▄█▌▀▄▓▓▄▄▄▄▀▀▀▄▓▓▓▓▓▌█
 * ░░░▄█▀▀▄▓█▓▓▓▓▓▓▓▓▓▓▓▓▀░▓▌█
 * ░░█▀▄▓▓▓███▓▓▓███▓▓▓▄░░▄▓▐█▌
 * ░█▌▓▓▓▀▀▓▓▓▓███▓▓▓▓▓▓▓▄▀▓▓▐█
 * ▐█▐██▐░▄▓▓▓▓▓▀▄░▀▓▓▓▓▓▓▓▓▓▌█▌
 * █▌███▓▓▓▓▓▓▓▓▐░░▄▓▓███▓▓▓▄▀▐█
 * █▐█▓▀░░▀▓▓▓▓▓▓▓▓▓██████▓▓▓▓▐█
 * ▌▓▄▌▀░▀░▐▀█▄▓▓██████████▓▓▓▌█▌
 * ▌▓▓▓▄▄▀▀▓▓▓▀▓▓▓▓▓▓▓▓█▓█▓█▓▓▌█▌
 * █▐▓▓▓▓▓▓▄▄▄▓▓▓▓▓▓█▓█▓█▓█▓▓▓▐█
 * <p>
 * Created by zhangzhixiu on 12/12/2016.
 */
@Component
public class WeatherHintUtil {

    private static final String prefix = "hc:sh:weather:";

    @Autowired
    private JedisPool pool;

    public String[] get(Integer aqi, Integer code, Integer temperture) {
        if (aqi > 100 || isBadWeather(code)) {
            return getOneHint(prefix + "bad");
        } else {
            if (temperture <= 10) {
                return getOneHint(prefix + "good:0");
            } else if (11 <= temperture && temperture <= 20) {
                return getOneHint(prefix + "good:10");
            } else if (21 <= temperture && temperture <= 30) {
                return getOneHint(prefix + "good:20");
            } else if (temperture > 30) {
                return getOneHint(prefix + "good:30");
            }
        }
        return new String[]{"", ""};
    }

    private Boolean isBadWeather(Integer code) {
        return !((25 < code && code < 35) || code == 44);
    }

    private String[] getOneHint(String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.srandmember(key).split(":");
        }
    }
}
