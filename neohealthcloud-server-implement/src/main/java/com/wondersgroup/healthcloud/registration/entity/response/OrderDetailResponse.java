package com.wondersgroup.healthcloud.registration.entity.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/5/23.
 */
@XmlRootElement(name = "Response")
public class OrderDetailResponse extends BaseResponse{

    @XmlElement(name = "Result")
    public OrderDetail orderDetail;
}
