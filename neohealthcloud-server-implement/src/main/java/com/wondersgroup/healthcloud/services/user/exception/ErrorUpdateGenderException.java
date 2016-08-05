package com.wondersgroup.healthcloud.services.user.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/8/5.
 */
public class ErrorUpdateGenderException extends BaseException {

    public ErrorUpdateGenderException(String msg){
        super(1020,msg,null);
    }

    public ErrorUpdateGenderException(int code, String msg){
        super(code,msg,null);
    }
}
