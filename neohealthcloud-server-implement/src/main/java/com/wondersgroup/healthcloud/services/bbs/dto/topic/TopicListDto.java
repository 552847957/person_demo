package com.wondersgroup.healthcloud.services.bbs.dto.topic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.common.utils.NumberUtils;
import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Topic;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by ys on 2016/08/11.
 * @author ys
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopicListDto {

    private Integer id;
    private String title;
    private String content;//话题内容
    private Integer status = TopicConstant.AppListStatus.OK;//1:正常, 2:被删除, 3:发帖用户被禁言

    private String[] imgs;//取话题包好的图片,最多4张
    private Integer imgCount;//话题包含的图片数
    private String lastCommentTime;//最后一次回复的时间
    private String publishTime;//最后一次回复的时间
    private String commentCount="0";//回复个数
    private String favorCount="0";//点赞数

    private String uid;
    private String nickName;
    private String avatar;
    private Integer isAdmin;
    private String babyAge;
    private Integer isBest=0;//是否精华推荐
    private Integer isVote=0;//是否投票
    private Integer circleId;
    private String circleName;

    public TopicListDto(){}

    public TopicListDto(Topic topic){
        this.id = topic.getId();
        this.commentCount = NumberUtils.formatCustom1(topic.getCommentCount());
        this.favorCount = NumberUtils.formatCustom1(topic.getFavorCount());
        this.uid = topic.getUid();
        this.circleId = topic.getCircleId();
        this.title = topic.getTitle();
        this.content = topic.getIntro();
        this.imgCount = topic.getImgCount();
        if (topic.getStatus() != TopicConstant.Status.OK && topic.getStatus() != TopicConstant.Status.WAIT_VERIFY){
            this.status = TopicConstant.AppListStatus.DELETE;
        }
        this.isBest = topic.getIsBest();
        this.isVote = topic.getIsVote();
        this.lastCommentTime = DateUtils.formatDate2Custom(topic.getLastCommentTime());
        this.publishTime = DateUtils.formatDate2Custom(topic.getCreateTime());
        if (StringUtils.isNotEmpty(topic.getImgs())){
            this.imgs = topic.getImgs().split(",");
            for (int i=0; i<this.imgs.length; i++){
                int indexOf = imgs[i].lastIndexOf("?");
                if (indexOf > 0){
                    imgs[i] = imgs[i].substring(0, indexOf) + "?imageView2/1/w/400/h/400";
                }else {
                    imgs[i] = imgs[i] + "?imageView2/1/w/400/h/400";
                }
            }
        }
    }

    public void mergeUserInfo(RegisterInfo registerInfo){
        this.uid = registerInfo.getRegisterid();
        this.nickName = registerInfo.getNickname();
        this.avatar = registerInfo.getHeadphoto();
        this.isAdmin = registerInfo.getIsBBsAdmin();
        if (registerInfo.getBanStatus() == UserConstant.BanStatus.FOREVER){
            this.status = TopicConstant.AppListStatus.USER_BAN;
        }
    }

}
