package com.wondersgroup.healthcloud.services.doctor.exception;


import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/5/12.
 */
public class ErrorUserWondersBaseInfoException extends BaseException {

    public ErrorUserWondersBaseInfoException(){
        super(1000,"获取用户信息失败",null);
    }
}
