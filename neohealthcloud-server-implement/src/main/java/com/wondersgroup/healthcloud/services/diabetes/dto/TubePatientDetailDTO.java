package com.wondersgroup.healthcloud.services.diabetes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * 在管人群详情
 * Created by zhuchunliu on 2016/12/9.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TubePatientDetailDTO {
    @JsonProperty("xm")
    private String name;//姓名
    @JsonProperty("xb")
    private String gender;//性别
    @JsonProperty("csrq")
    private Date birthDay;//出生年月
    @JsonProperty("zjlx")
    private String cardType;//证件类型 01:身份证
    @JsonProperty("zjhm")
    private String cardNumber;//证件号码
    @JsonProperty("mqzybm")
    private String profession;//职业
    @JsonProperty("cyzqdm")
    private String employStatus;//就业状况  31：学生 、70：无业人员、80：退（离）休人员、90：其他
    @JsonProperty("brsjhm")
    private String moblilePhone;//手机号码
    @JsonProperty("brgddh")
    private String fixedPhone;//固定号码
    @JsonProperty("lxrdh")
    private String contactPhone;//联系电话
    @JsonProperty("ywglsqdm")
    private String hospitalCode;//医院机构代码
    @JsonProperty("mqzrys")
    private String doctorName;//医生姓名
}
