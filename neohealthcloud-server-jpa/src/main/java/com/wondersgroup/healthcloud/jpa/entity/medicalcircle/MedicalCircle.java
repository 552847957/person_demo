package com.wondersgroup.healthcloud.jpa.entity.medicalcircle;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;

/**
 * 医生圈信息
 * Created by sunhaidi on 2016.8.28
 */
@Entity
@Data
@Table(name = "app_tb_medicalcircle")
public class MedicalCircle extends BaseEntity {
    private String  doctorid;  //医生id
    private Date    sendtime;  //发送时间
    private String  tagid;     //标签id，医生圈标签表中的id
    private Integer type;      //1:帖子 2:病例 3:动态
    private String  content;   //内容
    private String  title;     //标题
    private Long    views;     //阅读数
    private Long    praisenum; //点赞数
    private String  transmitid;

}
