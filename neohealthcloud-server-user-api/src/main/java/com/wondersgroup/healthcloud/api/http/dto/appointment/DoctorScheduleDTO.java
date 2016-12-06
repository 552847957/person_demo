package com.wondersgroup.healthcloud.api.http.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentDoctor;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentDoctorSchedule;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentL2Department;

import java.util.List;

/**
 * Created by longshasha on 16/5/21.
 */@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorScheduleDTO {

    @JsonProperty("hospital_id")
    private String hospitalId;

    @JsonProperty("hospital_name")
    private String hospitalName;

    @JsonProperty("department_id")
    private String departmentId;

    @JsonProperty("department_name")
    private String departmentName;

    @JsonProperty("hospital_rule")
    private String hospitalRule;

    private List<DoctorScheduleDetailDTO> contents;

    public DoctorScheduleDTO(AppointmentDoctor doctor, List<AppointmentDoctorSchedule> doctorScheduleList) {
        if(doctor!=null){
            this.departmentId = doctor.getL2DepartmentId()==null?"":doctor.getL2DepartmentId();
            this.departmentName = doctor.getDepartmentName()==null?"":doctor.getDepartmentName();
            this.hospitalId = doctor.getHospitalId()==null?"":doctor.getHospitalId();
            this.hospitalName = doctor.getHospitalName()==null?"":doctor.getHospitalName();
            this.hospitalRule = "";
        }

        List<DoctorScheduleDetailDTO> scheduleDetailDTOList = Lists.newArrayList();
        if(doctorScheduleList!=null && doctorScheduleList.size()>0){
            for(AppointmentDoctorSchedule schedule: doctorScheduleList){
                DoctorScheduleDetailDTO scheduleDetailDTO = new DoctorScheduleDetailDTO(schedule);
                scheduleDetailDTOList.add(scheduleDetailDTO);
            }
        }
        this.contents = scheduleDetailDTOList;


    }

    public DoctorScheduleDTO(AppointmentL2Department department, List<AppointmentDoctorSchedule> deptScheduleList) {
        if(department!=null){
            this.departmentId = department.getId();
            this.departmentName = department.getDeptName();
            this.hospitalId = department.getHospitalId();
            this.hospitalName = department.getHospitalName();
            this.hospitalRule = "";
        }
        List<DoctorScheduleDetailDTO> scheduleDetailDTOList = Lists.newArrayList();
        if(deptScheduleList!=null && deptScheduleList.size()>0){
            for(AppointmentDoctorSchedule schedule: deptScheduleList){
                DoctorScheduleDetailDTO scheduleDetailDTO = new DoctorScheduleDetailDTO(schedule);
                scheduleDetailDTOList.add(scheduleDetailDTO);
            }
        }
        this.contents = scheduleDetailDTOList;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getHospitalRule() {
        return hospitalRule;
    }

    public void setHospitalRule(String hospitalRule) {
        this.hospitalRule = hospitalRule;
    }

    public List<DoctorScheduleDetailDTO> getContents() {
        return contents;
    }

    public void setContents(List<DoctorScheduleDetailDTO> contents) {
        this.contents = contents;
    }
}
