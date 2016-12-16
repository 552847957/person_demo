package com.wondersgroup.healthcloud.jpa.entity.moduleportal;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xianglinhai on 2016/12/12.
 */
@Data
@Entity
@Table(name = "app_tb_module_portal")
public class ModulePortal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "main_title")
    private String mainTitle;

    @Column(name = "sub_title")
    private String subTitle;

    @Column(name = "jump_url")
    private String jumpUrl;

    @Column(name = "is_visible")
    private String isVisible;

    @Column(name = "sort")
    private int sort;

    @Column(name = "del_flag")
    private String delFlag;

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;



}
