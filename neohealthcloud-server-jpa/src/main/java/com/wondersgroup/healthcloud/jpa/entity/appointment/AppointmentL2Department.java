package com.wondersgroup.healthcloud.jpa.entity.appointment;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by longshasha on 16/5/21.
 */
@Data
@Entity
@Table(name = "app_tb_appointment_department_l2")
public class AppointmentL2Department {
    @Id
    private String id;

    @Column(name = "hospital_id")
    private String hospitalId;

    @Column(name = "department_l1_id")
    private String l1DepartmentId;

    @Column(name = "dept_code")
    private String hosDeptCode;

    @Column(name = "dept_name")
    private String deptName;

    @Column(name = "dept_desc")
    private String deptDesc;

    @Column(name = "dept_type")
    private String deptType;//科室类型 专家和普通科室--1 专病科室传--2

    private Integer weight;//权重

    private String isonsale;//是否显示科室预约  “0”表示下架科室预约， “1”表示上架科室预约'

    /**
     * 科室级别预约数
     */
    @Column(name = "reservation_num")
    private Integer reservationNum = 0;


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

    @Transient
    private String hospitalName;
    @Transient
    private String reservationRule;

}
