package com.wondersgroup.healthcloud.services.bbs.criteria;

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
public class TopicSearchCriteria extends BaseSearchCriteria {

    private Integer id;//根据id查询

    private List<Integer> ids;//id in 查询

    private String uid;//根据发贴人uid

    private List<String> uids;

    private String nickname;

    private String title;

    private Boolean isMine=false;//仅获取我 和我的小号 发布的

    private Integer circleId;//根据圈子id查询

    private List<Integer> circleIds;//可多圈子查询 circle_id in (?)

    private int tabId=0; //0:表示查询全部分类下面的帖子

    private Boolean isBest;//精华帖

    private Boolean isTop;//置顶帖

    private Boolean filterUserBanForver=true;//是否过滤掉永久禁言的用户

    private String publishStartTime;

    private String publishEndTime;

    private Integer status;//根据帖子状态进行查询
    private Integer[] statusIn;//设置status则此属性无效
    private Integer[] statusNotIn;//设置status则此属性无效

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
            if (null != statusIn && statusIn.length>0){
                String statusInStr = "";
                for (Integer status0 : statusIn){
                    statusInStr += "," + status0;
                }
                statusInStr = statusInStr.substring(1);
                where.append(" AND topic.status in ("+statusInStr+")");
            }
            if (null != statusNotIn && statusNotIn.length>0){
                String statusNotInStr = "";
                for (Integer status1 : statusNotIn){
                    statusNotInStr += "," + status1;
                }
                statusNotInStr = statusNotInStr.substring(1);
                where.append(" AND topic.status not in ("+statusNotInStr+")");
            }
        }
        if (this.circleId != null && this.circleId > 0){
            where.append(" AND topic.circle_id=?");
            elementType.add(this.circleId);
        }
        if (null != this.isBest){
            where.append(" AND topic.is_best=?");
            elementType.add(this.isBest ? 1 : 0);
        }
        if (null != this.isTop){
            where.append(" AND topic.is_top=?");
            elementType.add(this.isTop ? 1 : 0);
        }
        if (this.tabId > 0){
            where.append(" AND tab.tab_id=?");
            elementType.add(this.tabId);
        }
        if (StringUtils.isNotEmpty(this.nickname)){
            where.append(" AND user.nickname like ?");
            elementType.add("%"+this.nickname+"%");
        }
        if (StringUtils.isNotEmpty(this.title)){
            where.append(" AND topic.title like ?");
            elementType.add("%"+this.title+"%");
        }
        if (StringUtils.isNotEmpty(this.publishStartTime)){
            where.append(" AND topic.create_time >= ?");
            elementType.add(this.publishStartTime + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(this.publishEndTime)){
            where.append(" AND topic.create_time <= ?");
            elementType.add(this.publishEndTime + " 23:59:59");
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
