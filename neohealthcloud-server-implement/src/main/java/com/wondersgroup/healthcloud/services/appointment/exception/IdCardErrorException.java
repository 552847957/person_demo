package com.wondersgroup.healthcloud.services.appointment.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/3/7.
 */
public class IdCardErrorException extends BaseException {

    public IdCardErrorException() {
        super(3101, "身份证不合法", null);
    }
}
