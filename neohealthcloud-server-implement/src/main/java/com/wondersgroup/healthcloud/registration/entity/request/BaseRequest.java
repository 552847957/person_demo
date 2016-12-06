package com.wondersgroup.healthcloud.registration.entity.request;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by longshasha on 16/5/23.
 */
public abstract class BaseRequest {

    @XmlElement(name = "MessageHeader")
    public RequestMessageHeader requestMessageHeader;
}
