package com.wondersgroup.healthcloud.services.user.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/8/9.
 */
public class ErrorUpdateUserInfoException extends BaseException {
    public ErrorUpdateUserInfoException(String msg){
        super(1030,msg,null);
    }

    public ErrorUpdateUserInfoException(int code, String msg){
        super(code,msg,null);
    }
}
