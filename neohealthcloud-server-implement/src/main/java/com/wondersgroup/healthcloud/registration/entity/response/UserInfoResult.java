package com.wondersgroup.healthcloud.registration.entity.response;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/11/17.
 * 注册更新用户返回的对象类
 */
@XmlRootElement(name = "Result")
public class UserInfoResult {

    /**
     * 平台用户代码
     */
    private String platformUserId;
    /**
     * 若注册未提供密码，则默认用手机号做为密码。
     */
    private String userPwd;

    public String getPlatformUserId() {
        return platformUserId;
    }

    public void setPlatformUserId(String platformUserId) {
        this.platformUserId = platformUserId;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }
}
