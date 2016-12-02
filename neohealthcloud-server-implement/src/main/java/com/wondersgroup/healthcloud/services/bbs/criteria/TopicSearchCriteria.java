package com.wondersgroup.healthcloud.services.bbs.criteria;

import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.utils.searchCriteria.JdbcQueryParams;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用jdbc 查询话题(对话题的基本查询)
 * 查询字段为null就不查询改字段了，非null会查询
 */
@Data
public class TopicSearchCriteria implements Serializable {

    private Integer id;//根据id查询

    private List<Integer> ids;//id in 查询

    private String uid;//根据发贴人uid

    private Integer circleId;//根据圈子id查询

    private List<Integer> circleIds;//可多圈子查询 circle_id in (?)

    private int tabId=0; //0:表示查询全部分类下面的帖子

    private Integer isBest;//查询帖

    private Integer isVote;//查询投票贴

    private Integer isTop;//获取置顶,0:获取非置顶 1:获取置顶

    private Boolean filterUserBanForver=true;//是否过滤掉永久禁言的用户

    private Integer status= TopicConstant.Status.OK;//根据帖子状态进行查询

    private String orderInfo;//排序逻辑 eq: "topic.last_comment_time desc, topic.create_time desc";

    private int page=1;//分页信息
    private int pageSize=10;//pageSize设置为0 表示不分页，查询全部
    private Boolean getMoreOne = false; //为true的时候表示，在pageSize的基础上多返回一条数据,可用于判断是否有更多数据！

    public JdbcQueryParams toQueryParams(){
        StringBuffer where = new StringBuffer();
        List<Object> elementType = new ArrayList<>();
        if (this.id != null){
            where.append(" AND topic.id=?");
            elementType.add(this.id);
        }
        if (this.uid != null){
            where.append(" AND topic.uid=?");
            elementType.add(this.uid);
        }
        if (this.status != null){
            where.append(" AND topic.status=?");
            elementType.add(this.status);
        }
        if (this.circleId != null){
            where.append(" AND topic.circle_id=?");
            elementType.add(this.circleId);
        }
        if (this.isBest != null){
            where.append(" AND topic.is_best=?");
            elementType.add(this.isBest);
        }
        if (this.isVote != null){
            where.append(" AND topic.is_vote=?");
            elementType.add(this.isVote);
        }
        if (this.isTop != null){
            where.append(" AND topic.is_top=?");
            elementType.add(this.isTop);
        }
        if (this.tabId > 0){
            where.append(" AND tab.tab_id=?");
            elementType.add(this.tabId);
        }
        if (this.ids != null && !this.ids.isEmpty()){
            String idsStr = "";
            for (Integer id : this.ids){
                idsStr += "," + id;
            }
            idsStr = idsStr.substring(1);
            where.append(" AND topic.id in ("+idsStr+")");
        }
        if (this.circleIds != null && !this.circleIds.isEmpty()){
            String circleIdsStr = "";
            for (Integer circleIdTmp : this.circleIds){
                circleIdsStr += "," + circleIdTmp;
            }
            circleIdsStr = circleIdsStr.substring(1);
            where.append(" AND topic.circle_id in (" + circleIdsStr + ")");
        }
        if (this.filterUserBanForver){
            where.append(" AND user.ban_status != ? ");
            elementType.add(UserConstant.BanStatus.FOREVER);
        }
        String whereStr = "";
        if (where.length() > 0){
            whereStr = where.substring(4);
        }
        return new JdbcQueryParams(whereStr, elementType);
    }
}
