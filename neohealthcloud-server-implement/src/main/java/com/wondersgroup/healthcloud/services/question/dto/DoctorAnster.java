package com.wondersgroup.healthcloud.services.question.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

/**
 * 医生回复
 * Created by Administrator on 2017/5/18.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DoctorAnster extends  DoctorAndPations {
    private int questionType;//分类: 0 我的回复，1 患者追问  2 其他医生回复
    private String avatar; //医生头像
    private String doctorId; //医生ID
    private String doctorName;   //医生名称
    private String content;    //医生回复
    private String date;


    public DoctorAnster(int questionType, String avatar, String doctorId, String doctorName, String content, String date, Date sortDate) {
        this.questionType = questionType;
        this.avatar = avatar;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.content = content;
        this.date = date;
        super.sortDate = sortDate;

    }
}
