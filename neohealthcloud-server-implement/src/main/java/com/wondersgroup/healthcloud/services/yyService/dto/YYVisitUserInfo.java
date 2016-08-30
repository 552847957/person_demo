package com.wondersgroup.healthcloud.services.yyService.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 医养结合用户信息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YYVisitUserInfo {

    private String id;
    private String name;
    private String headIcon;//用户头像
    private String idNo;//身份证号
    private String address;//上海市普陀区真如镇南大街居委会路21
    private String phone;//电话号码
    private String age;//112
    private String gender;
//    private String csrq;//1900-03-24
//    private String qx;//普陀区
//    private String jd;//真如镇
//    private String jw;//南大街居委会

    private String keepername;//监护人姓名
    private String keeperphone;//监护人电话
    private String keeperrelation;//与监护人关系

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(String headIcon) {
        this.headIcon = headIcon;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getKeepername() {
        return keepername;
    }

    public void setKeepername(String keepername) {
        this.keepername = keepername;
    }

    public String getKeeperphone() {
        return keeperphone;
    }

    public void setKeeperphone(String keeperphone) {
        this.keeperphone = keeperphone;
    }

    public String getKeeperrelation() {
        return keeperrelation;
    }

    public void setKeeperrelation(String keeperrelation) {
        this.keeperrelation = keeperrelation;
    }
}
