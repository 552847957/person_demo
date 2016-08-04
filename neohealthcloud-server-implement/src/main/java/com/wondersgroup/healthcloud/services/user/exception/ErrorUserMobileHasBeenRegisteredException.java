package com.wondersgroup.healthcloud.services.user.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/8/4.
 */
public class ErrorUserMobileHasBeenRegisteredException extends BaseException {
    public ErrorUserMobileHasBeenRegisteredException(String msg){
        super(1005,msg,null);
    }
}
