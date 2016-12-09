package com.wondersgroup.healthcloud.services.appointment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by longshasha on 16/12/9.
 */
@Data
public class OrderDto {

    private String id;//预约单Id

    private String orderType;//预约类型 1:医生,2:科室
    /**
     * 出诊费用
     */
    private String visitCost;

    /**
     * 门诊类型
     */
    private String visitLevelCode;

    /**
     * 预约单Id
     */
    private String orderId;

    private String userName;//患者姓名

    private String userIdcard;//患者身份证

    private String userMobile;//患者手机号

    private String mediCardId;//社保卡

    private String createDate;//提交订单时间

    private String cancelTime;//订单取消时间

    /**
     * 预约状态
     * 1:预约成功,2:就诊成功,3:用户取消,4:爽约,5:系统取消
     */
    private String status;

    private String hospitalName;//医院名称

    private String departmentName;//科室名称

    private String doctorName;//医生名称

    private String dutyName;//医生职称

    private String scheduleDate;//就诊时间
}
