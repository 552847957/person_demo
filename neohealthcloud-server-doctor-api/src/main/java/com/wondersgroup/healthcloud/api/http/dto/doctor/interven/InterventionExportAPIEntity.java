package com.wondersgroup.healthcloud.api.http.dto.doctor.interven;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 导出实体
 * Created by zhuchunliu on 2015/9/10.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterventionExportAPIEntity {

    private String name;// 患者姓名
    private String personcardAbbr;// 证件号码
    private String age;//年龄
    private String gender;//性别
    private String mobilephone;//联系方式
    private String excName;//异常类型
    private String remindDate;//提醒日期
    private String registerId;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersoncardAbbr() {
        return personcardAbbr;
    }

    public void setPersoncardAbbr(String personcardAbbr) {
        this.personcardAbbr = personcardAbbr;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    public String getExcName() {
        return excName;
    }

    public void setExcName(String excName) {
        this.excName = excName;
    }

    public String getRemindDate() {
        return remindDate;
    }

    public void setRemindDate(String remindDate) {
        this.remindDate = remindDate;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }
}
