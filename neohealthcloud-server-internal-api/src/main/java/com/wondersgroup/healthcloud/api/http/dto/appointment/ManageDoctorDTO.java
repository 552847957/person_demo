package com.wondersgroup.healthcloud.api.http.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentDoctor;
import lombok.Data;

/**
 * Created by longshasha on 16/12/13.
 * 用于后台管理
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManageDoctorDTO {

    /**
     * 医生ID
     */
    private String id;

    /**
     * 医生姓名
     */
    private String name;


    /**
     * 医生简介
     */
    private String doctInfo;

    /**
     * 医生头像
     */
    private String avatar;

    public ManageDoctorDTO(AppointmentDoctor doctor) {
        if(doctor!=null){
            this.id = doctor.getId();
            this.name = doctor.getDoctName();
            this.doctInfo = doctor.getDoctInfo();
            this.avatar = doctor.getAvatar();
        }
    }

    public AppointmentDoctor mergeDoctor(AppointmentDoctor doctor, ManageDoctorDTO manageDoctorDTO) {
        if(manageDoctorDTO!=null){
            doctor.setDoctInfo(manageDoctorDTO.getDoctInfo());
            doctor.setAvatar(manageDoctorDTO.getAvatar());
        }
        return doctor;

    }
}
