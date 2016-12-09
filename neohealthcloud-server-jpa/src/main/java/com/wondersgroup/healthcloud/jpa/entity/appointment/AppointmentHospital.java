package com.wondersgroup.healthcloud.jpa.entity.appointment;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by longshasha on 16/5/21.
 */
@Data
@Entity
@Table(name = "app_tb_appointment_hospital")
public class AppointmentHospital {
    @Id
    private String id;
    @Column(name = "hos_org_code")
    private String hosOrgCode;//机构编码

    @Column(name = "hos_name")
    private String hosName;//医院名称

    @Column(name = "hospital_add")
    private String hospitalAdd;//地址

    @Column(name = "hospital_rule")
    private String hospitalRule;//预约须知

    @Column(name = "hospital_web")
    private String hospitalWeb;//医院网址

    @Column(name = "traffic_guide")
    private String trafficGuide;//交通指南

    @Column(name = "hospital_desc")
    private String hospitalDesc;//医院简介

    @Column(name = "hospital_tel")
    private String hospitalTel;//电话

    @Column(name = "hospital_grade")
    private String hospitalGrade;//医院等级1：一级医院  2：二级医院 3：三级医院' 文字

    @Column(name = "pay_mode")
    private String payMode;//多选，|1|2|3|） 1：第三方支付  2：诊疗卡支付， 3：窗口支付。

    @Column(name = "order_mode")
    private String orderMode;//预约方式 |1|2| 1：有卡预约，2：无卡预约。

    /**
     * 是否支持预约当天
     * 1：支持
     * 0：不支持
     */
    @Column(name = "is_order_today")
    private String isOrderToday;

    /**
     * 可预约天数
     */
    @Column(name = "order_range")
    private Integer orderRange;

    /**
     * 是否支持分时段
     * 0：支持(时段选择,序号选择同时支持),
     * 1：不支持,
     * 2：支持(仅支持时段选择),
     * 3：支持(仅支持序号选择)
     */
    @Column(name = "is_sp_time")
    private String isSpTime;

    /**
     * 预约关闭日期，整数，若为1
     * 即前一天不能预约、退号明天的号源
     */
    @Column(name = "close_days")
    private String closeDays;

    /**
     * 0-24的整数，代表时间，同上两个字段结合，起来为提前closeDays天closeTimeHour点之前不能预约和退号明天的号源
     */
    @Column(name = "close_time_hour")
    private String closeTimeHour;//预约关闭时间

    @Column(name = "address_county")
    private String addressCounty;//区县代码

    @Column(name = "pic_small")
    private String picSmall;//医院列表小图

    @Column(name = "pic_big")
    private String picBig;//图片详情大图

    private String isonsale;  //是否上架

    private Integer weight;//权重

    /**
     * 医院的医生总数
     */
    @Column(name = "doctor_num")
    private Integer doctorNum;

    @Column(name = "del_flag")
    private String delFlag;

    @Column(name = "source_id")
    private String sourceId;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "update_by")
    private String updateBy;


}
