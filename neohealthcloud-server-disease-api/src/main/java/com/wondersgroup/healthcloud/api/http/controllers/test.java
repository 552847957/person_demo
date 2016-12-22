package com.wondersgroup.healthcloud.api.http.controllers;

import java.util.Map;
import java.util.Properties;

/**
 * Created by zhuchunliu on 2016/12/21.
 */
public class test {
    public static void main(String[] args) {
        Map<String ,String> map = System.getenv();
        for(String key : map.keySet()){
            System.err.println(key +"  "+map.get(key));
        }


        Properties pro = System.getProperties();

        for(Object key : pro.keySet()){
            System.out.println(key +" === "+pro.get(key));
        }
    }
}
