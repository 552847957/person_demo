package com.wondersgroup.healthcloud.utils.searchCriteria;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JdbcQueryParams implements Serializable {

    private String queryString;
    private List<Object> queryElementType;

    public JdbcQueryParams(){}

    public JdbcQueryParams(String queryString, List<Object> queryElementType){
        this.queryElementType = queryElementType;
        this.queryString = queryString;
    }
}