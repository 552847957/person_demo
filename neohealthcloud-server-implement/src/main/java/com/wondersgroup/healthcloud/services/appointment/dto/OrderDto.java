package com.wondersgroup.healthcloud.services.appointment.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by longshasha on 16/12/9.
 * 用于数据库查询
 */
@Data
public class OrderDto {

    private String id;//预约单Id

    private String orderType;//预约类型 1:医生,2:科室

    /**
     * 预约单Id
     */
    private String orderId;

    private String userName;//患者姓名

    private String userCardId;//患者身份证

    private String userPhone;//患者手机号

    private String mediCardId;//社保卡

    private Date createDate;//提交订单时间

    private Date cancelTime;//订单取消时间

    /**
     * 预约状态
     * 1:预约成功,2:就诊成功,3:用户取消,4:爽约,5:系统取消
     */
    private String status;



    private Date startTime;

    private Date endTime;

    private String hospitalName;//医院名称

    private String departmentName;//科室名称

    private String doctorName;//医生名称

    private String dutyName;//医生职称

    private Date scheduleDate;//就诊时间
    /**
     * 出诊费用
     */
    private String visitCost;

    /**
     * 门诊类型
     */
    private String visitLevelCode;
    /**
     * 排班的状态
     * 1 正常 2停诊'
     */
    private String scheduleStatus;

    /**
     * 预约关闭日期，整数，若为1 即前一天不能预约、退号明天的号源
     */
    private String closeDays;

    /**
     * 0-24的整数，代表时间，同上两个字段结合，起来为提前closeDays天closeTimeHour点之前不能预约和退号明天的号源
     */
    private String closeTimeHour;

    /**
     * 用户的手机号(用于后台管理订单信息)
     */
    private String regmobilephone;

    private String timeRange;


}
