package com.wondersgroup.healthcloud.jpa.entity.doctor;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;
import javax.persistence.*;

/**
 * Created by zhaozhenxing on 2016/12/07.
 */

@Data
@Entity
@Table(name = "app_tb_doctor_intervention")
public class DoctorIntervention {
    /**
     * 餐前血糖正常值水平
     */
    public static final String BEFORE_REFERENCE = "4.4~7.0";

    /**
     * 餐后血糖正常值范围
     */
    public static final String AFTER_REFERENCE = "4.4~10.0";
    @Id
    @Column(name = "id")
    private String id;// 流水ID
    @Column(name = "doctor_id")
    private String doctorId;// 医生ID
    @Column(name = "patient_id")
    private String patientId;// 患者ID
    @Column(name = "type")
    private String type;// 建议类型 1:异常血糖干预
    @Column(name = "content")
    private String content;// 医生建议内容
    @Column(name = "fpg_value")
    private String fpgValue;// 血糖值
    @Column(name = "test_time")
    private String testTime;// 测量时间
    @Column(name = "test_period")
    private String testPeriod;// 测量时间点
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;// 创建时间
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;// 更新时间
    @Column(name = "del_flag")
    private String delFlag;// 删除标示,0-未删除,1-已删除
    @Transient
    private String name;// 医生姓名
    @Transient
    private String dutyName;// 医生职称
    @Transient
    private String avatar;// 医生头像

}