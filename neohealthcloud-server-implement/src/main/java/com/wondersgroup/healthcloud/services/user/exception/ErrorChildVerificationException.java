package com.wondersgroup.healthcloud.services.user.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/9/18.
 */
public class ErrorChildVerificationException extends BaseException {

    public ErrorChildVerificationException(String msg){
        super(1082,msg,null);
    }

    public ErrorChildVerificationException(int code,String msg){
        super(code,msg,null);
    }
}
