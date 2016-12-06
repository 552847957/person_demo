package com.wondersgroup.healthcloud.registration.entity.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/5/23.
 */
@XmlRootElement(name = "OrderInfo")
public class SubmitOrder {

    /**
     * notNUll
     */
    private String scheduleId;
    /**
     * notNULL
     */
    private String numSourceId;

    /**
     * notNULL 1:第三方支付 2：诊疗卡/健康卡支付，3：窗口支付，4：银行卡支付。 目前只有2和3 都是线下支付
     */
    private String payMode;
    /**
     * notNULL 1：已付费；2：未付费
     */
    private String payState;
    /**
     * 有卡预约必填
     */
    private String mediCardId;
    /**
     * 0:无卡，初诊病人 1：社保卡（医保卡） 2：上海医联卡
     */
    private String mediCardType;
    /**
     * notNULL
     */
    private String hosOrgCode;
    /**
     * notNULL 医院姓名
     */
    private String hosName;
    /**
     * notNULL 科室代码
     */
    private String hosDeptCode;
    /**
     * notNULL
     */
    private String deptName;
    /**
     * 医生代码 可为空
     */
    private String hosDoctCode;
    private String doctName;
    private String visitLevelCode;
    private String visitLevel;
    private String visitCost;
    private String timeRange;
    private String visitNo;
    private String takePassword;

    /**
     * 预约时间
     * 医生坐诊时间；格式：YYYY-MM-DD HH24:MI:SS
     */
    private String orderTime;

    /**
     * 平台用户代码
     */
    private String platformUserId;
    /**
     *
     */
    private String userCardType;
    private String userCardId;
    private String userName;
    private String userPhone;
    private String userSex;
    private String userBD;//可为空 用户出生日期
    private String userContAdd;//可为空 用户联系地址

    /**
     * 成员ID
     * 填写时为成员预约，未填写时为用户预约
     */
    private String memberId;

    public SubmitOrder() {
    }

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

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getPayState() {
        return payState;
    }

    public void setPayState(String payState) {
        this.payState = payState;
    }

    public String getMediCardId() {
        return mediCardId;
    }

    public void setMediCardId(String mediCardId) {
        this.mediCardId = mediCardId;
    }

    public String getMediCardType() {
        return mediCardType;
    }

    public void setMediCardType(String mediCardType) {
        this.mediCardType = mediCardType;
    }

    public String getHosOrgCode() {
        return hosOrgCode;
    }

    public void setHosOrgCode(String hosOrgCode) {
        this.hosOrgCode = hosOrgCode;
    }

    public String getHosName() {
        return hosName;
    }

    public void setHosName(String hosName) {
        this.hosName = hosName;
    }

    public String getHosDeptCode() {
        return hosDeptCode;
    }

    public void setHosDeptCode(String hosDeptCode) {
        this.hosDeptCode = hosDeptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getHosDoctCode() {
        return hosDoctCode;
    }

    public void setHosDoctCode(String hosDoctCode) {
        this.hosDoctCode = hosDoctCode;
    }

    public String getDoctName() {
        return doctName;
    }

    public void setDoctName(String doctName) {
        this.doctName = doctName;
    }

    public String getVisitLevelCode() {
        return visitLevelCode;
    }

    public void setVisitLevelCode(String visitLevelCode) {
        this.visitLevelCode = visitLevelCode;
    }

    public String getVisitLevel() {
        return visitLevel;
    }

    public void setVisitLevel(String visitLevel) {
        this.visitLevel = visitLevel;
    }

    public String getVisitCost() {
        return visitCost;
    }

    public void setVisitCost(String visitCost) {
        this.visitCost = visitCost;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public String getVisitNo() {
        return visitNo;
    }

    public void setVisitNo(String visitNo) {
        this.visitNo = visitNo;
    }

    public String getTakePassword() {
        return takePassword;
    }

    public void setTakePassword(String takePassword) {
        this.takePassword = takePassword;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getPlatformUserId() {
        return platformUserId;
    }

    public void setPlatformUserId(String platformUserId) {
        this.platformUserId = platformUserId;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserBD() {
        return userBD;
    }

    public void setUserBD(String userBD) {
        this.userBD = userBD;
    }

    public String getUserContAdd() {
        return userContAdd;
    }

    public void setUserContAdd(String userContAdd) {
        this.userContAdd = userContAdd;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
