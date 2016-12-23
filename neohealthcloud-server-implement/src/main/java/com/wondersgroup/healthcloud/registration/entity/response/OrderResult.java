package com.wondersgroup.healthcloud.registration.entity.response;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/5/22.
 * 提交预约订单返回对象类
 */
@XmlRootElement(name = "Result")
public class OrderResult {
    private String scheduleId;
    private String numSourceId;
    /**
     * 系统预约单编码
     */
    private String orderId;
    /**
     * 取号密码
     */
    private String takePassword;
    /**
     * 就诊序号
     */
    private String visitNo;
    /**
     * 平台用户编码
     */
    private String platformUserId;
    /**
     * 平台登录账号
     * 平台用户注册的登录账号，当用户注册时返回。
     */
    private String platformUser;
    /**
     * 当进行用户注册时，返回一个6位随机密码。
     * 平台用户密码
     */
    private String platformPwd;

    /**
     * 预约号
     */
    private String hosNumSourceId;


    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getNumSourceId() {
        return numSourceId;
    }

    public void setNumSourceId(String numSourceId) {
        this.numSourceId = numSourceId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTakePassword() {
        return takePassword;
    }

    public void setTakePassword(String takePassword) {
        this.takePassword = takePassword;
    }

    public String getVisitNo() {
        return visitNo;
    }

    public void setVisitNo(String visitNo) {
        this.visitNo = visitNo;
    }

    public String getPlatformUserId() {
        return platformUserId;
    }

    public void setPlatformUserId(String platformUserId) {
        this.platformUserId = platformUserId;
    }

    public String getPlatformUser() {
        return platformUser;
    }

    public void setPlatformUser(String platformUser) {
        this.platformUser = platformUser;
    }

    public String getPlatformPwd() {
        return platformPwd;
    }

    public void setPlatformPwd(String platformPwd) {
        this.platformPwd = platformPwd;
    }

    public String getHosNumSourceId() {
        return hosNumSourceId;
    }

    public void setHosNumSourceId(String hosNumSourceId) {
        this.hosNumSourceId = hosNumSourceId;
    }
}
