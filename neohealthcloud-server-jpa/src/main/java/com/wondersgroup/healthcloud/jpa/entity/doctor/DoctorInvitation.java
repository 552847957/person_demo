package com.wondersgroup.healthcloud.jpa.entity.doctor;

import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by zhangzhixiu on 7/7/15.
 */
@Data
@Entity
@Table(name = "app_tb_docinvite")
public class DoctorInvitation extends BaseEntity {

    @Column(name = "docid")
    private String doctorId;
    @Column(name = "docinfo")
    private String doctorInfo;
    private String personcard;
    private String mobile;
    private String name;
    private String code;
    @Column(name = "registerid")
    private String userId;
    @Column(name = "send_date")
    private Date sendDate;
    @Column(name = "success_date")
    private Date successDate;
}
