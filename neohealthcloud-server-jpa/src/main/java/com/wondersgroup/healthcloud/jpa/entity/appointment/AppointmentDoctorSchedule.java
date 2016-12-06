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
@Table(name = "app_tb_appointment_doctor_schedule")
public class AppointmentDoctorSchedule {

    @Id
    private String id;
    @Column(name = "schedule_id")
    private String scheduleId; //排班ID

    @Column(name = "num_source_id")
    private String numSourceId;//号源Id

    @Column(name = "hospital_id")
    private String hospitalId;//医院表的id

    @Column(name = "department_l1_id")
    private String l1DepartmentId;//一级科室id

    @Column(name = "department_l2_id")
    private String l2DepartmentId;//二级科室id

    @Column(name = "doctor_id")
    private String doctorId;//医生 预约类型为科室时可不填

    @Column(name = "order_type")
    private String orderType;//预约类型 1: 医生；2：医生级别；3：科室；

    @Column(name = "visit_level_code")
    private String visitLevelCode;//出诊级别编码 1专家2专病3普通

    /**
     * 出诊级别
     * 0:其他;1:住院医师;2:主治医生;3:副主任医师;4:主任医师;5:名老专家。
     * 由于各医院的出诊级别没有标准化，第三方接口直接用中文表示出诊级别
     */
    @Column(name = "visit_level")
    private String visitLevel;

    @Column(name = "visit_cost")
    private String visitCost;//出诊费用

    @Column(name = "schedule_date")
    private Date scheduleDate;//日期

    @Column(name = "time_range")
    private String timeRange;//上下午标志 1:上午 2:下午 3:晚上

    @Column(name = "start_time")
    private String startTime;//就诊时段_开始时间 2016-11-24 13:30:00

    @Column(name = "end_time")
    private String endTime;//就诊时段_结束时间 2016-11-24 14:30:00

    @Column(name = "ordered_num")
    private Integer orderedNum;//已预约数

    @Column(name = "reserve_order_num")
    private Integer reserveOrderNum;//可预约总数 可预约总数即放号数，包括已预约号源数

    @Column(name = "sum_order_num")
    private Integer sumOrderNum; //可预约总数即放号数，包括已预约号源数。(共享号源医院该值为空)

    @Column(name = "visit_no")
    private String visitNo;//序号 以|分割的数字串

    @Column(name = "status")
    private String status;//1 正常 2停诊

    /**
     * 就诊类型
     * 1|2|3  1专家.2专病3.普通 为空查询所有，为3时不能填医生ID，普通类型没有医生ID。
     */
    @Column(name = "register_type")
    private String registerType;

    @Column(name = "del_flag")
    private String delFlag;
    @Column(name = "source_id")
    private String sourceId;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_date")
    private Date updateDate;
    @Column(name = "update_by")
    private String updateBy;
}
