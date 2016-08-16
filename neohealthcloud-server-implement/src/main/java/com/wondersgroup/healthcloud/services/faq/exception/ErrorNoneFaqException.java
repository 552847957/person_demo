package com.wondersgroup.healthcloud.services.faq.exception;

import com.wondersgroup.healthcloud.exceptions.BaseException;

/**
 * Created by longshasha on 16/8/16.
 */
public class ErrorNoneFaqException extends BaseException {

    public ErrorNoneFaqException(){
        super(1050,"没有对应的问答详情",null);
    }
}
