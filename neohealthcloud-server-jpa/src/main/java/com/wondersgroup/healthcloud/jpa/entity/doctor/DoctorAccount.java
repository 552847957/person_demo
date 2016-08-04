package com.wondersgroup.healthcloud.jpa.entity.doctor;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by longshasha on 16/8/1.
 */
@Data
@Entity
@Table(name = "doctor_account_tb")
public class DoctorAccount {
    @Id
    private String id;
    private String mobile;
    private String name;

    private String nickname;
    @Column(name = "login_name")
    private String loginName;
    private String avatar;
    private String talkid;
    private String talkpwd;
    private String talkgroupid;

    private Date regtime;//注册时间
    @Column(name = "is_available")
    private String isAvailable;//是否可用

    @Column(name = "del_flag")
    private String delFlag;

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


}
