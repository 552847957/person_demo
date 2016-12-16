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
public class UserSearchCriteria extends BaseSearchCriteria {

    private String uid;//根据发贴人uid

    private List<String> uids;

    private String nickname;

    private String phone;

    private String name;

    private Integer banStatus;//根据帖子状态进行查询

    private Boolean isIdentify;//是否实名认证

    private String delFlag="0";

    public UserSearchCriteria(){}

    public UserSearchCriteria(Map<String, Object> parms){
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
        if (StringUtils.isNotEmpty(this.uid)){
            where.append(" AND user.registerid=?");
            elementType.add(this.uid);
        }
        if (StringUtils.isNotEmpty(this.nickname)){
            where.append(" AND user.nickname like ?");
            elementType.add("%"+this.nickname+"%");
        }
        if (StringUtils.isNotEmpty(this.phone)){
            where.append(" AND user.regmobilephone=?");
            elementType.add(this.phone);
        }
        if (StringUtils.isNotEmpty(this.name)){
            where.append(" AND user.name like ?");
            elementType.add("%"+this.name+"%");
        }
        if (null != this.banStatus){
            where.append(" AND user.ban_status=?");
            elementType.add(this.banStatus);
        }
        if (StringUtils.isNotEmpty(this.delFlag)){
            where.append(" AND user.del_flag=?");
            elementType.add(this.delFlag);
        }
        if (null != this.isIdentify){
            if (this.isIdentify){
                where.append(" AND user.identifytype<>'0'");
            }else {
                where.append(" AND user.identifytype='0'");
            }
        }
        String whereStr = "";
        if (where.length() > 0){
            whereStr = where.substring(4);
        }
        return new JdbcQueryParams(whereStr, elementType);
    }
}
