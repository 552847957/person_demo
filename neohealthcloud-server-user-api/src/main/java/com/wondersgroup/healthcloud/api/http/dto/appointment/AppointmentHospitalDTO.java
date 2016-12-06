package com.wondersgroup.healthcloud.api.http.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentHospital;



/**
 * Created by longshasha on 16/5/21.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentHospitalDTO {
    private String id; //医院Id
    private String name;//医院名称
    private String level;//医院等级
    private String address;//医院地址

    @JsonProperty("hospital_tel")
    private String hospitalTel;//医院电话

    @JsonProperty("hospital_desc")
    private String hospitalDesc;//医院简介

    @JsonProperty("hospital_rule")
    private String hospitalRule;//医院预约须知

    @JsonProperty("traffic_guide")
    private String trafficGuide;//医院交通指南

    @JsonProperty("pic_small")
    private String picSmall;//医院列表图片

    @JsonProperty("pic_big")
    private String picBig;//医院主页图片

    @JsonProperty("is_order_today")
    private String isOrderToday;//是否支持预约当天 1：支持，0：不支持

    /**
     * 医院的医生数量
     */
    @JsonProperty("doctor_num")
    private int doctorNum;

    public AppointmentHospitalDTO() {

    }

    public AppointmentHospitalDTO(AppointmentHospital hospital) {
        if(hospital!=null){
            this.id = hospital.getId();
            this.name = hospital.getHosName();
            this.level = hospital.getHospitalGrade();
            this.address = hospital.getHospitalAdd();
            this.picSmall = hospital.getPicSmall();
            this.isOrderToday = hospital.getIsOrderToday();
            this.doctorNum = hospital.getDoctorNum();
        }

    }

    public static AppointmentHospitalDTO getHospitalDetail(AppointmentHospital hospital) {
        AppointmentHospitalDTO hospitalDTO = new AppointmentHospitalDTO();
        if(hospital!=null){
            hospitalDTO.setId(hospital.getId());
            hospitalDTO.setName(hospital.getHosName());
            hospitalDTO.setLevel(hospital.getHospitalGrade());
            hospitalDTO.setAddress(hospital.getHospitalAdd());
            hospitalDTO.setPicSmall(hospital.getPicSmall());
            hospitalDTO.setPicBig(hospital.getPicBig());
            hospitalDTO.setHospitalDesc(hospital.getHospitalDesc());
            hospitalDTO.setHospitalRule(hospital.getHospitalRule());
            hospitalDTO.setHospitalTel(hospital.getHospitalTel());
            hospitalDTO.setTrafficGuide(hospital.getTrafficGuide());
            hospitalDTO.setDoctorNum(hospital.getDoctorNum()==null?0:hospital.getDoctorNum());
            hospitalDTO.setIsOrderToday(hospital.getIsOrderToday());
        }
        return hospitalDTO;

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


    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPicSmall() {
        return picSmall;
    }

    public void setPicSmall(String picSmall) {
        this.picSmall = picSmall;
    }

    public int getDoctorNum() {
        return doctorNum;
    }

    public void setDoctorNum(int doctorNum) {
        this.doctorNum = doctorNum;
    }

    public String getHospitalTel() {
        return hospitalTel;
    }

    public void setHospitalTel(String hospitalTel) {
        this.hospitalTel = hospitalTel;
    }

    public String getHospitalDesc() {
        return hospitalDesc;
    }

    public void setHospitalDesc(String hospitalDesc) {
        this.hospitalDesc = hospitalDesc;
    }

    public String getHospitalRule() {
        return hospitalRule;
    }

    public void setHospitalRule(String hospitalRule) {
        this.hospitalRule = hospitalRule;
    }

    public String getTrafficGuide() {
        return trafficGuide;
    }

    public void setTrafficGuide(String trafficGuide) {
        this.trafficGuide = trafficGuide;
    }

    public String getPicBig() {
        return picBig;
    }

    public void setPicBig(String picBig) {
        this.picBig = picBig;
    }

    public String getIsOrderToday() {
        return isOrderToday;
    }

    public void setIsOrderToday(String isOrderToday) {
        this.isOrderToday = isOrderToday;
    }
}
