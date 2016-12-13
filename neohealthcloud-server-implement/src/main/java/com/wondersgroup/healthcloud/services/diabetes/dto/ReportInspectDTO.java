package com.wondersgroup.healthcloud.services.diabetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * 检查报告
 * Created by zhuchunliu on 2016/12/9.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportInspectDTO {
    @JsonProperty("bgdh")
    private String reportNum;//报告单号
    @JsonProperty("yljgdm")
    private String hospitalCode;//医疗机构代码
    @JsonProperty("bgrq")
    private String reportDate;//报告日期
    @JsonProperty("bgdlb")
    private String reportType;//报告类型
}
