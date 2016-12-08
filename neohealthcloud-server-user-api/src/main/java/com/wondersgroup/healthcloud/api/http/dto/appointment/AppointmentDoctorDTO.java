package com.wondersgroup.healthcloud.api.http.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentDoctor;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentL2Department;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/5/21.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentDoctorDTO {

    /**
     * 医生Id
     */
    private String id;
    /**
     * 医生姓名
     */
    private String name;
    /**
     * 医生头像
     */
    private String avatar;

    /**
     * 医生职称
     */
    @JsonProperty("duty_name")
    private String dutyName;

    /**
     * 医生简介
     */
    private String specialty;

    private String reservationRule = ""; //预约规则


    @JsonProperty("hospital_name")
    private String hospitalName;//医院名称

    @JsonProperty("department_name")
    private String departmentName;//二级科室名称

    /**
     * 医生预约数
     */
    @JsonProperty("reservation_num")
    private int reservationNum;


    /**
     * 预约状态
     * 0: 无排班
     * 1: 可预约
     * 2: 约满
     */
    @JsonProperty("reservation_status")
    private int reservationStatus;

    /**
     * 1:医生 2:科室
     */
    private int type;

    public AppointmentDoctorDTO() {
    }

    public AppointmentDoctorDTO(AppointmentDoctor doctor,Map<String,Object> result) {
        this.id = doctor.getId();
        this.name = doctor.getDoctName();
        this.avatar = doctor.getAvatar();
        this.dutyName = doctor.getDoctTile();
        this.specialty = doctor.getDoctInfo();
        this.reservationNum = doctor.getReservationNum()==null?0:doctor.getReservationNum();
        this.reservationRule = doctor.getReservationRule();


        int scheduleNum = result.get("scheduleNum") == null ? 0:((Long)result.get("scheduleNum")).intValue();

        int reserveOrderNum = result.get("reserveOrderNum") == null ? 0 : ((BigDecimal)result.get("reserveOrderNum")).intValue();

        if(scheduleNum == 0){
            this.reservationStatus = 0;
        }else if(reserveOrderNum>0){
            this.reservationStatus = 1;
        }else{
            this.reservationStatus = 2;
        }
        this.type = 1;
    }

    public AppointmentDoctorDTO(AppointmentL2Department department,Map<String,Object> result) {
        this.id = department.getId();
        this.name = department.getDeptName();
        this.avatar = "";
        this.dutyName = "";

        this.specialty = department.getDeptDesc();
        this.reservationNum = department.getReservationNum()==null?0:department.getReservationNum();

        int scheduleNum = result.get("scheduleNum") == null ? 0:((Long)result.get("scheduleNum")).intValue();

        int reserveOrderNum = result.get("reserveOrderNum") == null ? 0 : ((BigDecimal)result.get("reserveOrderNum")).intValue();
        if(scheduleNum == 0){
            this.reservationStatus = 0;
        }else if(reserveOrderNum>0){
            this.reservationStatus = 1;
        }else{
            this.reservationStatus = 2;
        }

        if(this.reservationStatus!=0 && StringUtils.isBlank(this.specialty)){
            String visitLevelCode = getVisitLevelCodeView(result.get("visitLevelCode").toString());
            this.specialty = visitLevelCode;
        }
        this.type = 2;
    }

    public static String getVisitLevelCodeView(String reserveOrderNum) {

        if("1".equals(reserveOrderNum)){
            return "专家门诊";
        }else if("2".equals(reserveOrderNum)){
            return "专病门诊";
        }else if("3".equals(reserveOrderNum)){
            return "普通门诊";
        }

        return "";
    }

    public static AppointmentDoctorDTO getDoctorDTOList(AppointmentDoctor doctor){
        AppointmentDoctorDTO doctorDTO = new AppointmentDoctorDTO();
        doctorDTO.setId(doctor.getId());
        doctorDTO.setName(doctor.getDoctName());
        doctorDTO.setAvatar(doctor.getAvatar());
        doctorDTO.setDutyName(doctor.getDoctTile());

        doctorDTO.setHospitalName(doctor.getHospitalName());
        doctorDTO.setDepartmentName(doctor.getDepartmentName());
        return doctorDTO;
    }

    public AppointmentDoctorDTO(AppointmentL2Department department) {
        this.id = department.getId();
        this.name = department.getDeptName()+"门诊";
        this.avatar = DepartmentDTO.url;
        this.dutyName = "";
        this.specialty = "";
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getReservationRule() {
        return reservationRule;
    }

    public void setReservationRule(String reservationRule) {
        this.reservationRule = reservationRule;
    }


    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getReservationNum() {
        return reservationNum;
    }

    public void setReservationNum(int reservationNum) {
        this.reservationNum = reservationNum;
    }

    public int getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(int reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
