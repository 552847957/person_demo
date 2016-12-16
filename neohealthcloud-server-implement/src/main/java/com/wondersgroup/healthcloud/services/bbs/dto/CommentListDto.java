package com.wondersgroup.healthcloud.services.bbs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.jpa.constant.CommentConstant;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Comment;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Topic;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import lombok.Data;


/**
 * Created by ys on 2016/08/12.
 * @author ys
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentListDto {

    private Integer topicId;
    private String topicUid;
    private String topicTitle;

    private Integer commentId;
    private Integer status = CommentConstant.AppListStatus.OK;//1:正常, 2:被删除, 3:用户被禁言
    private String uid;
    private String nickName;
    private String avatar;
    private String babyAge;
    private Integer floor;

    private Integer isAdmin;//是否为管理员回复
    private Integer isOwner;//是否为楼主回复
    private String content;//回复内容

    private String createTime;//回复时间

    private ReferCommentInfo referCommentInfo;

    public void mergeTopicInfo(Topic topic){
        this.topicId = topic.getId();
        this.topicUid = topic.getUid();
        this.topicTitle = topic.getTitle();
    }

    public String getContent(){
        if (this.status != CommentConstant.AppListStatus.OK){
            this.content = "";
        }
        return this.content;
    }

    public void mergeCommentInfo(Comment comment){
        this.topicId = comment.getTopicId();
        this.commentId = comment.getId();
        this.uid = comment.getUid();
        this.floor = comment.getFloor();
        this.content = comment.getContent();
        if (comment.getStatus() != CommentConstant.Status.OK){
            this.status = CommentConstant.AppListStatus.DELETE;
        }
        this.isOwner = comment.getIsOwner();
        this.createTime = DateUtils.formatDate2Custom(comment.getCreateTime());
    }

    public void mergeCommentUserInfo(RegisterInfo registerInfo){
        if (null == registerInfo){
            return;
        }
        this.avatar = registerInfo.getHeadphoto();
        this.nickName = registerInfo.getNickname();
        this.isAdmin = registerInfo.getIsBBsAdmin();
        if (registerInfo.getBanStatus() == UserConstant.BanStatus.FOREVER){
            this.status = CommentConstant.AppListStatus.USER_BAN;
        }
    }

    public void mergeReferCommentInfo(Comment referComment, RegisterInfo referUserInfo){
        if (null == referComment){
            return;
        }
        ReferCommentInfo referCommentInfo = new ReferCommentInfo();
        referCommentInfo.setReferUid(referComment.getUid());
        referCommentInfo.setContent(referComment.getContent());
        referCommentInfo.setCreateTime(DateUtils.formatDate2Custom(referComment.getCreateTime()));
        referCommentInfo.setFloor(referComment.getFloor());
        if (referComment.getStatus() != CommentConstant.Status.OK){
            referCommentInfo.setStatus(CommentConstant.AppListStatus.DELETE);
        }
        referCommentInfo.setIsOwner(referComment.getIsOwner());
        if (referUserInfo != null){
            referCommentInfo.setReferUid(referUserInfo.getRegisterid());
            referCommentInfo.setReferNickName(referUserInfo.getNickname());
            referCommentInfo.setAvatar(referUserInfo.getHeadphoto());
            if (referUserInfo.getBanStatus() == UserConstant.BanStatus.FOREVER){
                referCommentInfo.setStatus(CommentConstant.AppListStatus.USER_BAN);
            }
            referCommentInfo.setIsAdmin(referUserInfo.getIsBBsAdmin()==1 ? 1 : 0);
        }
        this.referCommentInfo=referCommentInfo;
    }

    @Data
    public class ReferCommentInfo{
        String referUid;
        String referNickName;
        String avatar;
        Integer status= CommentConstant.AppListStatus.OK;//1:正常, 2:被删除, 3:用户被禁言
        Integer floor=0;
        Integer isAdmin;//是否为管理员回复
        Integer isOwner;//是否为楼主回复
        String content;//回复内容
        String createTime;//回复时间

        public String getContent(){
            if (this.status != CommentConstant.AppListStatus.OK){
                this.content = "";
            }
            return this.content;
        }
    }
}
