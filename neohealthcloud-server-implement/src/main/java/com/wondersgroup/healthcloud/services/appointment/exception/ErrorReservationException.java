package com.wondersgroup.healthcloud.services.appointment.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/5/24.
 */
public class ErrorReservationException extends BaseException {

    public ErrorReservationException(String msg){
        super(3011, msg, null);
    }
}
