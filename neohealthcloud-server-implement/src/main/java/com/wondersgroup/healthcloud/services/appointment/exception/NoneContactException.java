package com.wondersgroup.healthcloud.services.appointment.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/5/24.
 */
public class NoneContactException extends BaseException {

    public NoneContactException(){
        super(3010, "该就诊人人不存在", null);
    }
}
