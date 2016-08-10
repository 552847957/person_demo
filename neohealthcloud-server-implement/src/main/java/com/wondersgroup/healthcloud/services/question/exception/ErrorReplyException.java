package com.wondersgroup.healthcloud.services.question.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by dukuanxin on 16/8/10
 *
 */
public class ErrorReplyException extends BaseException {

    public ErrorReplyException(String msg){
        super(2004, msg, null);
    }
}
