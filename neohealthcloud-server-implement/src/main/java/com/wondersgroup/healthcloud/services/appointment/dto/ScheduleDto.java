package com.wondersgroup.healthcloud.services.appointment.dto;


import lombok.Data;

import java.util.Date;

/**
 * Created by longshasha on 16/12/7.
 * 用于数据库查询
 */
@Data
public class ScheduleDto {

    private String id;

    private String name;

    /**
     * 医生头像
     */
    private String avatar;

    /**
     *医生职称
     */
    private String dutyName;

    /**
     * 医生简介
     */
    private String specialty;

    /**
     * 预约数
     */
    private String reservationNum;


    /**
     * 1:医生 2:科室
     */
    private String type;

    /**
     * 就诊时间
     */
    private Date scheduleDate;

    private Date startTime;

    private Date endTime;

    /**
     * 出诊费用
     */
    private String visitCost;

    /**
     * 剩余号源数
     */
    private Integer reserveOrderNum;

    /**
     * 已预约号源数
     */
    private Integer orderedNum;

    /**
     * 门诊类型
     * 1专家2专病3普通
     */
    private String visitLevelCode;

    private String hospitalName;//医院名称

    private String departmentName;//二级科室名称

    private String doctorId;//科室排班时为科室Id 医生排班时为医生Id




}
