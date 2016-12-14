package com.wondersgroup.healthcloud.services.bbs.criteria;

import com.wondersgroup.healthcloud.utils.searchCriteria.JdbcQueryParams;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 使用jdbc 查询话题(对话题的基本查询)
 * 查询字段为null就不查询改字段了，非null会查询
 */
@Data
public class AdminVestSearchCriteria extends BaseSearchCriteria {

    private String adminUid;//根据发贴人uid

    private String nickname;

    public AdminVestSearchCriteria(){}

    public AdminVestSearchCriteria(Map<String, Object> parms){
        if (parms == null){
            return;
        }
        try {
            BeanUtils.populate(this, parms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JdbcQueryParams toQueryParams(){
        StringBuffer where = new StringBuffer();
        List<Object> elementType = new ArrayList<>();
        if (StringUtils.isNotEmpty(this.adminUid)){
            where.append(" AND vest.admin_uid=?");
            elementType.add(this.adminUid);
        }
        if (StringUtils.isNotEmpty(this.nickname)){
            where.append(" AND user.nickname like ?");
            elementType.add("%"+this.nickname+"%");
        }
        String whereStr = "";
        if (where.length() > 0){
            whereStr = where.substring(4);
        }
        return new JdbcQueryParams(whereStr, elementType);
    }
}
