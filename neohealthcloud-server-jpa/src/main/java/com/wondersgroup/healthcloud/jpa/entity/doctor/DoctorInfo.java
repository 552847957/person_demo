package com.wondersgroup.healthcloud.jpa.entity.doctor;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by longshasha on 16/8/1.
 */
@Data
@Entity
@Table(name = "doctor_info_tb")
public class DoctorInfo {

    @Id
    private String id;

    @Column(name = "hospital_id")
    private String hospitalId;
    private String no;

    @Column(name = "depart_standard")
    private String departStandard;//标准科室编码 对应字典表
    private String idcard;
    private String gender;

    @Column(name = "duty_id")
    private String dutyId;//医生职称编码 对应字典表
    private String expertin;
    private String introduction;

    private int actcode;//医生推广邀请码


    @Column(name = "his_doctor_id")
    private String hisDoctorId;

    @Column(name = "his_hospital_id")
    private String hisHospitalId;

    @Column(name = "his_num")
    private String hisNum;

    @Column(name = "del_flag")
    private String delFlag;

    @Column(name = "source_id")
    private String sourceId;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_by")
    private String updateBy;

    @Column(name = "update_date")
    private Date updateDate;
}
