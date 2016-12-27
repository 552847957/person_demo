package com.wondersgroup.healthcloud.services.appointment.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/8/1.
 */
public class ErrorAppointmentIsOffException extends BaseException {
    public ErrorAppointmentIsOffException(){
        super(1, "很抱歉,预约挂号功能由于特殊原因现已关闭", null);
    }

    public ErrorAppointmentIsOffException(String msg){
        super(3101, msg, null);
    }
}
