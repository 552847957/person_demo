package com.wondersgroup.healthcloud.common.utils;


import org.apache.commons.lang3.StringUtils;

/**
 * Created by zhangzhixiu on 15/9/6.
 */
public class Debug {
    private static final Boolean sandbox;

    static {
        String env = System.getProperty("spring.profiles.active");
        if (env == null) {
            env = System.getenv("spring.profiles.active");
        }
        sandbox = StringUtils.equals("de", env) || StringUtils.equals("te", env);
    }


    public static Boolean sandbox() {
        return sandbox;
    }
}
