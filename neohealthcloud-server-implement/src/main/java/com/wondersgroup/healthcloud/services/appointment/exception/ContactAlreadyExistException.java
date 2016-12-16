package com.wondersgroup.healthcloud.services.appointment.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/3/7.
 */
public class ContactAlreadyExistException extends BaseException {

    public ContactAlreadyExistException() {
        super(3103, "该联系人已存在，不能重复提交！", null);
    }
}
