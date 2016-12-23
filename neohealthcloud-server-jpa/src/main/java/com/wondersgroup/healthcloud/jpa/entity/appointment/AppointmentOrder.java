package com.wondersgroup.healthcloud.jpa.entity.appointment;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by longshasha on 16/5/24.
 */

@Data
@Entity
@Table(name = "app_tb_appointment_order")
public class AppointmentOrder {
    @Id
    private String id;

    private String uid;

    /**
     * 就诊人Id
     */
    @Column(name = "contact_id")
    private String contactId;

    @Column(name = "hospital_id")
    private String hospitalId;//医院表的id

    @Column(name = "department_l1_id")
    private String l1DepartmentId;//一级科室id

    @Column(name = "department_l2_id")
    private String l2DepartmentId;//二级科室id

    @Column(name = "doctor_id")
    private String doctorId;//医生id

    /**
     * app_tb_appointment_doctor_schedule 主键
     */
    @Column(name = "appointment_schedule_id")
    private String appointmentScheduleId;

    //用户平台Id
    @Column(name = "platform_user_id")
    private String platformUserId;

    /**
     * 预约平台订单Id
     */
    @Column(name = "order_id")
    private String orderId;

    /**
     * 预约平台预约号
     */
    @Column(name = "hos_num_source_id")
    private String hosNumSourceId;

    /**
     * 预约序号
     */
    @Column(name = "visit_no")
    private String visitNo;

    /**
     * 取号密码
     */
    @Column(name = "take_password")
    private String takePassword;

    /**
     * 预约平台排班Id
     */
    @Column(name = "schedule_id")
    private String scheduleId;

    /**
     * 预约平台号源Id
     */
    @Column(name = "num_source_id")
    private String numSourceId;//号源Id


    /**
     * 支付方式 1：第三方支付，2：诊疗卡支付，3：窗口支付
     */
    @Column(name = "pay_mode")
    private String payMode;

    /**
     * 1：已付费；2：未付费
     */
    @Column(name = "pay_state")
    private String payState;

    /**
     * 0:无卡，初诊病人 1：社保卡（医保卡）2：上海医联卡
     */
    @Column(name = "medi_card_type")
    private String mediCardType;

    @Column(name = "medi_card_id")
    private String mediCardId;//卡号

    /**
     * 1：居民身份证 2：居民户口簿 3：护照 4：军官证（士兵证） 5：驾驶执照 6：港澳居民来往内地通行证 7：台湾居民来往内地通行证 99：其他
     */
    @Column(name = "user_card_type")
    private String userCardType;

    /**
     * 证件号码 contact表中idcard
     */
    @Column(name = "user_card_id")
    private String userCardId;

    @Column(name = "user_name")
    private String userName;//contact表中name

    @Column(name = "user_phone")
    private String userPhone;//contact表中mobile

    /**
     * 0：未知的性别 1：男性 2：女性  5：女性改（变）为男性 6：男性改（变）为女性 9：未说明的性别
     */
    @Column(name = "user_sex")
    private String userSex;

    @Column(name = "user_bd")
    private String userBd;//用户出生日期 yyyy-MM-dd

    @Column(name = "user_cont_add")
    private String userContAdd;//用户联系地址

    /**
     * 预约状态 1：已预约； 2：已支付；3：已退号； 4：已取号；5：待退费
     */
    @Column(name = "order_status")
    private String orderStatus;

    /**
     * 排班状态 1正常 2 停诊
     */
    private String status;//状态

    @Column(name = "order_time")
    private Date orderTime;//医生坐诊时间 预约资源的 start_time

    @Column(name = "cancel_time")
    private Date cancelTime;//取消时间

    /**
     * 1：患者；2：服务商
     */
    @Column(name = "cancel_obj")
    private String cancelObj;

    /**
     * 0：其他 1：患者主动退号
     */
    @Column(name = "cancel_reason")
    private String cancelReason;//退号原因

    @Column(name = "close_sms")
    private String closeSms = "0";

    /**
     * 备注 只有退号原因为其他时才有用
     */
    @Column(name = "cancel_desc")
    private String cancelDesc;

    /**
     * 接口没有这个字段 暂留
     */
    @Column(name = "order_type")
    private String orderType;//预约类型 1:医生,2:科室

    private String evaluation;//评价

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
