package com.wondersgroup.healthcloud.registration.entity.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/5/22.
 */
@XmlRootElement(name = "Response")
public class OrderResultResponse extends BaseResponse {

    @XmlElement(name = "Result")
    public OrderResult orderResult;
}
