package com.wondersgroup.healthcloud.common.utils;

import java.util.UUID;

public class UUIDUtil {

    private UUIDUtil(){}

    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
