package com.wondersgroup.healthcloud.jpa.entity.doctor;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "app_tb_doctor_used_template")
public class DoctorUsedTemplate {
    @Id
    private String id;
    @Column(name = "doctor_id")
    private String doctorId;
    @Column(name = "template_id")
    private String templateId;
    @Column(name = "create_time")
    private Date createTime;

    public DoctorUsedTemplate(){}
    public DoctorUsedTemplate(String doctorId,String templateId){
        this.doctorId = doctorId;
        this.templateId = templateId;
        this.createTime = new Date();
    }
}
