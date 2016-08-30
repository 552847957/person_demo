package com.wondersgroup.healthcloud.services.yyService.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * 医养结合 服务表单
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YYExecDemoInfo {

    private String xh;//序号
    private String fwnrid;//服务内容id
    private String exectype;///模板类别
    private String execitem;//模板项目
    private List<YYExecDemoResultInfo> execresult;//模板列表

    public String getXh() {
        return xh;
    }

    public void setXh(String xh) {
        this.xh = xh;
    }

    public String getFwnrid() {
        return fwnrid;
    }

    public void setFwnrid(String fwnrid) {
        this.fwnrid = fwnrid;
    }

    public String getExectype() {
        return exectype;
    }

    public void setExectype(String exectype) {
        this.exectype = exectype;
    }

    public String getExecitem() {
        return execitem;
    }

    public void setExecitem(String execitem) {
        this.execitem = execitem;
    }

    public List<YYExecDemoResultInfo> getExecresult() {
        return execresult;
    }

    public void setExecresult(List<YYExecDemoResultInfo> execresult) {
        this.execresult = execresult;
    }

    public void addExecresult(YYExecDemoResultInfo execresult){
        if (this.execresult == null){
            this.execresult = new ArrayList<>();
        }
        this.execresult.add(execresult);
    }
}
