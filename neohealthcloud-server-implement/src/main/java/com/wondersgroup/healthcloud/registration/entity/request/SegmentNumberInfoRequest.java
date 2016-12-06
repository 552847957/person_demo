package com.wondersgroup.healthcloud.registration.entity.request;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/11/16.
 */
@XmlRootElement(name = "Request")
public class SegmentNumberInfoRequest extends BaseRequest{

    @XmlElement(name = "NumberInfo")
    public SegmentNumberInfoR segmentNumberInfoR;
}
