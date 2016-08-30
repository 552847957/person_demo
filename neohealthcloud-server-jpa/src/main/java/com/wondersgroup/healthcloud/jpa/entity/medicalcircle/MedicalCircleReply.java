package com.wondersgroup.healthcloud.jpa.entity.medicalcircle;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;

/**
 * 医生圈评论回复
 * Created by sunhaidi on 2016.8.28
 */
@Entity
@Data
@Table(name = "app_tb_medicalcirclereply")
public class MedicalCircleReply extends BaseEntity {
    private String communityid; //评论id，医生圈评论表中的id
    private String doctorid;    //医生id
    private String replyid;     //上级回复id
    private Date   discusstime; //回复时间
    private String content;     //内容
}
