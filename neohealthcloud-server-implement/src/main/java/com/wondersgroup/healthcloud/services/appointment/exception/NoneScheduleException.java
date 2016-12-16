package com.wondersgroup.healthcloud.services.appointment.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/5/24.
 */
public class NoneScheduleException extends BaseException {

    public NoneScheduleException(){
        super(3011, "该排班不存在", null);
    }
}
