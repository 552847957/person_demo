package com.wondersgroup.healthcloud.api.http.dto.doctor;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by longshasha on 16/8/2.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SyncRequestDTO {

    @JsonProperty(value = "hospital_id")
    private String hospitalId;

    private String no;

    private String mobile;

    private String name;

    private String idcard;

    @JsonProperty(value = "duty_id",required = true)
    private String dutyId;

    private String roles;//多个用逗号隔开

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
