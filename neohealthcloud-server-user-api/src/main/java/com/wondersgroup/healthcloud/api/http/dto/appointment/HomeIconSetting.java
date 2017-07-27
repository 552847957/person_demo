package com.wondersgroup.healthcloud.api.http.dto.appointment;

import java.util.Date;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HomeIconSetting {
    
    private Integer id;
   
    private String serviceName;
    
    private Integer switchStatus;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;
}
