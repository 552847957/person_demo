package com.wondersgroup.healthcloud.services.user.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/8/5.
 */
public class ErrorChangeMobileException extends BaseException{
    public ErrorChangeMobileException(String msg){
        super(1012,msg,null);
    }

    public ErrorChangeMobileException(int code,String msg){
        super(code,msg,null);
    }
}
