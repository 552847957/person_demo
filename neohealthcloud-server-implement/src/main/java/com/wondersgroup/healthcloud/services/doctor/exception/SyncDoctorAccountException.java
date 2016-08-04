package com.wondersgroup.healthcloud.services.doctor.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/8/2.
 */
public class SyncDoctorAccountException extends BaseException {
    public SyncDoctorAccountException(){
        super(2001, "请求同步数据不能为空", null);
    }

    public SyncDoctorAccountException(String msg){
        super(2002, msg, null);
    }
}
