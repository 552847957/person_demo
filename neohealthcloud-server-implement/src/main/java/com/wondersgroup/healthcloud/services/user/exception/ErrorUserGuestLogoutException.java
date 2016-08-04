package com.wondersgroup.healthcloud.services.user.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/5/12.
 */
public class ErrorUserGuestLogoutException extends BaseException {

    public ErrorUserGuestLogoutException(String msg){
        super(1015,msg,null);
    }
}
