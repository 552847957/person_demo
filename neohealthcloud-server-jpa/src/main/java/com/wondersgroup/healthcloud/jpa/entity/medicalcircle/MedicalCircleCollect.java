package com.wondersgroup.healthcloud.jpa.entity.medicalcircle;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;

/**
 * 医生圈收藏
 * Created by sunhaidi on 2016.8.28
 */
@Entity
@Data
@Table(name = "app_tb_medicalcollect")
public class MedicalCircleCollect extends BaseEntity {
    private String  doctorid;
    private String  circleid;    //帖子id，医生圈信息表中的id
    private Integer type;        //1:医学圈 2：学苑',
    private Date    collecttime; //收藏时间

}
