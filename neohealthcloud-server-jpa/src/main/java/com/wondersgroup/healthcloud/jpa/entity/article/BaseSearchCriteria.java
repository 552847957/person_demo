package com.wondersgroup.healthcloud.jpa.entity.article;

import java.io.Serializable;

public class BaseSearchCriteria implements Serializable {

    private Integer id;
    /**
     * eq: " start_time>233 "
     */
    private String extendSqlWhere;
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private Integer pageOffset = 0;
    private String orderInfo = "";

    public Integer getPageIndex() {
        return pageIndex != null ? pageIndex : 1;
    }
    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }
    public Integer getPageSize() {
        return pageSize != null ? pageSize : 10;
    }
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    public String getOrderInfo() {
        return orderInfo;
    }
    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExtendSqlWhere() {
        return extendSqlWhere;
    }

    public void setExtendSqlWhere(String extendSqlWhere) {
        this.extendSqlWhere = extendSqlWhere;
    }

    public void setPageOffset(Integer offset){
        this.pageOffset = offset;
    }

    public Integer getPageOffset(){
        if (this.pageOffset == 0){
            this.pageOffset = (this.getPageIndex() - 1) * this.getPageSize();
        }

        return this.pageOffset;
    }
}
