package com.wondersgroup.healthcloud.services.doctor.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/8/1.
 */
public class ErrorDoctorAccountException extends BaseException {
    public ErrorDoctorAccountException(){
        super(1003, "该账号不存在，请重新输入", null);
    }

    public ErrorDoctorAccountException(String msg){
        super(1004, msg, null);
    }
}
