package com.wondersgroup.healthcloud.services.bbs.criteria;

import com.wondersgroup.healthcloud.utils.searchCriteria.JdbcQueryParams;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 使用jdbc 查询话题(对话题的基本查询)
 * 查询字段为null就不查询改字段了，非null会查询
 */
@Data
public abstract class BaseSearchCriteria implements Serializable {

    protected String orderInfo;//排序逻辑 eq: "topic.last_comment_time desc, topic.create_time desc";
    protected int page=1;//分页信息
    protected int pageSize=10;//pageSize设置为0 表示不分页，查询全部
    protected Boolean getMoreOne = false; //为true的时候表示，在pageSize的基础上多返回一条数据,可用于判断是否有更多数据！

    public abstract JdbcQueryParams toQueryParams();

    public String getOrderInfo(){
        return !StringUtils.isEmpty(this.orderInfo) ? " order by " + this.orderInfo : "";
    }

    public String getLimitInfo(){
        String limitStr;
        if (this.getPageSize() > 0){
            int offset = (this.getPage()-1)*this.getPageSize();
            int size = this.getGetMoreOne() ? this.pageSize +1 : this.pageSize;
            limitStr = String.format(" limit %d,%d ", offset, size);
        }else {
            limitStr = "";
        }
        return limitStr;
    }
}
