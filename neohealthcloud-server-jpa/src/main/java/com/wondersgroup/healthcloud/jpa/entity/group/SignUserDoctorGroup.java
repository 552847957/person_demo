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
 *      患者分组中间表
 */
@Entity
@Data
@JsonNaming
@Table(name = "app_tb_sign_user_doctor_group")
public class SignUserDoctorGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "user_id")
    private String uid;
    
    @Column(name = "group_id")
    private Integer groupId;
    
    @Column(name = "del_flag")
    private String delFlag="0";
    
    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    
    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;
}
