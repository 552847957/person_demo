package com.wondersgroup.healthcloud.services.bbs.dto.topic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.common.utils.NumberUtils;
import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Topic;
import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicContent;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ys on 2016/08/12.
 * @author ys
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopicDetailDto {

    private Integer id;
    private Integer status;
    private Integer userCommentStatus= UserConstant.UserCommentStatus.OK;//当前登陆用户 1:正常, 2:当前用户被禁言 3:圈子被禁用
    private String commentCount="0";//回复个数
    private String favorCount="0";//点赞数
    private Date createTime;

    private Integer isAdmin;//是否管理员发布
    private String uid;
    private Boolean isIdentify = false;
    private String nickName;
    private String avatar;
    private Integer banStatus;//发帖用户禁言状态

    private Integer isTop;//是否置顶
    private Integer topRank=0;//置顶的优先级值越大优先级越高
    private Integer isBest;//是否精华推荐
    private Integer isVoted=0;//用户是否投过票
    private Integer isCollected=0;//用户是否收藏该话题
    private Integer isFavor = 0;//用户是否点过赞

    private Integer circleId;
    private String circleName;

    private String title;
    private String intro;
    private List<TopicContentInfo> topicContents = new ArrayList<>();
    private VoteInfoDto voteInfo;

    public TopicDetailDto(Topic topic){
        this.id = topic.getId();
        this.status = topic.getStatus();
        this.title = topic.getTitle();
        this.intro = StringUtils.isEmpty(topic.getIntro()) ? topic.getTitle() : topic.getIntro();
        this.circleId = topic.getCircleId();
        this.isBest = topic.getIsBest();
        this.isTop = topic.getIsTop();
        this.topRank = topic.getTopRank();
        this.createTime = topic.getCreateTime();
        this.commentCount = NumberUtils.formatCustom1(topic.getCommentCount());
        this.favorCount = NumberUtils.formatCustom1(topic.getFavorCount());
        if (topic.getStatus() == TopicConstant.Status.FORBID_REPLY){
            this.userCommentStatus = UserConstant.UserCommentStatus.CIRCLE_BAN;
        }
    }
    public boolean isCanShowForUser(String uid, Boolean isAdmin){
        //管理员可以查看, 话题正常可以查看
        if (isAdmin || this.status == TopicConstant.Status.OK){
            return true;
        }
        //查看自己的不受限制
        if (StringUtils.isNotEmpty(uid) && uid.equals(this.uid)){
            return true;
        }
        return false;
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
        this.avatar = registerInfo.getHeadphoto();
        this.nickName = registerInfo.getNickname();
        this.isAdmin = registerInfo.getIsBBsAdmin();
        this.isIdentify = !registerInfo.getIdentifytype().equals("0");
        //发帖用户被禁言 不可以回复
        if (registerInfo.getBanStatus() != UserConstant.BanStatus.OK){
            this.userCommentStatus = UserConstant.UserCommentStatus.USER_BAN;
        }
    }

    public void mergeTopicContents(List<TopicContent> topicContentList){
        if (topicContentList != null){
            for (TopicContent topicContent : topicContentList){
                this.topicContents.add(new TopicContentInfo(topicContent));
            }
        }
    }

    @Data
    public class TopicContentInfo{
        Integer id;
        String content;
        String[] imgs;

        TopicContentInfo(TopicContent topicContent){
            this.id = topicContent.getId();
            this.content = topicContent.getContent();
            if (StringUtils.isNotEmpty(topicContent.getImgs())){
                this.imgs = topicContent.getImgs().split(",");
            }
        }
    }
}
