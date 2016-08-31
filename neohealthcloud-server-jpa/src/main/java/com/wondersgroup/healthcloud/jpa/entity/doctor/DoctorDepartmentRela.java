package com.wondersgroup.healthcloud.jpa.entity.doctor;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by longshasha on 16/8/31.
 */
@Data
@Entity
@Table(name = "app_tb_doctor_departmentcare")
public class DoctorDepartmentRela {

    @Id
    private String id;
    private String doctorid;
    private String departid;

    @Column(name = "del_flag")
    private String delFlag = "0";

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_by")
    private String updateBy;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "source_id")
    private String sourceId;
}
