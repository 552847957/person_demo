package com.wondersgroup.healthcloud.jpa.constant;

/**
 * Created by ys on 16-08-11.
 * 评论相关常量
 */
public class CommentConstant {

    /**
     * 返回app的状态定义
     */
    public static class AppListStatus{

        public final static Integer OK = 1;

        public final static Integer DELETE=2;//已删除

        public final static Integer USER_BAN=3;//回复用户被删除
    }

    public static class Status{

        public final static Integer WAIT_VERIFY = 0;

        public final static Integer OK = 1;

        public final static Integer DELETE=2;//已删除
    }

}
