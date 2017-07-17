package com.wondersgroup.healthcloud.common.utils;



/**
 * app schema url build
 * Created by ys on 16/12/12.
 */
public class AppUrlSchemaUtils {

    public static String basePath = "com.wondersgroup.healthcloud.${area}://user/";
    
    public static String areaCode="3101";
    
    public static String getBasePath(String area){
        return basePath.replace("${area}", area);
    }

    /**
     * 圈子的话题详情
     */
    public static String bbsTopicView(int topic_id) {
        return getBasePath(areaCode) + "bbs/topic_detail?topic_id="+topic_id;
    }

    /**
     * 消息中心列表
     */
    public static String msgCenterView() {
        return getBasePath(areaCode) + "message_center";
    }


}
