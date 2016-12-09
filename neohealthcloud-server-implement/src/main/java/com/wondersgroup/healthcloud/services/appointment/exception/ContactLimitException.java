package com.wondersgroup.healthcloud.services.appointment.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/3/7.
 */
public class ContactLimitException extends BaseException {

    public ContactLimitException() {
        super(3104, "就诊人最多只能添加3个！", null);
    }
}
