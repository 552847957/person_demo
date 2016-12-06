package com.wondersgroup.healthcloud.registration.entity.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/5/23.
 * 查询订单列表的请求对象类
 */
@XmlRootElement(name = "OrderInfo")
public class OrderListR {

    /**
     * 查询类型
     * 1：双闭区间，2：开闭区间
     * 如果入参中传入下列时间区间，则该字段必填，默认取双闭区间
     * applyStartTime、applyEndTime；
     * 和visitStartTime、visitEndTime
     */
    private String queryType;
    /**
     * 申请开始时间 格式：YYYY-MM-DD HH24:MI:SS
     * 如果证件类型和证件号码为空，则以下条件至少选填一组
     * applyStartTime、applyEndTime；和visitStartTime、visitEndTime
     */
    private String applyStartTime;
    /**
     * 申请结束时间
     * 格式：YYYY-MM-DD HH24:MI:SS
     */
    private String applyEndTime;
    /**
     * 就诊开始时间
     * 格式：yyyy-MM-dd
     */
    private String visitStartTime;
    /**
     * 就诊结束时间
     * 格式：yyyy-MM-dd
     */
    private String visitEndTime;
    private String hosOrgCode;
    private String hosDeptCode;
    private String userCardType;
    private String userCardId;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 订单状态
     * 订单状态包括：1：已预约；2：已支付；3：已退号；4：已取号；5：待退费
     *
     * 以|分割订单状态，比如需要查询已预约和已支付的订单，则订单状态为1|2；
     */
    private String orderStatus;
    /**
     * 是否有效
     * 是否只查询有效订单（有效订单即未取消，也未过期的订单 true：只查询有效订单，
     * false：只查询无效订单，
     * 空：查询所有订单
     */
    private String isValid;
    /**
     * 绑定类型
     * 00：微信绑定， 01：支付宝绑定，  02：湖南有线绑定。
     */
    private String bindType;

    /**
     * 第三方平台或设备标识
     */
    private String identifyCode;


    public OrderListR() {
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getApplyStartTime() {
        return applyStartTime;
    }

    public void setApplyStartTime(String applyStartTime) {
        this.applyStartTime = applyStartTime;
    }

    public String getApplyEndTime() {
        return applyEndTime;
    }

    public void setApplyEndTime(String applyEndTime) {
        this.applyEndTime = applyEndTime;
    }

    public String getVisitStartTime() {
        return visitStartTime;
    }

    public void setVisitStartTime(String visitStartTime) {
        this.visitStartTime = visitStartTime;
    }

    public String getVisitEndTime() {
        return visitEndTime;
    }

    public void setVisitEndTime(String visitEndTime) {
        this.visitEndTime = visitEndTime;
    }

    public String getHosOrgCode() {
        return hosOrgCode;
    }

    public void setHosOrgCode(String hosOrgCode) {
        this.hosOrgCode = hosOrgCode;
    }

    public String getHosDeptCode() {
        return hosDeptCode;
    }

    public void setHosDeptCode(String hosDeptCode) {
        this.hosDeptCode = hosDeptCode;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getBindType() {
        return bindType;
    }

    public void setBindType(String bindType) {
        this.bindType = bindType;
    }

    public String getIdentifyCode() {
        return identifyCode;
    }

    public void setIdentifyCode(String identifyCode) {
        this.identifyCode = identifyCode;
    }
}
