package com.wondersgroup.healthcloud.services.bbs.dto.topic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.common.utils.NumberUtils;
import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Topic;
import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicContent;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.services.bbs.dto.VoteInfoDto;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ys on 2016/08/12.
 * h5 显示用
 * @author ys
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopicH5ViewDto {

    private Integer id;
    private Integer isAdmin;
    private String uid;
    private String nickName;
    private String avatar;
    private String babyAge;

    private Integer isBest;//是否精华推荐
    private Integer isVoted=0;//用户是否投过票
    private Integer isCollected=0;//用户是否收藏该话题
    private Integer userCommentStatus= UserConstant.UserCommentStatus.OK;//当前登陆用户 1:正常, 2:当前用户被禁言 3:圈子被禁用
    private String createTime;

    private String commentCount="0";//回复个数

    private Integer circleId;
    private String circleName;

    private String title;
    private List<TopicContentInfo> topicContents = new ArrayList<>();
    private VoteInfoDto voteInfo;


    public void mergeTopicInfo(Topic topic, List<TopicContent> topicContents){
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.circleId = topic.getCircleId();
        this.isBest = topic.getIsBest();
        this.createTime = DateUtils.formatDate2Custom(topic.getCreateTime());
        this.commentCount = NumberUtils.formatCustom1(topic.getCommentCount());
        this.mergeTopicContents(topicContents);
        if (topic.getStatus().intValue() == TopicConstant.Status.FORBID_REPLY){
            this.userCommentStatus = UserConstant.UserCommentStatus.CIRCLE_BAN;
        }
    }

    public void mergeCircleInfo(Circle circle){
        this.circleName = circle.getName();
        if (circle.getDelFlag().equals("1")){
            //帖子所在圈子被删除 用户不能回复
            this.userCommentStatus = UserConstant.UserCommentStatus.CIRCLE_BAN;
        }
    }

    public void mergeUserInfo(RegisterInfo registerInfo){
        this.uid = registerInfo.getRegisterid();
        this.nickName = registerInfo.getNickname();
        this.avatar = registerInfo.getHeadphoto();
        this.isAdmin = registerInfo.getIsBBsAdmin();
        //发帖用户被禁言 不可以回复
        if (registerInfo.getBanStatus().intValue() != UserConstant.BanStatus.OK){
            this.userCommentStatus = UserConstant.UserCommentStatus.USER_BAN;
        }
    }

    private void mergeTopicContents(List<TopicContent> topicContentList){
        if (topicContentList != null){
            for (TopicContent topicContent : topicContentList){
                this.topicContents.add(new TopicContentInfo(topicContent));
            }
        }
    }

    @Data
    public class TopicContentInfo{
        String content;
        String[] imgs;

        TopicContentInfo(TopicContent topicContent){
            this.content = topicContent.getContent();
            if (StringUtils.isNotEmpty(topicContent.getImgs())){
                this.imgs = topicContent.getImgs().split(",");
            }
        }
    }
}
