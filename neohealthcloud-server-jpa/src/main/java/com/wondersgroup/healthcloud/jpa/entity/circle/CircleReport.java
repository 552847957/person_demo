package com.wondersgroup.healthcloud.jpa.entity.circle;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

import org.hibernate.annotations.GenericGenerator;

/**
 * 健康圈举报表
 * Created by sunhaidi on 2016.8.28
 */
@Entity
@Data
@Table(name = "app_tb_reportcontent")
public class CircleReport {
    private String registerid;
    private String content;
    private String reportid;
    private Integer contenttype;
    private Integer reportnum;
    private Date reporttime;
    private Integer reporttype;
    private String operator;
    private String dealtime;
    private String dealstatus;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
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
