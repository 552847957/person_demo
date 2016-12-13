package com.wondersgroup.healthcloud.services.diabetes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 筛查报告
 * Created by zhuchunliu on 2016/12/9.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportScreeningDTO {
    @JsonProperty("filterDate")
    private Date reportDate; // 筛查日期
    @JsonProperty("hospitalId")
    private String hospitalCode;//医疗机构代码
    @JsonProperty("fpbg")
    private BigDecimal peripheralBloodSugar;//空腹末梢血糖
    @JsonProperty("fbg")
    private BigDecimal venousBloodSugar;//空腹静脉血糖
    @JsonProperty("ogtt")
    private BigDecimal dgtt;//dgtt2h静脉血糖
    @JsonProperty("filterResult")
    private String reportResult;//筛查结果  1:糖尿病、2：糖尿病前期、3：血糖正常
}
