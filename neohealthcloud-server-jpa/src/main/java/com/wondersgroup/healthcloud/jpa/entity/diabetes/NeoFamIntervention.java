package com.wondersgroup.healthcloud.jpa.entity.diabetes;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by longshasha on 17/5/18.
 *
 * 测量项目中将异常的血糖和血压记录插入到异常干预表
 */
@Data
@Entity
@Table(name = "neo_fam_intervention")
public class NeoFamIntervention {

    @Id
    @Column(name = "abnormal_id")
    private String abnormalId;// 异常号
    @Column(name = "register_id")
    private String registerId;//
    @Column(name = "name")
    private String name;// 患者姓名
    @Column(name = "person_card")
    private String personCard;// 证件号码

    /**
     * 异常类型,10000-血糖首次异常,20000-血糖连续7天过高,30000-当日首次异常(血糖普通异常),
     * 40000-血压首次异常, 40001-非首次收缩压过高,40002-非首次舒张压过低,40003-3天收缩压持续升高,40004-脉压差异常
     *
     * 41000- 除以上异常之外的其他异常(普通异常 用于计算是否是3天收缩压持续升高)
     */
    @Column(name = "type")
    private String type;

    @Column(name = "warn_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date warnDate;// 异常出现日期
    @Column(name = "is_deal")
    private String isDeal;// 干预状态,1-已干预,0-未干预
    @Column(name = "remind_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date remindDate;// 异常提醒到期日
    @Column(name = "deal_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dealDate;// 处理日期
    @Column(name = "org_id")
    private String orgId;// 医疗机构代码
    @Column(name = "fam_id")
    private String famId;// 处理医生ID
    @Column(name = "message")
    private String message;// 干预内容
    @Column(name = "measure_way")
    private String measureWay;// 测量途径1:设备测量2:手动输入3:非手动输入（无mac地址设备）
    @Column(name = "test_period")
    private String testPeriod;// 测量时段
    @Column(name = "valuememo")
    private String valuememo;// 测量值文本

    @Column(name = "systolic")
    private Integer systolic;//收缩压

    @Column(name = "diastolic")
    private Integer diastolic;//舒张压

    @Column(name = "fpg_value")
    private Double fpgValue;//血糖值

    @Column(name = "doctor_intervention_id")
    private String doctorInterventionId;//医生干预表的Id

    @Column(name = "del_flag")
    private String delFlag;// 删除标记0：正常 1：删除
    @Column(name = "source_id")
    private String sourceId;// 来源代码
    @Column(name = "create_by")
    private String createBy;// 创建者
    @Column(name = "create_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;// 创建时间
    @Column(name = "update_by")
    private String updateBy;// 更新者
    @Column(name = "update_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDate;// 更新时间
}
