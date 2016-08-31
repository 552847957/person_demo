package com.wondersgroup.healthcloud.jpa.entity.doctor;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by longshasha on 16/8/31.
 */
@Data
@Entity
@Table(name = "app_tb_doctor_sympcare")
public class DoctorConcerned {

    /**
     * 0:患者不使用 1：患者使用
     */
    @Id
    private String id;//疾病或症状所对应id
    private String name;//疾病
    private String type;//1:病种 2:症状
    private String module; //1:糖尿病 2:高血压 3:脑卒中 多个逗号分隔
    private String ispatientuse;
    private int sort;//排序序号
    private int tagnum;//关键词数量

    @Column(name = "delFlag")
    private String del_flag;

    @Column(name = "createBy")
    private String create_by;

    @Column(name = "createDate")
    private Date create_date;

    @Column(name = "updateBy")
    private String update_by;

    @Column(name = "updateDate")
    private Date update_date;

    @Column(name = "sourceId")
    private String source_id;
}
