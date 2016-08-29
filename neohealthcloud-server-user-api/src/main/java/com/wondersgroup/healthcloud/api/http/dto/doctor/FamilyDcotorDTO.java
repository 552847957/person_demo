package com.wondersgroup.healthcloud.api.http.dto.doctor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by longshasha on 16/8/29.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FamilyDcotorDTO {

    private Boolean isSignDoctor;
    private Boolean hasOpenWonder;

    @JsonProperty("doctorDetail")
    private DoctorAccountDTO doctorDetail;


    public Boolean getIsSignDoctor() {
        return isSignDoctor;
    }

    public void setIsSignDoctor(Boolean isSignDoctor) {
        this.isSignDoctor = isSignDoctor;
    }

    public Boolean getHasOpenWonder() {
        return hasOpenWonder;
    }

    public void setHasOpenWonder(Boolean hasOpenWonder) {
        this.hasOpenWonder = hasOpenWonder;
    }

    public DoctorAccountDTO getDoctorDetail() {
        return doctorDetail;
    }

    public void setDoctorDetail(DoctorAccountDTO doctorDetail) {
        this.doctorDetail = doctorDetail;
    }
}
