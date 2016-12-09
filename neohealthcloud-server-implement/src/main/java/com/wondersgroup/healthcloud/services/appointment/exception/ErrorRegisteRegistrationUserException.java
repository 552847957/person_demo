package com.wondersgroup.healthcloud.services.appointment.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/12/8.
 */
public class ErrorRegisteRegistrationUserException extends BaseException {

    public ErrorRegisteRegistrationUserException(String msg){
        super(3006, msg, null);
    }
}
