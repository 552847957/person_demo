package com.wondersgroup.healthcloud.jpa.entity.config;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * Created by zhaozhenxing on 2016/8/16.
 */
@Data
@Entity
@Table(name = "app_tb_neoconfiguration")
public class AppConfig {
    @Id
    private String id;
    @Column(name = "key_word")
    private String keyWord;
    private String data;
    private Integer discrete;
    private String remark;
    @Column(name = "del_flag")
    private String delFlag;
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @Column(name = "main_area", nullable = false)
    private String mainArea;
    @Column(name = "spec_area")
    private String specArea;
    private String source;// 1-用户端;2-医生端
}
