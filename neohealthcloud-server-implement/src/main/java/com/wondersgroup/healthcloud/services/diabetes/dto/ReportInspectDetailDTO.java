package com.wondersgroup.healthcloud.services.diabetes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 检查报告详情
 * Created by zhuchunliu on 2016/12/9.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportInspectDetailDTO {
    @JsonProperty("jczbmc")
    private String itemName;//项目名称
    @JsonProperty("jczbjg")
    private String itemResult;//项目结果
    @JsonProperty("jldw")
    private String itemUnit;//项目单位
    @JsonProperty("ckz")
    private String itemReference;//项目参考值
}
