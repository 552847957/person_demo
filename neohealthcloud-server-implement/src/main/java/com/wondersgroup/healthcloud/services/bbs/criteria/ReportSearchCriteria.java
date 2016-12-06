package com.wondersgroup.healthcloud.services.bbs.criteria;

import com.wondersgroup.healthcloud.jpa.constant.ReportConstant;
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
public class ReportSearchCriteria extends BaseSearchCriteria {

    private Integer id;//根据id查询

    private List<Integer> ids;//id in 查询

    private String uid;//根据发贴人uid

    private String nickname;

    private String title;

    private String comment;

    private Integer circle_id;//根据圈子id查询

    private String report_startTime;

    private String report_endTime;

    private Integer status;//审核状态0待处理，1：忽略 2：已处理

    public ReportSearchCriteria(){}

    public ReportSearchCriteria(Map<String, Object> parms){
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
        if (this.id != null && this.id > 0){
            where.append(" AND report.id=?");
            elementType.add(this.id);
        }
        if (StringUtils.isNotEmpty(this.uid)){
            where.append(" AND report.target_uid=?");
            elementType.add(this.uid);
        }
        if (this.status != null){
            where.append(" AND report.status=?");
            elementType.add(this.status);
        }else {
            where.append(" AND report.status="+ ReportConstant.ReportStatus.WAIT_REVIEW);
        }
        if (this.circle_id != null && this.circle_id > 0){
            where.append(" AND topic.circle_id=?");
            elementType.add(this.circle_id);
        }
        if (StringUtils.isNotEmpty(this.nickname)){
            where.append(" AND account.nickname like ?");
            elementType.add("%"+this.nickname+"%");
        }
        if (StringUtils.isNotEmpty(this.title)){
            where.append(" AND topic.title like ?");
            elementType.add("%"+this.title+"%");
        }
        if (StringUtils.isNotEmpty(this.comment)){
            where.append(" AND comment.content like ?");
            elementType.add("%"+this.comment+"%");
        }
        if (StringUtils.isNotEmpty(this.report_startTime)){
            where.append(" AND report.create_time >= ?");
            elementType.add(this.report_startTime + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(this.report_endTime)){
            where.append(" AND report.create_time <= ?");
            elementType.add(this.report_endTime + " 23:59:59");
        }
        if (this.ids != null && !this.ids.isEmpty()){
            String idsStr = "";
            for (Integer id : this.ids){
                idsStr += "," + id;
            }
            idsStr = idsStr.substring(1);
            where.append(" AND report.id in ("+idsStr+")");
        }

        String whereStr = "";
        if (where.length() > 0){
            whereStr = where.substring(4);
        }
        return new JdbcQueryParams(whereStr, elementType);
    }
}
