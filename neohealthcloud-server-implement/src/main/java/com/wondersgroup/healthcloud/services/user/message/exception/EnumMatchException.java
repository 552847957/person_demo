package com.wondersgroup.healthcloud.services.user.message.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by jialing.yao on 2016-8-15.
 */
public class EnumMatchException extends BaseException {

    public EnumMatchException(){
        super(1000, "枚举匹配异常.");
    }

    public EnumMatchException(String msg) {
        super(1000, msg);
    }
}
