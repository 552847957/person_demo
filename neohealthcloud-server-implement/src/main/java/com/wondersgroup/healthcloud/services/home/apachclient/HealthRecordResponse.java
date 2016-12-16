package com.wondersgroup.healthcloud.services.home.apachclient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 健康档案
 *  接口返回JSON封装
 * Created by jialing.yao on 2016-11-15.
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthRecordResponse<T> {
    @JsonProperty("more")
    private String more;

    @JsonProperty("content")
    private T content;//数据实体


}
