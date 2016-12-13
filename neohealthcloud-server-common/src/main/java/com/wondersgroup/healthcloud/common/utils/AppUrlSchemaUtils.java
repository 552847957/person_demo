package com.wondersgroup.healthcloud.common.utils;



/**
 * app schema url build
 * Created by ys on 16/12/12.
 */
public class AppUrlSchemaUtils {

    private static String basePath = "com.wondersgroup.healthcloud.${area}://user/";

    private static String getBasePath(String area){
        return basePath.replace("${area}", area);
    }

    /**
     * 圈子的话题详情
     */
    public static String bbsTopicView(int topic_id) {
        return getBasePath("3031") + "bbs/topic_detail?topic_id="+topic_id;
    }


}
