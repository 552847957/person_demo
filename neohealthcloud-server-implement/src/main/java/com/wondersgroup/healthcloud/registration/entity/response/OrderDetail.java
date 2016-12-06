package com.wondersgroup.healthcloud.registration.entity.response;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/5/23.
 */
@XmlRootElement(name = "OrderInfo")
public class OrderDetail {
    /**
     * 系统预约单编码
     */
    private String orderId;
    /**
     * 号源ID
     */
    private String numSourceId;
    /**
     * 预约单创建时间
     * YYYY-MM-DD HH24:MI:SS
     */
    private String createTime;
    /**
     * 预约时间
     * 医生坐诊时间； 格式：yyyy-MM-dd
     */
    private String orderTime;

    /**
     * 预约状态 1：已预约； 2：已支付；3：已退号； 4：已取号；5：待退费。
     */
    private String orderStatus;
    /**
     * 支付方式
     * 1：第三方支付，2：诊疗卡支付，3：窗口支付。
     */
    private String payMode;
    /**
     * 出诊费用
     */
    private String visitCost;
    /**
     * 取号密码
     */
    private String takePassword;
    /**
     * 就诊序号
     */
    private String visitNo;
    /**
     * 医院代码
     */
    private String hosOrgCode;
    /**
     * 医院名称
     */
    private String hosOrgName;
    /**
     * 预约科室
     */
    private String deptName;
    /**
     * 预约医生
     */
    private String doctName;
    /**
     * 医生级别
     */
    private String visitLevel;
    private String timeRange;
    private String startTime;
    private String endTime;
    /**
     * 平台用户编码
     * 预约人（操作人或预约系统登录用户）的用户编码
     */
    private String platformUserId;
    /**
     * 平台患者用户编码
     * 被预约人（就诊患者）的用户编码
     */
    private String platformPatientId;
    /**
     * 患者姓名
     */
    private String patientName;
    /**
     * 证件类型
     * 01：居民身份证
     * 02：居民户口簿
     * 03：护照
     * 04：军官证（士兵证）
     * 05：驾驶执照
     * 06：港澳居民来往内地通行证
     * 07：台湾居民来往内地通行证
     * 99：其他
     */
    private String patientCardType;
    /**
     * 证件号码
     */
    private String patientCardId;
    /**
     * 手机号码
     */
    private String patientPhone;
    /**
     * 用户性别
     * 0：未知的性别
     * 1：男性
     * 2：女性
     * 5：女性改（变）为男性
     * 6：男性改（变）为女性
     * 9：未说明的性别
     */
    private String patientSex;
    /**
     * 用户出生日期
     */
    private String patientBD;
    /**
     * 排班状态
     * 1正常 2 停诊
     */
    private String status;

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getVisitCost() {
        return visitCost;
    }

    public void setVisitCost(String visitCost) {
        this.visitCost = visitCost;
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

    public String getHosOrgCode() {
        return hosOrgCode;
    }

    public void setHosOrgCode(String hosOrgCode) {
        this.hosOrgCode = hosOrgCode;
    }

    public String getHosOrgName() {
        return hosOrgName;
    }

    public void setHosOrgName(String hosOrgName) {
        this.hosOrgName = hosOrgName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDoctName() {
        return doctName;
    }

    public void setDoctName(String doctName) {
        this.doctName = doctName;
    }

    public String getVisitLevel() {
        return visitLevel;
    }

    public void setVisitLevel(String visitLevel) {
        this.visitLevel = visitLevel;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPlatformUserId() {
        return platformUserId;
    }

    public void setPlatformUserId(String platformUserId) {
        this.platformUserId = platformUserId;
    }

    public String getPlatformPatientId() {
        return platformPatientId;
    }

    public void setPlatformPatientId(String platformPatientId) {
        this.platformPatientId = platformPatientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientCardType() {
        return patientCardType;
    }

    public void setPatientCardType(String patientCardType) {
        this.patientCardType = patientCardType;
    }

    public String getPatientCardId() {
        return patientCardId;
    }

    public void setPatientCardId(String patientCardId) {
        this.patientCardId = patientCardId;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(String patientSex) {
        this.patientSex = patientSex;
    }

    public String getPatientBD() {
        return patientBD;
    }

    public void setPatientBD(String patientBD) {
        this.patientBD = patientBD;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
