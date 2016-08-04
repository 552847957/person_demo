package com.wondersgroup.healthcloud.services.user.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/8/4.
 */
public class ErrorSmsRequestException extends BaseException {
    public  ErrorSmsRequestException(){
        super(1003,"未知的短信请求",null);
    }
}
