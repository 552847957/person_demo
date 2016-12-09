package com.wondersgroup.healthcloud.jpa.constant;

/**
 * Created by ys on 16-08-11.
 * 话题相关常量
 */
public class TopicConstant {

    /**
     * 返回app的状态定义
     */
    public static class AppListStatus{

        public final static int OK = 1;

        public final static int DELETE=2;//已删除

        public final static int USER_BAN=3;//回复用户被删除
    }

    public static class Status{
        public final static int WAIT_PUBLISH = -1;

        public final static int WAIT_VERIFY = 0;

        public final static int OK = 1;

        public final static int FORBID_REPLY = 2;//禁止回复

        public final static int USER_DELETE = 3;//用户删除

        public final static int ADMIN_DELETE = 4;//管理员删除

        public static boolean isDelStatus(Integer status){
            return status == USER_DELETE || status == ADMIN_DELETE;
        }
    }
}
