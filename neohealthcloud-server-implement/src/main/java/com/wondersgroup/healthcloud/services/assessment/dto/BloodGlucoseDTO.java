package com.wondersgroup.healthcloud.services.assessment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import lombok.Data;

import java.util.Date;

/**
 * Created by zhuchunliu on 2016/12/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BloodGlucoseDTO {
    @JsonProperty("flag")
    private String flag; // 0 正常 1 偏高 2 偏低
    @JsonProperty("testTime")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date date;//测量 日期
    @JsonProperty("fpgValue")
    private Double fpq;//血糖值
    @JsonProperty("testPeriod")
    private Integer interval;//区间 0 早餐前 1 早餐后 2 午餐前 3 午餐后 4 晚餐前 5 晚餐后 6睡前 7 凌晨 8 随机
    @JsonProperty("personCard")
    private String personcard;



}
