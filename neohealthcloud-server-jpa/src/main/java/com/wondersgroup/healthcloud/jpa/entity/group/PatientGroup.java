package com.wondersgroup.healthcloud.jpa.entity.group;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * 
 * @author zhongshuqing 
 *     患者分组
 */
@Entity
@Data
@JsonNaming
@Table(name = "app_tb_patient_group")
public class PatientGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    
    private Integer rank;//排序
    
    @Column(name = "doctor_id")
    private String doctorId;
    
    private String isDefault;
    
    @Column(name = "del_flag")
    private String delFlag="0";
    
    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
}
