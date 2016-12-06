package com.wondersgroup.healthcloud.services.weather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
 * Created by zhangzhixiu on 02/12/2016.
 */
@Component
public class WeatherCache {

    @Autowired
    private JedisPool pool;


    private static final String keyPrefix = "hc:sh:weather:";

    public enum Type {
        BRIEF("brief"),
        ALL("all");

        private String prefix;

        Type(String prefix) {
            this.prefix = keyPrefix + prefix + ":";
        }

        public String key() {
            return prefix;
        }
    }

    private String key(Type type, String code) {
        return type.prefix + code;
    }

    public void save(Type type, String code, String value) {

    }

    public String get(Type type, String code) {
        return "";
    }
}
