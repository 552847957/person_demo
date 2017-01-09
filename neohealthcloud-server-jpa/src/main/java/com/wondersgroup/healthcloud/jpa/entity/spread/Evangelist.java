package com.wondersgroup.healthcloud.jpa.entity.spread;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by nick on 2016/12/23.
 * @author nick
 */
@Data
@Entity
@Table(name = "app_tb_local_spread")
public class Evangelist {
    @Id
    private String id;
    @Column(name = "spread_code")
    private String spreadCode;// 地推邀请码
    @Column(name = "name")
    private String name;// 地推人员姓名
    @Column(name = "staff_id")
    private String staffId;// 地推人员工号
    @Column(name = "mobilephone")
    private String mobilephone;// 地推人员电话
    @Column(name = "del_flag")
    private String delFalg;// 删除标志 0：不删除 1：已删除
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;// 创建时间
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;// 更新时间
}
