package com.wondersgroup.healthcloud.api.http.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by zhuchunliu on 2015/12/31.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssessmentAPIEntity {

    //private Integer riskLevel; //风险等级  1 健康  2 高危 3 患者
    private String risk; //1 糖尿病 2 高血压  3 脑卒中
    private Integer age;
    private String pressure;
    private Integer height;
    private Float weight;
    private Float waist;
    private String familyHistory;
    private Boolean isHypertension; //是否高血压
    private Boolean isOverWeight; //是否超重 (BMI)
    private Boolean isFat; //是否中心性肥胖
    private Boolean hasFamilyHistory; //是否含有家族史;
    private Boolean needAmendLife; //是否需要改进生活方式；
    private Boolean needMovement; //是否需要运动
    private Integer diseaseid;
    private String lastAssessTime; //上次评估时间
    private Integer sport;


    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }


    public String getFamilyHistory() {
        return familyHistory;
    }

    public void setFamilyHistory(String familyHistory) {
        this.familyHistory = familyHistory;
    }

    public Boolean getIsHypertension() {
        return isHypertension;
    }

    public void setIsHypertension(Boolean isHypertension) {
        this.isHypertension = isHypertension;
    }

    public Boolean getIsOverWeight() {
        return isOverWeight;
    }

    public void setIsOverWeight(Boolean isOverWeight) {
        this.isOverWeight = isOverWeight;
    }

    public Boolean getIsFat() {
        return isFat;
    }

    public void setIsFat(Boolean isFat) {
        this.isFat = isFat;
    }

    public Boolean getHasFamilyHistory() {
        return hasFamilyHistory;
    }

    public void setHasFamilyHistory(Boolean hasFamilyHistory) {
        this.hasFamilyHistory = hasFamilyHistory;
    }

    public Boolean getNeedAmendLife() {
        return needAmendLife;
    }

    public void setNeedAmendLife(Boolean needAmendLife) {
        this.needAmendLife = needAmendLife;
    }

    public Boolean getNeedMovement() {
        return needMovement;
    }

    public void setNeedMovement(Boolean needMovement) {
        this.needMovement = needMovement;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Float getWaist() {
        return waist;
    }

    public void setWaist(Float waist) {
        this.waist = waist;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public Integer getDiseaseid() {
        return diseaseid;
    }

    public void setDiseaseid(Integer diseaseid) {
        this.diseaseid = diseaseid;
    }

    public String getLastAssessTime() {
        return lastAssessTime;
    }

    public void setLastAssessTime(String lastAssessTime) {
        this.lastAssessTime = lastAssessTime;
    }

    public Integer getSport() {
        return sport;
    }

    public void setSport(Integer sport) {
        this.sport = sport;
    }
}
