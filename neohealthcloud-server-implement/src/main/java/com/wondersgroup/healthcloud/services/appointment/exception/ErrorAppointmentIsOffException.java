package com.wondersgroup.healthcloud.services.appointment.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/8/1.
 */
public class ErrorAppointmentIsOffException extends BaseException {
    public ErrorAppointmentIsOffException(){
        super(1, "该服务暂未开通,敬请期待", null);
    }

    public ErrorAppointmentIsOffException(String msg){
        super(3101, msg, null);
    }
}
