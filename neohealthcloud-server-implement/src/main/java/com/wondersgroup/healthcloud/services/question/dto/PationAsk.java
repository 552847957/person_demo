package com.wondersgroup.healthcloud.services.question.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

/**
 * 病人追问
 * Created by Administrator on 2017/5/18.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PationAsk extends  DoctorAndPations{

    private int questionType;//1,//患者追问
    private String sex; //性别
    private int age;//年龄
    private String content; //追问内容
    private String contentImgs; //追问图标
    private String date;

    public PationAsk() {
    }

    public PationAsk(int questionType, String sex, int age, String content, String contentImgs, String date,Date sortDate) {
        this.questionType = questionType;
        this.sex = sex.equals("1")?"男":"女";
        this.age = age;
        this.content = content;
        this.contentImgs = contentImgs;
        this.date = date;
        super.sortDate = sortDate;

    }
}
