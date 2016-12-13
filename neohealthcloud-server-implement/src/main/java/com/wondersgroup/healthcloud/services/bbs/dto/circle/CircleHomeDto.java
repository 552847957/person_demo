package com.wondersgroup.healthcloud.services.bbs.dto.circle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicTab;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicTopListDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ys on 2016/8/15.
 * 圈子首页
 * @author ys
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CircleHomeDto {
    private Integer id;
    private String icon;
    private String name;
    private String description;
    private Integer userPublishStatus= UserConstant.UserCommentStatus.OK;//1:正常, 2:当前用户被禁言 3:圈子被禁用
    private Integer todayActiveCount=0;//今日活跃人数
    private Integer ifAttent=0;// 是否关注，0：未关注；1：已关注

    private List<CircleTabs> tabs = new ArrayList<>();

    private List<TopicTopListDto> topTopics;

    public void mergeCircleInfo(Circle circle){
        this.id = circle.getId();
        this.icon = circle.getIcon();
        this.name = circle.getName();
        this.description = circle.getDescription();
        if(circle.getDelFlag().equals("1")){
            this.userPublishStatus = UserConstant.UserCommentStatus.CIRCLE_BAN;
        }
    }

    public void mergeCircleTopicTab(List<TopicTab> topicTabs){
        tabs.add(new CircleTabs(0, "全部"));
        tabs.add(new CircleTabs(TopicConstant.DefaultTab.NEW_PUBLISH, "新鲜"));
        tabs.add(new CircleTabs(TopicConstant.DefaultTab.BASE_RECOMMEND, "精华"));
        if (topicTabs != null){
            for (TopicTab topicTab : topicTabs){
                tabs.add(new CircleTabs(topicTab.getId(), topicTab.getTabName()));
            }
        }
    }

    @Data
    class CircleTabs{
        Integer tabId;
        String tabName;

        public CircleTabs(Integer tabId, String tabName){
            this.tabId = tabId;
            this.tabName = tabName;
        }
    }
}
