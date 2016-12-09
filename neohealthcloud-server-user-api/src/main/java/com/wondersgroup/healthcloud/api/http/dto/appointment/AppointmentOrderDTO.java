package com.wondersgroup.healthcloud.api.http.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


/**
 * Created by longshasha on 16/5/24.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentOrderDTO {

    private String id;//预约单Id

    @JsonProperty("hospital_name")
    private String hospitalName;//医院名称

    @JsonProperty("department_name")
    private String departmentName;//科室名称

    @JsonProperty("doctor_name")
    private String doctorName;//医生名称

    @JsonProperty("duty_name")
    private String dutyName;//医生职称

    @JsonProperty("schedule_date")
    private String scheduleDate;//就诊时间

    /**
     * 周
     */
    private String week;

    /**
     * 时间段
     */
    private String time;

    private String fee;//费用

    /**
     * 门诊类型
     */
    @JsonProperty("visit_level")
    private String visitLevelCode;

    @JsonProperty("order_type")
    private String orderType;//预约类型 1:医生,2:科室

    /**
     * 预约单Id
     */
    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("patient_name")
    private String patientName;//患者姓名

    @JsonProperty("patient_idcard")
    private String patientIdcard;//患者身份证

    @JsonProperty("patient_mobile")
    private String patientMobile;//患者手机号

    @JsonProperty("medi_card_id")
    private String mediCardId;//社保卡

    @JsonProperty("order_time")
    private String orderTime;//提交订单时间

    /**
     * 预约状态
     * 1:预约成功,2:就诊成功,3:用户取消,4:爽约,5:系统取消
     */
    private String status;

    /**
     * 是否可以取消(已过就诊日期)
     */
    @JsonProperty("can_cancel")
    private Boolean canCancel;//是否可以取消

    /**
     * 取消预约的备注
     * 不迟于2016年11月20日16点50分前取消，逾期取消失败。
     */
    @JsonProperty("cancel_desc")
    private String cancelDesc;


}
