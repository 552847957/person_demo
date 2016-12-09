package com.wondersgroup.healthcloud.services.appointment.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/3/7.
 */
public class AddContactErrorException extends BaseException {

    public AddContactErrorException() {
        super(3105, "添加就诊人失败", null);
    }
}
