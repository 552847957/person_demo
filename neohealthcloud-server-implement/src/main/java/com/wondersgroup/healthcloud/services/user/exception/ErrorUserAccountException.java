package com.wondersgroup.healthcloud.services.user.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/8/5.
 */
public class ErrorUserAccountException extends BaseException {

    public ErrorUserAccountException(){
        super(1011,"不存在的用户",null);
    }

    public ErrorUserAccountException(String msg){
        super(1011,msg,null);
    }

}
