package com.wondersgroup.healthcloud.registration.entity.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/11/17.
 * 注册或修改用户的请求类
 */
@XmlRootElement(name = "UserInfo")
public class QueryUserInfoR {

    /**
     * 平台用户代码
     *
     */
    private String userId;
    /**
     * 证件类型
     */
    private String userCardType;
    /**
     * 证件号码
     */
    private String userCardId;
    /**
     * 手机号码
     */
    private String userPhone;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserCardType() {
        return userCardType;
    }

    public void setUserCardType(String userCardType) {
        this.userCardType = userCardType;
    }

    public String getUserCardId() {
        return userCardId;
    }

    public void setUserCardId(String userCardId) {
        this.userCardId = userCardId;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
