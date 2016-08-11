package com.wondersgroup.healthcloud.jpa.entity.user;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by longshasha on 16/8/9.
 */
@Data
@Entity
@Table(name = "app_tb_register_userinfo")
public class UserInfo {

    @Id
    private String registerid;

    private Integer height;

    private Float weight;

    private Float waist;

    private Integer age;


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
