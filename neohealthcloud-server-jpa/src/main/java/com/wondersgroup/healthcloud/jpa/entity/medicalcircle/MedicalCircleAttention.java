package com.wondersgroup.healthcloud.jpa.entity.medicalcircle;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;

/**
 * 医生圈关注
 * Created by sunhaidi on 2016.8.28
 */
@Entity
@Data
@Table(name = "app_tb_medicalattention")
public class MedicalCircleAttention extends BaseEntity {
    private String doctorid;      //医生id
    private String concernedid;   //被关注医生id
    private Date   attentiontime; //关注时间

}
