package com.wondersgroup.healthcloud.services.home.dto.familyHealth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

/**
 * 个人健康 异常项
 * Created by xianglihai on 2016/12/13.
 */

@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserHealthItemDTO {
    private String name;
    private String data;
    private String hightAndLow;
    private Long testTime;
    private String testPeriod;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public String getHightAndLow() {
        return hightAndLow;
    }
    public void setHightAndLow(String hightAndLow) {
        this.hightAndLow = hightAndLow;
    }
    public Long getTestTime() {
        return testTime;
    }
    public void setTestTime(Long testTime) {
        this.testTime = testTime;
    }
    public String getTestPeriod() {
        if(testPeriod==null){
            return "";
        }
        return testPeriod;
    }
    public void setTestPeriod(String testPeriod) {
        this.testPeriod = testPeriod;
    }
    
}
