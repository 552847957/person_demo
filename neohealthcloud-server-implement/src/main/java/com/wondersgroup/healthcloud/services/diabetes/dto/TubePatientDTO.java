package com.wondersgroup.healthcloud.services.diabetes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * 在管人群列表
 * Created by zhuchunliu on 2016/12/9.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TubePatientDTO {
    @JsonProperty("xm")
    private String name; //姓名
    @JsonProperty("xb")
    private String gender;//性别
    @JsonProperty("csrq")
    private Date birthDay;//出生日期
    @JsonProperty("qzrq")
    private Date diagnoseDate;//诊断时间
    @JsonProperty("tnbfx")
    private String diabetesType;//糖尿病类型 1、1型糖尿病,2、2型糖尿病；3、其它特殊类型糖尿病，4、妊娠糖尿病
    @JsonProperty("zjlx")
    private String cardType;//证件类型 01:身份证
    @JsonProperty("zjhm")
    private String cardNumber;//证件号码
}
