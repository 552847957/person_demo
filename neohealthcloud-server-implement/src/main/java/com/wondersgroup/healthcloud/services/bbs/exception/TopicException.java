package com.wondersgroup.healthcloud.services.bbs.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by ys on 16/08/11.
 *
 */
public class TopicException extends BaseException {

    public TopicException(int code, String msg) {
        super(code, msg, null);
    }

    public static TopicException notExist(){
        return new TopicException(1034, "抱歉，该话题已被删除");
    }

    public static TopicException publishUserBanForever(){
        return new TopicException(1035, "抱歉，该话题已被删除");
    }

    public static TopicException waitVerify(){
        return new TopicException(1036, "话题正在审核中...");
    }

    public static TopicException deleteForReport(){
        return new TopicException(1033, "举报失败,话题无效");
    }

    public static TopicException deleteByUser(){
        return new TopicException(1031, "抱歉，该话题已被删除");
    }

    public static TopicException deleteByAdmin(){
        return new TopicException(1032, "抱歉，该话题已被删除");
    }
}
