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

        public final static Integer OK = 1;

        public final static Integer DELETE=2;//已删除

        public final static Integer USER_BAN=3;//回复用户被删除
    }

    public static class Status{
        public final static Integer WAIT_PUBLISH = -1;

        public final static Integer WAIT_VERIFY = 0;

        public final static Integer OK = 1;

        public final static Integer FORBID_REPLY = 2;//禁止回复

        public final static Integer USER_DELETE = 3;//用户删除

        public final static Integer ADMIN_DELETE = 4;//管理员删除

        public static boolean isDelStatus(Integer status){
            return status.intValue() == USER_DELETE || status.intValue() == ADMIN_DELETE;
        }
    }
}
