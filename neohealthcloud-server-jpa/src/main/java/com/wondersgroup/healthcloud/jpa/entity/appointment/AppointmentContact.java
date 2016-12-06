package com.wondersgroup.healthcloud.jpa.entity.appointment;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by longshasha on 16/3/7.
 */
@Data
@Entity
@Table(name = "app_tb_appointment_contact")
public class AppointmentContact {

    @Id
    private String id;

    private String uid;//创建人

    /**
     * 预约平台用户ID
     */
    @Column(name = "platform_user_id")
    private String platformUserId;

    /**
     * 预约平台成员Id 若不为空则此人为uid对应的家庭成员
     */
    @Column(name = "member_id")
    private String memberId;

    /**
     * 预约平台用户密码
     */
    @Column(name = "user_pwd")
    private  String userPwd;


    private String name;
    private String idcard;
    private String mobile;

    /**
     * 社保卡
     */
    @Column(name = "medi_card_id")
    private String mediCardId;

    @Column(name = "is_default")
    private String isDefault;//是否默认
    @Column(name = "del_flag")
    private String delFlag;


    @Column(name = "create_time")
    private Date createTime;

    public AppointmentContact() {
    }

    public AppointmentContact(String id, String uid, String name, String idcard, String mobile, String isDefault) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.idcard = idcard;
        this.mobile = mobile;
        this.isDefault = isDefault;
    }
}
