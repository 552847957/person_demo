package com.wondersgroup.healthcloud.jpa.entity.diabetes;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 随访报告
 * Created by zhuchunliu on 2017/4/18.
 */
@Data
@Entity
@Table(name = "app_tb_report_follow")
public class ReportFollow {
    @Id
    private String id;
    private String registerid;
    @Column(name="report_date")
    private Date reportDate;
    @Column(name="follow_date")
    private Date followDate;//下次随访时间
    private Integer status;//新报告是否提醒 0：否，1：是
    @Column(name = "plan_status")
    private Integer planStatus; // 随访计划定时任务是否创建 0 ：否 ， 1：是
    @Column(name = "hospital_code")
    private String hospitalCode;//随访医院编号
    @Column(name = "doctor_name")
    private String doctorName;//随访医生名称
    @Column(name = "remind_begin_date")
    private Date remindBeginDate;//随访开始提醒时间
    @Column(name="remind_end_date")
    private Date remindEndDate;//医生端停止提醒时间
    private String taskIds; // 任务id集合
    @Column(name = "del_flag")
    private String delFlag;
    @Column(name="create_date")
    private Date createDate;
    @Column(name="update_date")
    private Date updateDate;
}
