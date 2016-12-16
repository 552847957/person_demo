package com.wondersgroup.healthcloud.services.bbs.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by ys on 16/08/11.
 *
 */
public class CircleException extends BaseException {

    private CircleException(Integer code, String msg) {
        super(code, msg, null);
    }

    public static CircleException NotExistForReply(){
        return new CircleException(1041, "回复失败,该圈已禁用");
    }

    public static CircleException NotExistForPublishTopic(){
        return new CircleException(1041, "发布失败,该圈已禁用");
    }
}
