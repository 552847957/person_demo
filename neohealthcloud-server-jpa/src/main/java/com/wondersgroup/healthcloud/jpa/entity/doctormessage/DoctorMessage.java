package com.wondersgroup.healthcloud.jpa.entity.doctormessage;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "app_tb_doctor_message")
@Data
public class DoctorMessage{

	@Id
    private String id;
    private String send;
    private String sendId;
    private String receive;
    private String receiveId;
    private String title;
    private String content;
    private String updateDate;
    private String msgType;

}
