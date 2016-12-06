package com.wondersgroup.healthcloud.registration.entity.response;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by longshasha on 16/5/22.
 */
public abstract class BaseResponse {

    @XmlElement(name = "MessageHeader")
    public ResponseMessageHeader messageHeader;

}
