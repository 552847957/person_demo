package com.wondersgroup.healthcloud.services.user.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/8/4.
 */
public class ErrorUserMobileHasNotRegisteredException extends BaseException {

    public ErrorUserMobileHasNotRegisteredException(String msg){
        super(1005,msg,null);
    }
}
