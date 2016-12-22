package com.wondersgroup.healthcloud.services.appointment.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/3/7.
 */
public class ContactAlreadyExistException extends BaseException {

    public ContactAlreadyExistException() {
        super(3103, "该证件已存在于您的就诊人列表，请勿重复添加", null);
    }
}
