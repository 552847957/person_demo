package com.wondersgroup.healthcloud.services.diabetes.dto;

import java.util.Date;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author zhongshuqing
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FollowPlanDTO {
    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date followDate;//随访日期
    
    private String doctorName;//随访医生
    
    private String hospitalName;//医疗机构名称
    
    @JsonProperty("type")
    private String followCrowedType;//随访人群种类
}
