package com.wondersgroup.healthcloud.api.http.dto.bbs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.api.http.dto.article.ShareH5APIDTO;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.services.bbs.BadWordsService;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.VoteInfoDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicDetailDto;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ys on 2016/08/12.
 * @author ys
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopicViewDto {

    private Integer id;
    private Integer isAdmin;
    private String uid;
    private String nickName;
    private String avatar;
    private String babyAge;

    private Integer isBest;//是否精华推荐
    private Integer isVoted=0;//用户是否投过票
    private Integer isCollected=0;//用户是否收藏该话题
    private Integer isFavor = 0;//用户是否点过赞
    private Integer userCommentStatus= UserConstant.UserCommentStatus.OK;//当前登陆用户 1:正常, 2:当前用户被禁言 3:圈子被禁用
    private String createTime;

    private String commentCount="0";//回复个数
    private String favorCount="0";//点赞数

    private Integer circleId;
    private String circleName;

    private String title;
    private List<TopicDetailDto.TopicContentInfo> topicContents = new ArrayList<>();
    private VoteInfoDto voteInfo;

    private ShareH5APIDTO shareInfo;

    public TopicViewDto(TopicDetailDto detailInfo) {
        try {
            BeanUtils.copyProperties(this, detailInfo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        this.createTime = DateUtils.formatDate2Custom(detailInfo.getCreateTime());
    }

    public void dealBadWords(BadWordsService badWordsService){
        this.title = badWordsService.dealBadWords(this.title);
        if (topicContents != null){
            for (TopicDetailDto.TopicContentInfo topicContentInfo : topicContents){
                topicContentInfo.setContent(badWordsService.dealBadWords(topicContentInfo.getContent()));
            }
        }
    }

}
