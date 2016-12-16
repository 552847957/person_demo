package com.wondersgroup.healthcloud.services.appointment.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/8/1.
 */
public class ErrorAppointmentManageException extends BaseException {

    public ErrorAppointmentManageException(String msg){
        super(3012, msg, null);
    }
}
