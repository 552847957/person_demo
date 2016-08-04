package com.wondersgroup.healthcloud.jpa.entity.user;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by longshasha on 16/8/4.
 */
@Data
@Entity
@Table(name = "app_tb_register_info")
public class RegisterInfo {
    @Id
    private String registerid; //主键

    private String userid; //用户id

    private String usertype; //用户类型（1.患者，2.医生）

    private String identifytype; //认证类型（0.未认证，1.认证）

    private String personcard; //身份证

    private String regmobilephone; //手机号

    @Column(name = "bind_personcard")
    private String bindPersoncard; //医养结合服务绑定的身份证

    private String medicarecard;//医保卡

    private String regpassword; //密码

    private String headphoto; //头像

    private Date regtime; //注册时间

    private String nickname; //昵称

    private String talkid; //环信id
    private String talkpwd; //环信pwd
    private String name;
    private String gender;
    private Date birthday;
    private String tagid;
    private Integer channelType; // 渠道类型：1:健康云、2:QQ、3:微信、4:微博

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
