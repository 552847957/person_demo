package com.wondersgroup.healthcloud.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by jialing.yao on 2016-8-17.
 */
public class MapChecker {

    public static void checkMap(Map map){
        checkMap(map, true, false);
    }
    public static void checkMap(Map map, boolean NULL, boolean KONG){
        Set<Map.Entry> mapEntries = map.entrySet();
        Iterator it = mapEntries.iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            if(NULL){
                if(entry.getValue()==null){
                    entry.setValue("");
                    continue;
                }
            }
            if(KONG){
                if(entry.getValue().toString().equalsIgnoreCase("")){
                    it.remove();
                    continue;
                }
            }
        }
    }
}
