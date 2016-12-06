package com.wondersgroup.healthcloud.registration.entity.request;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/5/23.
 */
@XmlRootElement(name = "Request")
public class DoctInfoRequest extends BaseRequest {

    /**
     * 只传hosOrgCode和hosDeptCode
     * hosDeptCode 为空时是查询医院下所有的医生
     */
    @XmlElement(name = "DoctInfo")
    public DeptInfoR deptInfoR;

}
