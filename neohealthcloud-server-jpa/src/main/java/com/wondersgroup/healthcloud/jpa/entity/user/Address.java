package com.wondersgroup.healthcloud.jpa.entity.user;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by longshasha on 16/8/10.
 */
@Data
@Entity
@Table(name = "app_tb_register_address")
public class Address {

    @Id
    private String id;

    @Column(name = "registerid")
    private String userId;

    private String province;

    private String city;

    private String county;

    private String town;

    private String committee;

    private String other;

    private String remarks;

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
