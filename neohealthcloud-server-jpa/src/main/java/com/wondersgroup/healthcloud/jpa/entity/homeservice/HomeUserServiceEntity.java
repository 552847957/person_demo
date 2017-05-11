package com.wondersgroup.healthcloud.jpa.entity.homeservice;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2017/5/9.
 */
@Data
@Entity
@Table(name = "app_tb_user_service")
public class HomeUserServiceEntity {
    @Id
    private String id;
    @Column(name = "register_id")
     private String registerId; //主标题
    @Column(name = "service_id")
    private String  serviceId; //服务ID
    @Column(name = "del_flag")
    private  String delFlag; // 删除标志 0：不删除 1：已删除
    @Column(name = "create_time")
    private Date createTime; //创建时间
    @Column(name = "update_time")
    private Date updateTime; // 更新时间
}
