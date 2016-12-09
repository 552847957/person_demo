package com.wondersgroup.healthcloud.jpa.entity.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 匿名账户表
 * Created by sunhaidi on 2016.8.5
 */
@Data
@Entity
@Table(name = "app_tb_anonymous_account")
public class AnonymousAccount{
    @Id
    private String id;
    private String username;
    private String password;
    private String creator;
    private String name;
    private String idcard;
    @Column(name = "is_child")
    private Boolean isChild;
    @Column(name = "del_flag")
    private String delFlag = "0";
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
    
    private String appellation;
    private String height;
    private String weight;
    @Column(name = "birth_date")
    private Date birthDate;
    private String sex;
    private String mobile;
    private String nickname;
    @Column(name = "is_standalone")
    private Boolean isStandalone;
    
}
