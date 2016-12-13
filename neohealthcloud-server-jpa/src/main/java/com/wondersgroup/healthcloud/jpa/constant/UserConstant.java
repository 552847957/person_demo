package com.wondersgroup.healthcloud.jpa.constant;


/**
 * Created by ys on 16-08-11.
 * 用户相关常量
 */
public class UserConstant {

    public static class UserCommentStatus{
        public final static int OK = 1;//正常

        public final static int USER_BAN = 2;//当前用户被禁言

        public final static int CIRCLE_BAN = 3;//话题所在圈子被禁用
    }

    public static class BanStatus{
        public final static int FOREVER = -1;//永久禁言

        public final static int OK = 0;

        public final static int HOUR_1 = 1;//禁言1小时

        public final static int HOUR_12 = 12;//禁言12小时

        public final static int DAY_1 = 24;//禁言24小时

        private final static Integer[] status = new Integer[]{-1, 0, 1, 12, 24};

        public static Boolean isVaildStatus(Integer banStatus){
            Boolean ok = false;
            for (Integer statusTmp : status){
                if (statusTmp == banStatus.intValue()){
                    ok = true;
                }
            }
            return ok;
        }
    }
}
