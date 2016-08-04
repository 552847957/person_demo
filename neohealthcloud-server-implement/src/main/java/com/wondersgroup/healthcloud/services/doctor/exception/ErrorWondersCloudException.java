package com.wondersgroup.healthcloud.services.doctor.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/5/11.
 */
public class ErrorWondersCloudException extends BaseException {

    public ErrorWondersCloudException(String msg){
        super(1002, msg, null);
    }

    public ErrorWondersCloudException(int code, String msg){
        super(code, msg, null);
    }
}
