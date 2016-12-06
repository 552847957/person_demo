package com.wondersgroup.healthcloud.api.http.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by longshasha on 16/12/5.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AreaDTO {

    @JsonProperty("area_name")
    private String areaName;

    @JsonProperty("area_code")
    private String areaCode;

    @JsonProperty("area_upper_code")
    private String areaUpperCode;

    public AreaDTO(Map<String,Object> result) {
        this.areaName=(String) result.get("explain_memo");
        this.areaCode= (String) result.get("code");
    }
    public AreaDTO(){

    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaUpperCode() {
        return areaUpperCode;
    }

    public void setAreaUpperCode(String areaUpperCode) {
        this.areaUpperCode = areaUpperCode;
    }
}
