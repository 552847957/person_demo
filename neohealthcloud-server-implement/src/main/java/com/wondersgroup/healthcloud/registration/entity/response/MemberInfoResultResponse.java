package com.wondersgroup.healthcloud.registration.entity.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/12/8.
 * 注册成员返回值
 */
@XmlRootElement(name = "Response")
public class MemberInfoResultResponse extends BaseResponse {

    @XmlElement(name = "Result")
    public MemberInfoResult memberInfoResult;
}
