package com.wondersgroup.healthcloud.registration.entity.request;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/11/15.
 */
@XmlRootElement(name = "Request")
public class CheckMediCardIdRequest extends BaseRequest{

    @XmlElement(name = "HospitalInfo")
    public CheckMediCardIdR checkMediCardIdR;

}
