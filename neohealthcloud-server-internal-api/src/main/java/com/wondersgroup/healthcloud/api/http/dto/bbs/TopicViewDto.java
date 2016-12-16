package com.wondersgroup.healthcloud.api.http.dto.bbs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicContent;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicDetailDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.VoteInfoDto;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ys on 2016/08/21.
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
    private Integer status;
    private Integer isPublish=0;

    private Integer isBest=0;//是否精华推荐
    private Integer isTop=0;//是否置顶
    private Integer topRank=0;//置顶的优先级值越大优先级越高

    private Integer isCanComment=1;//用户是否可以评论该话题//0:不可以,1:可以

    private Integer commentCount=0;//回复个数

    private Integer circleId;
    private List<Integer> topicTabs;

    private String title;
    private List<TopicContentInfo> topicContents = new ArrayList<>();
    private VoteInfoDto voteInfo;

    public TopicViewDto(TopicDetailDto detailInfo) {
        try {
            BeanUtils.copyProperties(this, detailInfo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        this.isPublish = this.status == TopicConstant.Status.WAIT_PUBLISH ? 0 : 1;
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
