package com.wondersgroup.healthcloud.api.http.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentHospital;
import org.apache.commons.lang3.StringUtils;


/**
 * Created by longshasha on 16/5/21.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManageHospitalDTO {

    private String id; //医院Id

    private String name;//医院名称

    private String picSmall;//医院列表图片

    private String picBig;//医院主页图片

    private String hospitalDesc;//医院简介

    private String delFlag;//服务商状态 0:有效，1:停用

    private String isonsale;//是否上架 “0”表示下架， “1”表示上架'

    private String addressCounty;//区县


    public ManageHospitalDTO() {

    }

    public ManageHospitalDTO(AppointmentHospital hospital) {
        if(hospital!=null){
            this.id = hospital.getId();
            this.name = hospital.getHosName();
            this.picSmall = hospital.getPicSmall();
            this.picBig = hospital.getPicBig();
            this.hospitalDesc = hospital.getHospitalDesc();
            this.delFlag = hospital.getDelFlag();
            this.isonsale = hospital.getIsonsale();
            this.addressCounty = hospital.getAddressCounty();
        }

    }


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

    public String getPicSmall() {
        return picSmall;
    }

    public void setPicSmall(String picSmall) {
        this.picSmall = picSmall;
    }

    public String getPicBig() {
        return picBig;
    }

    public void setPicBig(String picBig) {
        this.picBig = picBig;
    }

    public String getHospitalDesc() {
        return hospitalDesc;
    }

    public void setHospitalDesc(String hospitalDesc) {
        this.hospitalDesc = hospitalDesc;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getIsonsale() {
        return isonsale;
    }

    public void setIsonsale(String isonsale) {
        this.isonsale = isonsale;
    }

    public String getAddressCounty() {
        return addressCounty;
    }

    public void setAddressCounty(String addressCounty) {
        this.addressCounty = addressCounty;
    }

    public AppointmentHospital mergeHospital(AppointmentHospital hospital, ManageHospitalDTO manageHospitalDTO) {
        hospital.setPicBig(manageHospitalDTO.getPicBig());
        hospital.setPicSmall(manageHospitalDTO.getPicSmall());
        if(StringUtils.isNotBlank(manageHospitalDTO.getIsonsale()) ){
            String isonsale = manageHospitalDTO.getIsonsale();
            if("0".equals(isonsale) || "1".equals(isonsale)){
                hospital.setIsonsale(isonsale);
            }
        }
        //未设置两张图片则自动设置为停用状态
        if(StringUtils.isBlank(hospital.getPicSmall()) || StringUtils.isBlank(hospital.getPicBig())){
            hospital.setIsonsale("0");
        }
        return hospital;
    }
}
