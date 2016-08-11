package com.wondersgroup.healthcloud.services.user.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/8/11.
 */
public class ErrorAnonymousAccountException extends BaseException {

    public ErrorAnonymousAccountException(){
        super(1040,"不存在的用户",null);
    }

    public ErrorAnonymousAccountException(String msg){
        super(1040,msg,null);
    }
}
