package com.wondersgroup.healthcloud.jpa.entity.appointment;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by longshasha on 16/5/21.
 */
@Data
@Entity
@Table(name = "app_tb_appointment_department_l1")
public class AppointmentL1Department {
    @Id
    private String id;

    @Column(name = "hospital_id")
    private String hospitalId;

    @Column(name = "dept_code")
    private String hosDeptCode;//科室代码

    @Column(name = "dept_name")
    private String deptName;//科室名称

    @Column(name = "dept_desc")
    private String deptDesc;//科室简介

    @Column(name = "dept_type")
    private String deptType;//科室类型 专家和普通科室--1 专病科室传--2


    private Integer weight;//权重
    @Column(name = "del_flag")
    private String delFlag;

    @Column(name = "source_id")
    private String sourceId;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "update_by")
    private String updateBy;


}
