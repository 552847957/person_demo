package com.wondersgroup.healthcloud.services.bbs.criteria;

import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
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
public class TopicSearchCriteria extends BaseSearchCriteria {

    private Integer id;//根据id查询

    private List<Integer> ids;//id in 查询

    private String uid;//根据发贴人uid

    private List<String> uids;

    private String nickname;

    private String title;

    private Boolean is_mine=false;//仅获取我 和我的小号 发布的

    private Integer circle_id;//根据圈子id查询

    private List<Integer> circle_ids;//可多圈子查询 circle_id in (?)

    private int tab_id=0; //0:表示查询全部分类下面的帖子

    private Boolean is_best=false;//精华帖

    private Boolean is_top=false;//置顶帖

    private String publish_startTime;

    private String publish_endTime;

    private Integer status;//根据帖子状态进行查询

    public TopicSearchCriteria(){}

    public TopicSearchCriteria(Map<String, Object> parms){
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
            where.append(" AND topic.id=?");
            elementType.add(this.id);
        }
        if (StringUtils.isNotEmpty(this.uid)){
            where.append(" AND topic.uid=?");
            elementType.add(this.uid);
        }
        if (this.status != null){
            where.append(" AND topic.status=?");
            elementType.add(this.status);
        }else {
            where.append(" AND topic.status!="+ TopicConstant.Status.USER_DELETE);
        }
        if (this.circle_id != null && this.circle_id > 0){
            where.append(" AND topic.circle_id=?");
            elementType.add(this.circle_id);
        }
        if (this.is_best){
            where.append(" AND topic.is_best=1");
        }
        if (this.is_top){
            where.append(" AND topic.is_top=1");
        }
        if (this.tab_id > 0){
            where.append(" AND tab.tab_id=?");
            elementType.add(this.tab_id);
        }
        if (StringUtils.isNotEmpty(this.nickname)){
            where.append(" AND account.nickname like ?");
            elementType.add("%"+this.nickname+"%");
        }
        if (StringUtils.isNotEmpty(this.title)){
            where.append(" AND topic.title like ?");
            elementType.add("%"+this.title+"%");
        }
        if (StringUtils.isNotEmpty(this.publish_startTime)){
            where.append(" AND topic.create_time >= ?");
            elementType.add(this.publish_startTime + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(this.publish_endTime)){
            where.append(" AND topic.create_time <= ?");
            elementType.add(this.publish_endTime + " 23:59:59");
        }
        if (this.ids != null && !this.ids.isEmpty()){
            String idsStr = "";
            for (Integer id : this.ids){
                idsStr += "," + id;
            }
            idsStr = idsStr.substring(1);
            where.append(" AND topic.id in ("+idsStr+")");
        }

        if (this.uids != null && !this.uids.isEmpty()){
            String uidsStr = "";
            for (String uid : this.uids){
                uidsStr += ",'" + uid+"'";
            }
            uidsStr = uidsStr.substring(1);
            where.append(" AND topic.uid in ("+uidsStr+")");
        }
        if (this.circle_ids != null && !this.circle_ids.isEmpty()){
            String circleIdsStr = "";
            for (Integer circleIdTmp : this.circle_ids){
                circleIdsStr += "," + circleIdTmp;
            }
            circleIdsStr = circleIdsStr.substring(1);
            where.append(" AND topic.circle_id in (" + circleIdsStr + ")");
        }
        String whereStr = "";
        if (where.length() > 0){
            whereStr = where.substring(4);
        }
        return new JdbcQueryParams(whereStr, elementType);
    }
}
