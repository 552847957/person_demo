package com.wondersgroup.healthcloud.api.shiro;

import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * Created by Administrator on 2015/11/23.
 */
public class PasswordHelper {

    private final static String algorithmName = "md5";
    private final static int hashIterations = 1;

    public static String encryptPassword(String password) {

        return new SimpleHash(
                algorithmName,
                password,
                null,
                hashIterations).toHex();
    }

}
