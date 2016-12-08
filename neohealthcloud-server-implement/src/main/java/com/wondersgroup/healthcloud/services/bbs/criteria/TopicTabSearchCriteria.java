package com.wondersgroup.healthcloud.services.bbs.criteria;

import com.wondersgroup.healthcloud.utils.searchCriteria.JdbcQueryParams;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 使用jdbc 查询话题(对话题的基本查询)
 * 查询字段为null就不查询改字段了，非null会查询
 */
@Data
public class TopicTabSearchCriteria extends BaseSearchCriteria {

    private Integer id;//根据id查询

    private List<Integer> ids;//id in 查询

    private String tabName;

    private Integer circleId;//根据圈子id查询

    private String delFlag;//是否置顶

    public TopicTabSearchCriteria(){}

    public TopicTabSearchCriteria(Map<String, Object> parms){
        if (parms == null){
            return;
        }
        try {
            BeanUtils.populate(this, parms);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public JdbcQueryParams toQueryParams(){
        StringBuffer where = new StringBuffer();
        List<Object> elementType = new ArrayList<>();
        if (this.id != null && this.id > 0){
            where.append(" AND topic_tab.id=?");
            elementType.add(this.id);
        }
        if (StringUtils.isNotEmpty(this.tabName)){
            where.append(" AND topic_tab.tab_name like ? ");
            elementType.add("%"+this.tabName+"%");
        }
        if (circleId != null && circleId > 0){
            where.append(" AND topic_tab.circle_id=?");
            elementType.add(circleId);
        }
        if (StringUtils.isNotEmpty(this.delFlag)){
            where.append(" AND topic_tab.del_flag=?");
            elementType.add(this.delFlag);
        }
        if (this.ids != null && !this.ids.isEmpty()){
            String idsStr = "";
            for (Integer id : this.ids){
                idsStr += "," + id;
            }
            idsStr = idsStr.substring(1);
            where.append(" AND topic_tab.id in ("+idsStr+")");
        }
        String whereStr = "";
        if (where.length() > 0){
            whereStr = where.substring(4);
        }
        return new JdbcQueryParams(whereStr, elementType);
    }
}
