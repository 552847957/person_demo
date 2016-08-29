package com.wondersgroup.healthcloud.api.http.dto.doctor.signedPerson;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by longshasha on 16/8/28.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignedPersonDTO {

    private String name;
    private String gender;
    private String age;
    private String cardType;
    private String personcard;
    private String addressText;
    private String addressOther;

    private Boolean isRisk;//是否是"危"
    private Boolean isJky;//是否是 "云"
    private Boolean isHyp;//是否是高血压 "高"
    private Boolean isDiabetes;//是否是糖尿病 "糖"
    private Boolean isApo;//是否是脑卒中 "脑"

    private String avatar;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getPersoncard() {
        return personcard;
    }

    public void setPersoncard(String personcard) {
        this.personcard = personcard;
    }

    public String getAddressText() {
        return addressText;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
    }

    public String getAddressOther() {
        return addressOther;
    }

    public void setAddressOther(String addressOther) {
        this.addressOther = addressOther;
    }

    public Boolean getIsRisk() {
        return isRisk;
    }

    public void setIsRisk(Boolean isRisk) {
        this.isRisk = isRisk;
    }

    public Boolean getIsJky() {
        return isJky;
    }

    public void setIsJky(Boolean isJky) {
        this.isJky = isJky;
    }

    public Boolean getIsHyp() {
        return isHyp;
    }

    public void setIsHyp(Boolean isHyp) {
        this.isHyp = isHyp;
    }

    public Boolean getIsDiabetes() {
        return isDiabetes;
    }

    public void setIsDiabetes(Boolean isDiabetes) {
        this.isDiabetes = isDiabetes;
    }

    public Boolean getIsApo() {
        return isApo;
    }

    public void setIsApo(Boolean isApo) {
        this.isApo = isApo;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
