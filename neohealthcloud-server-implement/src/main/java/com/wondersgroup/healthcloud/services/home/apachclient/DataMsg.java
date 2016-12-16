package com.wondersgroup.healthcloud.services.home.apachclient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by xianglinhai on 2016/12/14.
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataMsg <T>{
    @JsonProperty("code")
    private String code ;
    @JsonProperty("msg")
    private String msg;

    @JsonProperty("data")
    private T data;

    public boolean isSuccessFul(){
        if(code.equals("0")){
            return true;
        }

        return false;
    }

}
