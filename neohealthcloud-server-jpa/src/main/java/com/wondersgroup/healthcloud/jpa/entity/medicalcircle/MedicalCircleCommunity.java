package com.wondersgroup.healthcloud.jpa.entity.medicalcircle;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;

/**
 * 医生圈评论
 * Created by sunhaidi on 2016.8.28
 */
@Entity
@Data
@Table(name = "app_tb_medicalcirclecommunity")
public class MedicalCircleCommunity extends BaseEntity {
    private String  doctorid;
    private String  circleid;    //帖子id，医生圈信息表中的id'
    private Date    discusstime; //评论时间'
    private String  content;     //内容
    private Boolean isreply;     //是否有回复 0：无 1：有',

}
