package com.wondersgroup.healthcloud.services.disease.dto;

import lombok.Data;

/**
 * Created by limenghua on 2017/6/7.
 * 用于封装查询条件
 *
 * @author limenghua
 */
@Data
public class ResidentCondition {
    private String famId;//医生id
    private int page;//用于分页
    private int pageSize;//用于分页
    private Integer signed;//签约状态  1-已签约 0-未签约
    private String peopleType;//人群分类
    private String diseaseType;//慢病种类
    private String kw;//搜索关键字

    public ResidentCondition() {
    }

    public ResidentCondition(String famId, int page, int pageSize, Integer signed, String peopleType, String diseaseType, String kw) {
        this.famId = famId;
        this.page = page;
        this.pageSize = pageSize;
        this.signed = signed;
        this.peopleType = peopleType;
        this.diseaseType = diseaseType;
        this.kw = kw;
    }
}
