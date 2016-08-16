package com.wondersgroup.healthcloud.api.http.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by zhuchunliu on 2016/1/3.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssessmentHistoryAPIEntity {
    private String id;
    private String risk;
    private String assesstime;
    private Integer age;
    private Boolean isHypertension; //是否高血压
    private Boolean isOverWeight; //是否超重 (BMI)
    private Boolean isFat; //是否中心性肥胖
    private Boolean hasFamilyHistory; //是否含有家族史;
    private Boolean needAmendLife; //是否需要改进生活方式；
    private Boolean needMovement; //是否需要运动

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

    public Boolean getNeedMovement() {
        return needMovement;
    }

    public void setNeedMovement(Boolean needMovement) {
        this.needMovement = needMovement;
    }

    public Boolean getNeedAmendLife() {
        return needAmendLife;
    }

    public void setNeedAmendLife(Boolean needAmendLife) {
        this.needAmendLife = needAmendLife;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getAssesstime() {
        return assesstime;
    }

    public void setAssesstime(String assesstime) {
        this.assesstime = assesstime;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
