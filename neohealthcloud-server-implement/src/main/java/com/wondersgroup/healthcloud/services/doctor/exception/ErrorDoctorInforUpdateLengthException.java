package com.wondersgroup.healthcloud.services.doctor.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/5/17.
 */
public class ErrorDoctorInforUpdateLengthException extends BaseException {

    public ErrorDoctorInforUpdateLengthException(String msg){
        super(1014,msg,null);
    }

}
