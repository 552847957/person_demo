package com.wondersgroup.healthcloud.api.http.dto.family;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 家庭个人信息DTO
 * Created by sunhaidi on 2016年12月7日
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FamilyMemberInfoDTO {
    private String  nikcName;
    private String  relationName;
    private int     age;
    private boolean isVerification;     //是否实名
    private String  doctorRecord;       //就医记录
    private String  familyDoctor;       //家庭医生
    private String  doctorRecordValue;
    private String  riskEvaluate;       //风险评估
    private String  riskEvaluateValue;
    private String  healthQuestion;     //中医体质识别
    private String  healthQuestionValue;
    private String  diabetes;           //糖尿病
    private String  diabetesValue;
    private String  jogging;            //记步
    private String  joggingValue;
    private String  bmi;
    private String  bmiValue;
    private String  bloodPressure;      //血压
    private String  bloodPressureValue;
    private String  bloodSugar;         //血糖
    private String  bloodSugarValue;
}
