package com.wondersgroup.healthcloud.registration.entity.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/5/23.
 */
@XmlRootElement(name = "OrderInfo")
public class OrderCancelR {

    private String hosOrgCode;
    private String orderId;
    private String numSourceId;
    /**
     * 平台用户编码
     */
    private String platformUserId;
    /**
     * 取号密码
     * 如果是窗口支付或订单已支付，取号密码必填
     */
    private String takePassword;

    /**
     * cancelObj
     * 1：患者；2：服务商
     */
    private String cancelObj;
    /**
     * 退号原因
     * 0：其他 1：患者主动退号
     */
    private String cancelReason;
    /**
     * 备注
     * 只有退号原因为其他时才有用
     */
    private String cancelDesc;


    public OrderCancelR() {
    }

    public String getHosOrgCode() {
        return hosOrgCode;
    }

    public void setHosOrgCode(String hosOrgCode) {
        this.hosOrgCode = hosOrgCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getNumSourceId() {
        return numSourceId;
    }

    public void setNumSourceId(String numSourceId) {
        this.numSourceId = numSourceId;
    }

    public String getPlatformUserId() {
        return platformUserId;
    }

    public void setPlatformUserId(String platformUserId) {
        this.platformUserId = platformUserId;
    }

    public String getTakePassword() {
        return takePassword;
    }

    public void setTakePassword(String takePassword) {
        this.takePassword = takePassword;
    }

    public String getCancelObj() {
        return cancelObj;
    }

    public void setCancelObj(String cancelObj) {
        this.cancelObj = cancelObj;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getCancelDesc() {
        return cancelDesc;
    }

    public void setCancelDesc(String cancelDesc) {
        this.cancelDesc = cancelDesc;
    }
}
