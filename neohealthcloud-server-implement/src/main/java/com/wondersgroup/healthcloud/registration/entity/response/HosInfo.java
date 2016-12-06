package com.wondersgroup.healthcloud.registration.entity.response;


import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/5/22.
 */
@XmlRootElement(name = "HosInfo")
public class HosInfo {

    private String hosOrgCode;
    private String hosName;
    private String hospitalAdd;
    private String hospitalRule;
    private String hospitalWeb;
    private String trafficGuide;
    private String hospitalDesc;
    private String hospitalTel;
    private String hospitalGrade;
    private String payMode;//格式（|1|2|3|） 1：第三方支付  2：诊疗卡支付， 3：窗口支付。
    private String orderMode;//预约方式 |1|2| 1：有卡预约，2：无卡预约。

    /**
     * 是否支持预约当天
     * 1：支持
     * 0：不支持
     */
    private String isOrderToday;

    /**
     * 可预约天数
     */
    private String orderRange;

    /**
     * 是否支持分时段
     * 0：支持(时段选择,序号选择同时支持),
     * 1：不支持,
     * 2：支持(仅支持时段选择),
     * 3：支持(仅支持序号选择)
     */
    private String isSpTime;

    /**
     * 预约关闭日期，整数，若为1
     * 即前一天不能预约、退号明天的号源
     */
    private String closeDays;

    /**
     * 0-24的整数，代表时间，同上两个字段结合，起来为提前closeDays天closeTimeHour点之前不能预约和退号明天的号源
     */
    private String closeTimeHour;//预约关闭时间


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

    public String getHospitalAdd() {
        return hospitalAdd;
    }

    public void setHospitalAdd(String hospitalAdd) {
        this.hospitalAdd = hospitalAdd;
    }

    public String getHospitalRule() {
        return hospitalRule;
    }

    public void setHospitalRule(String hospitalRule) {
        this.hospitalRule = hospitalRule;
    }

    public String getHospitalWeb() {
        return hospitalWeb;
    }

    public void setHospitalWeb(String hospitalWeb) {
        this.hospitalWeb = hospitalWeb;
    }

    public String getTrafficGuide() {
        return trafficGuide;
    }

    public void setTrafficGuide(String trafficGuide) {
        this.trafficGuide = trafficGuide;
    }

    public String getHospitalDesc() {
        return hospitalDesc;
    }

    public void setHospitalDesc(String hospitalDesc) {
        this.hospitalDesc = hospitalDesc;
    }

    public String getHospitalTel() {
        return hospitalTel;
    }

    public void setHospitalTel(String hospitalTel) {
        this.hospitalTel = hospitalTel;
    }

    public String getHospitalGrade() {
        return hospitalGrade;
    }

    public void setHospitalGrade(String hospitalGrade) {
        this.hospitalGrade = hospitalGrade;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getOrderMode() {
        return orderMode;
    }

    public void setOrderMode(String orderMode) {
        this.orderMode = orderMode;
    }

    public String getIsSpTime() {
        return isSpTime;
    }

    public void setIsSpTime(String isSpTime) {
        this.isSpTime = isSpTime;
    }

    public String getIsOrderToday() {
        return isOrderToday;
    }

    public void setIsOrderToday(String isOrderToday) {
        this.isOrderToday = isOrderToday;
    }

    public String getOrderRange() {
        return orderRange;
    }

    public void setOrderRange(String orderRange) {
        this.orderRange = orderRange;
    }

    public String getCloseDays() {
        return closeDays;
    }

    public void setCloseDays(String closeDays) {
        this.closeDays = closeDays;
    }

    public String getCloseTimeHour() {
        return closeTimeHour;
    }

    public void setCloseTimeHour(String closeTimeHour) {
        this.closeTimeHour = closeTimeHour;
    }
}
