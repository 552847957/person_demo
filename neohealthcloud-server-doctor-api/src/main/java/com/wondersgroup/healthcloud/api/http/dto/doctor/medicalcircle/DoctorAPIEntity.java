package com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class DoctorAPIEntity {
    private String  doctor_id;    //医生id
    private String  avatar;       //图像
    private String  name;         //名称
    private String  hospital;     //医院名称
    private Boolean is_attention; //是否是自己
    private Long    dynamic_num;
    private Long    notecase_num; //
    private Long    attention_num; //关注数
    private Long    fans_num;     //粉丝数

}
