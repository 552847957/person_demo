package com.wondersgroup.healthcloud.jpa.entity.doctor;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "app_tb_doctor_template")
public class DoctorTemplate {

    @Id
    private String id;
    @Column(name = "doctor_id")
    private String doctorId;
    private String type;
    private String title;
    private String content;
    @Column(name = "update_time")
    private Date updateTime;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "del_flag")
    private String delFlag;
}
