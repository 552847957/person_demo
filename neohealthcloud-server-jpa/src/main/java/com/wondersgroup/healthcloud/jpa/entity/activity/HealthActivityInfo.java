package com.wondersgroup.healthcloud.jpa.entity.activity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

import com.google.gson.annotations.SerializedName;
import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;

/**
 * 
 * Created by sunhaidi on 2016.8.12
 */
@Data
@Entity
@Table(name = "app_tb_healthactivity_info")
public class HealthActivityInfo {

    private static final long serialVersionUID = -6988698188678786262L;
    @Id
    private String            activityid;
    private String            host;                                    // '主办者',
    private String            type;                                    // '活动类型 1：糖尿病:2：高血压',
    private String            title;                                   // '标题',
    private Date              releasetime;                             // '发布时间',
    private String            summary;                                 // '活动概述',
    private Date              starttime;                               // '开始时间',
    private Date              endtime;                                 // '结束时间',
    private String            province;                                // '地址 省 area字典表代码',
    private String            city;                                    // '地址 市 area字典表代码',
    private String            county;                                  // '地址 县或区 area字典表代码',
    private String            locate;                                  // '举办地点',
    private String            photo;                                   // '活动图片存入attach表',
    private String            speaker;                                 // '主讲人信息 姓名 科室 职务',
    private String            department;                              //科室
    private String            pftitle;                                 //职称
    private String            iscancel;                                // '是否取消 0：未取消 1：取消',
    private Integer           quota;                                   // '名额',
    private Integer           style;                                   //'活动形式1：讲座 2：表演',
    private String            thumbnail;                               //活动缩略图
    @Column(name = "online_status")
    @SerializedName("online_status")
    private String            onlineStatus;                            //0 未上线 1已上线 2已下线',
    @Column(name = "online_time")
    @SerializedName("online_time")
    private Date              onlineTime;                              //上线时间
    @Column(name = "offline_time")
    @SerializedName("offline_time")
    private Date              offlineTime;                             //下线时间
    @Column(name = "enroll_start_time")
    @SerializedName("enroll_start_time")
    private Date              enrollStartTime;                         //活动报名开始时间'
    @Column(name = "enroll_end_time")
    @SerializedName("enroll_end_time")
    private Date              enrollEndTime;                           //活动报名结束时间
    @Column(name = "del_flag")
    @SerializedName("del_flag")
    private String            delFlag;
    @Column(name = "source_id")
    @SerializedName("source_id")
    private String            sourceId;
    @Column(name = "create_by")
    @SerializedName("create_by")
    private String            createBy;
    @Column(name = "create_date")
    @SerializedName("create_date")
    private Date              createDate;
    @Column(name = "update_by")
    @SerializedName("update_by")
    private String            updateBy;
    @Column(name = "update_date")
    @SerializedName("update_date")
    private Date              updateDate;

}
