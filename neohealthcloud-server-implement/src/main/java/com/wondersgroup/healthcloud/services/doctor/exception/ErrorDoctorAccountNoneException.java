package com.wondersgroup.healthcloud.services.doctor.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/5/17.
 */
public class ErrorDoctorAccountNoneException extends BaseException {

    public ErrorDoctorAccountNoneException(){
        super(1011,"不存在的医生",null);
    }
}
