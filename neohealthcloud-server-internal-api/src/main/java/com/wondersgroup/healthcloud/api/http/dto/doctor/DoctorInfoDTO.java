package com.wondersgroup.healthcloud.api.http.dto.doctor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by shenbin on 16/8/5.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorInfoDTO {

    private String id;

    @JsonProperty(value = "hospital_id")
    private String hospitalId;
    private String no;

    @JsonProperty(value = "depart_standard")
    private String departStandard;//标准科室编码 对应字典表
    private String idcard;

    @JsonProperty(value = "duty_id")
    private String dutyId;//医生职称编码 对应字典表
    private String expertin;
    private String introduction;

    private int actcode;//医生推广邀请码

    @JsonProperty(value = "del_flag")
    private String delFlag;

    @JsonProperty(value = "source_id")
    private String sourceId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getDepartStandard() {
        return departStandard;
    }

    public void setDepartStandard(String departStandard) {
        this.departStandard = departStandard;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getDutyId() {
        return dutyId;
    }

    public void setDutyId(String dutyId) {
        this.dutyId = dutyId;
    }

    public String getExpertin() {
        return expertin;
    }

    public void setExpertin(String expertin) {
        this.expertin = expertin;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getActcode() {
        return actcode;
    }

    public void setActcode(int actcode) {
        this.actcode = actcode;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
}
