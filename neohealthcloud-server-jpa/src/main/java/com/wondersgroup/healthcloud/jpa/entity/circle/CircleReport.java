package com.wondersgroup.healthcloud.jpa.entity.circle;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;

/**
 * 健康圈举报表
 * Created by sunhaidi on 2016.8.28
 */
@Entity
@Data
@Table(name = "app_tb_reportcontent")
public class CircleReport extends BaseEntity {
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

}
