package com.wondersgroup.healthcloud.jpa.constant;


import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by ys on 16-09-13.
 * 举报相关常量
 */
public class ReportConstant {

    public static class ReportType{

        public final static int TOPIC = 1;//话题举报

        public final static int COMMENT = 2;//评论举报

    }

    public static class ReportStatus{

        public final static int WAIT_REVIEW = 0;//等待处理

        public final static int SET_OK = 1;//已处理，忽略举报

        public final static int DEL_TARGET = 2;//已处理，删除举报内容
    }

    /**
     * 举报原因的定义
     */
    public static class ReportReason{

        public final static Map<Integer, String> reasonList;

        static{
            reasonList = Maps.newLinkedHashMap();
            reasonList.put(1, "垃圾广告");
            reasonList.put(2, "敏感信息");
            reasonList.put(3, "虚假中奖");
            reasonList.put(4, "淫秽色情");
            reasonList.put(5, "不实信息");
        }

    }

}
