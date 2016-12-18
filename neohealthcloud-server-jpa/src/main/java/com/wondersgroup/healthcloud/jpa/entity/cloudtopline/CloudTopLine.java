package com.wondersgroup.healthcloud.jpa.entity.cloudtopline;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 云头条 父节点
 * Created by xianglinhai on 2016/12/9.
 */
@Entity
@Data
@JsonNaming
@Table(name = "app_tb_cloud_top_line")
public class CloudTopLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "title")
    private String title;

    @Column(name = "jump_url")
    private String jumpUrl;

    @Column(name = "jump_id")
    private String jumpId;

    @Column(name = "type")
    private Integer type;

    @Column(name = "del_flag")
    private String delFlag;

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;


}
