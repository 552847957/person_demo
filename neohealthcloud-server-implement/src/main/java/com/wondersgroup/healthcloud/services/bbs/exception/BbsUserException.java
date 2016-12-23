package com.wondersgroup.healthcloud.services.bbs.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by ys on 16/08/11.
 *
 */
public class BbsUserException extends BaseException {

    private BbsUserException(Integer code, String msg) {
        super(code, msg, null);
    }

    public BbsUserException(String msg) {
        super(1031, msg, null);
    }

    public static BbsUserException userBanForReply(){
        return new BbsUserException(1021, "回复失败,您已被禁言");
    }

    public static BbsUserException userBanForPublishTopic(){
        return new BbsUserException(1021, "发布失败,您已被禁言");
    }

    public static BbsUserException banForever(){
        return new BbsUserException(1011, "用户已被禁言");
    }
}
