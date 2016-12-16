package com.wondersgroup.healthcloud.registration.entity.request;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/11/17.
 */
@XmlRootElement(name = "Request")
public class MemberInfoRequest extends BaseRequest{

    @XmlElement(name = "MemberInfo")
    public MemberInfoR memberInfoR;
}
