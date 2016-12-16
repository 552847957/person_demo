package com.wondersgroup.healthcloud.registration.entity.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/11/17.
 */
@XmlRootElement(name = "Response")
public class UserInfoResultResponse extends BaseResponse{

    @XmlElement(name = "Result")
    public UserInfoResult userInfoResult;
}
