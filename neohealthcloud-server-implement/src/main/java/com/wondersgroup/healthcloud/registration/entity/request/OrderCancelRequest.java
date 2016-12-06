package com.wondersgroup.healthcloud.registration.entity.request;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/5/23.
 */
@XmlRootElement(name = "Request")
public class OrderCancelRequest extends BaseRequest {

    @XmlElement(name = "OrderInfo")
    public OrderCancelR orderCancelR;

}
