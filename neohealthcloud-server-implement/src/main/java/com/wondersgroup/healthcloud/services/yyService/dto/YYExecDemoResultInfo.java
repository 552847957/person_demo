package com.wondersgroup.healthcloud.services.yyService.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * 医养结合 服务表单
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YYExecDemoResultInfo {

    private String title;
    private List<String> item;
    private String comment;//医生备注
    private String result;//医生处理结果

    public YYExecDemoResultInfo(){

    }
    public YYExecDemoResultInfo(String title, List<String> item){
        this.title = title;
        this.item = item;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getItem() {
        return item;
    }

    public void setItem(List<String> item) {
        this.item = item;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}

