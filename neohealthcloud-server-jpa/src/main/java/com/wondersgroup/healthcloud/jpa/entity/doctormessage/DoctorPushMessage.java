package com.wondersgroup.healthcloud.jpa.entity.doctormessage;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


@Entity
@Table(name = "app_tb_doctor_sendmessage")
@Data
public class DoctorPushMessage {

	@Id
    private String id;
    private String uid;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String title;
    private String content;
    private String updateTime;
    private String detail;
}
