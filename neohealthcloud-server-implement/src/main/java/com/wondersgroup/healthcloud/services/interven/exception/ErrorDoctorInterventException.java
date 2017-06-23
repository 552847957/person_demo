package com.wondersgroup.healthcloud.services.interven.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/5/17.
 */
public class ErrorDoctorInterventException extends BaseException {

    public ErrorDoctorInterventException(){
        super(4001,"该居民已被其他医生干预",null);
    }
}
