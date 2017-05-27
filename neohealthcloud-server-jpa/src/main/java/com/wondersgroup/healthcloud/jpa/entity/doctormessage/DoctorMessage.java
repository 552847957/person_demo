package com.wondersgroup.healthcloud.jpa.entity.doctormessage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

import java.util.Date;

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

    @Column(name = "is_read")
    private Integer isRead;//是否已读 0:未读，1:已读

    @Column(name = "del_flag")
    private String delFlag;//删除标志

}
