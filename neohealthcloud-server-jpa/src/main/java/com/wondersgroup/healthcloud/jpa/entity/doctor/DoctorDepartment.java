package com.wondersgroup.healthcloud.jpa.entity.doctor;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by longshasha on 16/8/31.
 */
@Data
@Entity
@Table(name = "app_dic_doctor_department")
public class DoctorDepartment {

    @Id
    private String id;
    private String name;
    private String pid;
    private String sort;
    private String isview;
    private String tagnum;

    @Column(name = "del_flag")
    private String delFlag;

    @Column(name = "create_by")
    private String createBy;
    @Column(name = "create_date")
    private String createDate;
    @Column(name = "update_by")
    private String updateBy;
    @Column(name = "update_date")
    private String updateDate;
    @Column(name = "source_id")
    private String sourceId;


}
