package com.wondersgroup.healthcloud.services.bbs.criteria;

import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
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
public class CommentSearchCriteria extends BaseSearchCriteria {

    private Integer id;//根据id查询

    private List<Integer> ids;//id in 查询

    private String uid;//根据回贴人uid

    private List<String> uids;

    private String nickname;//回贴人昵称

    private Boolean is_mine=false;//仅获取我 和我的小号 回复的

    private Boolean filterUserBanForever = true;//过滤掉回帖人被永久禁言的

    private String title;

    private Integer topicStatus = TopicConstant.Status.OK;

    private String comment;

    private Integer circle_id;//根据圈子id查询

    private String publish_startTime;

    private String publish_endTime;

    private Integer status= TopicConstant.Status.OK;//根据帖子状态进行查询

    public CommentSearchCriteria(){}

    public CommentSearchCriteria(Map<String, Object> parms){
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
            where.append(" AND comment.id=?");
            elementType.add(this.id);
        }
        if (StringUtils.isNotEmpty(this.uid)){
            where.append(" AND comment.uid=?");
            elementType.add(this.uid);
        }
        if (this.status != null){
            where.append(" AND comment.status=?");
            elementType.add(this.status);
        }
        if (this.circle_id != null && this.circle_id > 0){
            where.append(" AND topic.circle_id=?");
            elementType.add(this.circle_id);
        }
        if (StringUtils.isNotEmpty(this.nickname)){
            where.append(" AND user.nickname like ?");
            elementType.add("%"+this.nickname+"%");
        }
        if (this.filterUserBanForever){
            where.append(" AND user.ban_status != " + UserConstant.BanStatus.FOREVER);
        }
        if (StringUtils.isNotEmpty(this.title)){
            where.append(" AND topic.title like ?");
            elementType.add("%"+this.title+"%");
        }
        if (this.topicStatus != null){
            where.append(" AND topic.status = ?");
            elementType.add(this.topicStatus);
        }
        if (StringUtils.isNotEmpty(this.comment)){
            where.append(" AND comment.content like ?");
            elementType.add("%"+this.comment+"%");
        }
        if (StringUtils.isNotEmpty(this.publish_startTime)){
            where.append(" AND comment.create_time >= ?");
            elementType.add(this.publish_startTime + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(this.publish_endTime)){
            where.append(" AND comment.create_time <= ?");
            elementType.add(this.publish_endTime + " 23:59:59");
        }
        if (this.ids != null && !this.ids.isEmpty()){
            String idsStr = "";
            for (Integer id : this.ids){
                idsStr += "," + id;
            }
            idsStr = idsStr.substring(1);
            where.append(" AND comment.id in ("+idsStr+")");
        }
        if (this.uids != null && !this.uids.isEmpty()){
            String uidsStr = "";
            for (String uid : this.uids){
                uidsStr += ",'" + uid+"'";
            }
            uidsStr = uidsStr.substring(1);
            where.append(" AND comment.uid in ("+uidsStr+")");
        }
        String whereStr = "";
        if (where.length() > 0){
            whereStr = where.substring(4);
        }
        return new JdbcQueryParams(whereStr, elementType);
    }
}
