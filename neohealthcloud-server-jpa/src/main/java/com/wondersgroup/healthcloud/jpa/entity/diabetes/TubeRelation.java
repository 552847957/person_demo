package com.wondersgroup.healthcloud.jpa.entity.diabetes;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by zhuchunliu on 2016/12/14.
 */
@Data
@Entity
@Table(name = "app_tb_diabetes_tube_relation")
public class TubeRelation {
    @Id
    private String id;
    private String registerid;//用户id
    @Column(name="hospital_code")
    private String hospitalCode;//在管医院
    @Column(name="doctor_name")
    private String doctorName;//在管医生姓名
    @Column(name = "del_flag")
    private String delFlag;
    @Column(name = "create_date")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createDate;
    @Column(name = "update_date")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateDate;
}
