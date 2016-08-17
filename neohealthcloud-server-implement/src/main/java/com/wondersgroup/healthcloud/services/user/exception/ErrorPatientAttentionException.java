package com.wondersgroup.healthcloud.services.user.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/8/17.
 */
public class ErrorPatientAttentionException extends BaseException {

    public  ErrorPatientAttentionException(String msg){
        super(1601,msg,null);
    }
}

