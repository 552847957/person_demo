package com.wondersgroup.healthcloud.services.appointment.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/5/24.
 */
public class NoneSchedulePayModeException extends BaseException {

    public NoneSchedulePayModeException(){
        super(3012, "第三方接口的支付方式错误", null);
    }
}
