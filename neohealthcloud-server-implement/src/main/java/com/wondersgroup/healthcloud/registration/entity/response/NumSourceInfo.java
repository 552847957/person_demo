package com.wondersgroup.healthcloud.registration.entity.response;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/5/22.
 * 可预约号源信息查询返回对象类
 */

@XmlRootElement(name = "Result")
public class NumSourceInfo {
    /**
     * 排班ID
     */
    private String scheduleId;

    /**
     * 排班日期 格式：yyyy-MM-dd
     */
    private String scheduleDate;
    private String hosOrgCode;
    private String hosName;
    private String hosDeptCode;
    private String deptName;
    private String hosDoctCode;
    private String doctName;

    /**
     * 出诊级别编码
     * 1专家2专病3普通
     */
    private String visitLevelCode;

    /**
     * 出诊级别
     * 0:其他;1:住院医师;2:主治医生;3:副主任医师;4:主任医师;5:名老专家
     * 由于各医院的出诊级别没有标准化，第三方接口直接用中文表示出诊级别
     */
    private String visitLevel;

    /**
     * 出诊费用
     */
    private String visitCost;

    /**
     * 出诊时段
     * 1:上午2:上午3:晚上
     */
    private String timeRange;

    /**
     * 开始时间 yyyy-MM-dd HH:MI:SS
     */
    private String startTime;

    /**
     * 结束时间 2012-11-5 9:30:00
     */
    private String endTime;

    /**
     * 已预约数 已预约数
     */
    private String orderedNum;

    /**
     * 剩余号源数
     * 部分医院可能只有该值而没有总数
     */
    private String reserveOrderNum;
    /**
     * 可预约总数
     * 可预约总数即放号数，包括已预约号源数。(共享号源医院该值为空)
     */
    private String sumOrderNum;

    /**
     * 预约类型
     * 包括指定到科室，医生级别，医生（缺省到医生）
     * 1: 医生；2：医生级别；3：科室；
     */
    private String orderType;
    /**
     * 序号 以|分割的数字串
     */
    private String visitNo;
    /**
     * 排班状态
     * 1 正常 2停诊
     */
    private String status;

    /**
     * 就诊类型
     * 1|2|3  1专家.2专病3.普通 为空查询所有，为3时不能填医生ID，普通类型没有医生ID。
     */
    private String registerType;

    private String registerName;

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }


    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
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

    public String getOrderedNum() {
        return orderedNum;
    }

    public void setOrderedNum(String orderedNum) {
        this.orderedNum = orderedNum;
    }

    public String getReserveOrderNum() {
        return reserveOrderNum;
    }

    public void setReserveOrderNum(String reserveOrderNum) {
        this.reserveOrderNum = reserveOrderNum;
    }

    public String getSumOrderNum() {
        return sumOrderNum;
    }

    public void setSumOrderNum(String sumOrderNum) {
        this.sumOrderNum = sumOrderNum;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getVisitNo() {
        return visitNo;
    }

    public void setVisitNo(String visitNo) {
        this.visitNo = visitNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public String getRegisterName() {
        return registerName;
    }

    public void setRegisterName(String registerName) {
        this.registerName = registerName;
    }
}
