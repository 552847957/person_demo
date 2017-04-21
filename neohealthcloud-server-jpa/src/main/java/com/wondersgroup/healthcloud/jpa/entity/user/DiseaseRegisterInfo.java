package com.wondersgroup.healthcloud.jpa.entity.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "app_tb_register_info_disease")
public class DiseaseRegisterInfo {
    @Id
    @Column(name = "id")
    public String id;
    @Column(name = "registerid")
    public String registerid;
    @Column(name = "type")   //'慢病类型 1：确诊人群，2：高危人群',
    public String type;
    @Column(name = "del_flag")
    public String delFlag;
    @Column(name = "create_date")
    public Date   createDate;
    @Column(name = "update_date")
    public Date   updateDate;

}
