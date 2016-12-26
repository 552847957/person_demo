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

    /**
     * 预约序号
     */
    private String visitNo;

    private String hosNumSourceId;

    private String contactId;

    private String userName;//患者姓名

    private String userCardId;//患者身份证

    private String userPhone;//患者手机号

    private String mediCardId;//社保卡

    private Date createDate;//提交订单时间

    private Date cancelTime;//订单取消时间

    /**
     * 预约状态 1：已预约； 2：已支付；3：已退号； 4：已取号； 7：停诊未通知 8：退号中 ; 9：停诊已通知
     */
    private String orderStatus;

    /**
     * 状态 1 正常 2停诊
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

    private String hosOrgCode;

    /**
     * 就诊类型 1|2|3  1专家.2专病3.普通
     */
    private String registerType;

    /**
     * 2专病3.普通 对应的门诊名称
     */
    private String registerName;


}
