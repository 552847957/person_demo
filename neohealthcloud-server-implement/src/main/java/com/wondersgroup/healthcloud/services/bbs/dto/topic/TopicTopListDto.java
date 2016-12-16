package com.wondersgroup.healthcloud.services.bbs.dto.topic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Topic;
import lombok.Data;

/**
 * Created by ys on 2016/08/11.
 * @author ys
 * 置顶帖
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopicTopListDto {

    private Integer id;
    private Integer isBest=0;//是否精华推荐
    private Integer isVote=0;//是否投票
    private String title;
    private String lastCommentTime;//最后一次回复的时间
    private Integer commentCount=0;//回复个数

    public TopicTopListDto(){}

    public TopicTopListDto(Topic topic){
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.isBest = topic.getIsBest();
        this.isVote = topic.getIsVote();
        this.commentCount = topic.getCommentCount();
    }
}
