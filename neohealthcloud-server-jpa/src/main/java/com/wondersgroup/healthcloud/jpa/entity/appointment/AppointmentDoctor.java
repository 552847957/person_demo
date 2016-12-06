package com.wondersgroup.healthcloud.jpa.entity.appointment;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by longshasha on 16/5/21.
 */
@Data
@Entity
@Table(name = "app_tb_appointment_doctor")
public class AppointmentDoctor {

    @Id
    private String id;

    @Column(name = "hospital_id")
    private String hospitalId;

    @Column(name = "department_l1_id")
    private String l1DepartmentId;

    @Column(name = "department_l2_id")
    private String l2DepartmentId;

    @Column(name = "doct_code")
    private String hosDoctCode;

    @Column(name = "doct_name")
    private String doctName;

    @Column(name = "doct_info")
    private String doctInfo;

    @Column(name = "doct_tile")
    private String doctTile;

    @Column(name = "doct_add")
    private String doctAdd;

    /**
     * 医生头像
     */
    private String avatar;

    private String isonsale;

    /**
     * 医生预约数
     */
    @Column(name = "reservation_num")
    private Integer reservationNum;

    @Column(name = "del_flag")
    private String delFlag;
    @Column(name = "source_id")
    private String sourceId;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_date")
    private Date updateDate;
    @Column(name = "update_by")
    private String updateBy;


    @Transient
    private String hospitalName;//医院名称
    @Transient
    private String departmentName;//二级科室名称

    @Transient
    private String reservationRule;//预约规则

}
