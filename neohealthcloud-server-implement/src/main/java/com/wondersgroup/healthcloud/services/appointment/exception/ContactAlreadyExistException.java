package com.wondersgroup.healthcloud.services.appointment.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/3/7.
 */
public class ContactAlreadyExistException extends BaseException {

    public ContactAlreadyExistException() {
        super(3103, "添加失败，就诊人已被您或别人添加，建议：就诊人可以直接注册并进行预约", null);
    }
}
